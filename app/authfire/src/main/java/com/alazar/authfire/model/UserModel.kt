package com.alazar.authfire.model

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserModel @Inject constructor() : UserManagerInterface {

    fun interface AuthPhoneCallback {
        fun onReady(isAuthorized: Boolean, userId: String?, phoneAuthState: Int)
    }

    companion object {
        private val TAG = UserModel::class.simpleName
    }

    private val auth: FirebaseAuth = Firebase.auth

    private var mVerificationId: String? = ""
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken

    override fun isAuthenticated(): Boolean = auth.uid != null

    override fun getUserId(): String? {
        return auth.uid
    }

    override fun signOut() {
        auth.signOut()
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onReady: (isAuthenticated: Boolean, userId: String?) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    onReady(true, getUserId())
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onReady(false, getUserId())
                }
            }
    }

    fun signInUserWithEmailAndPassword(
        email: String,
        password: String,
        onReady: (isAuthenticated: Boolean, userId: String?) -> Unit
    ) {
        Log.d(TAG, "signIn:$email")

         auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    onReady(true, getUserId())
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    onReady(false, getUserId())
                }
            }
    }


    fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity,
        callback: AuthPhoneCallback
    ) {
        auth.signOut()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(PhoneAuthVerificationHandler(callback))
            .setActivity(activity)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        callback: AuthPhoneCallback
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(PhoneAuthVerificationHandler(callback))
            .setForceResendingToken(mResendToken)
            .setActivity(activity)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithCode(
        code: String,
        callback: AuthPhoneCallback
    ) {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(mVerificationId!!, code)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    callback.onReady(isAuthenticated(), getUserId(), PhoneAuthState.STATE_SIGNIN_SUCCESS)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    callback.onReady(isAuthenticated(), getUserId(), PhoneAuthState.STATE_SIGNIN_FAILED)
                }
            }
    }


    inner class PhoneAuthVerificationHandler(private val callback: AuthPhoneCallback) :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$phoneAuthCredential")
            callback.onReady(isAuthenticated(), getUserId(), PhoneAuthState.STATE_VERIFY_SUCCESS)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                callback.onReady(isAuthenticated(), getUserId(), PhoneAuthState.STATE_VERIFY_FAILED) //Invalid phone number
            } else if (e is FirebaseTooManyRequestsException) {
                callback.onReady(isAuthenticated(), getUserId(), PhoneAuthState.STATE_VERIFY_FAILED_SMS_QUOTA) // SMS quota has been exceeded
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")
            mVerificationId = verificationId
            mResendToken = token
            callback.onReady(isAuthenticated(), getUserId(), PhoneAuthState.STATE_CODE_SENT)
        }

    }

}