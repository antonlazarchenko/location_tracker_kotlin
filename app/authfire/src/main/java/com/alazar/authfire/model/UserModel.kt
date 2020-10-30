package com.alazar.authfire.model

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class UserModel : UserModelInterface {

    companion object {
        private val TAG = UserModel::class.simpleName
    }

    private val auth: FirebaseAuth = Firebase.auth

    private var mVerificationInProgress = false
    private var mVerificationId: String? = ""
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken

    override fun isAuthorized(): Boolean {

        return auth.uid != null
    }

    override fun getUserId(): String? {
        return auth.uid
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: UserModelInterface.AuthEmailCallback
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    callback.onReady(true)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    callback.onReady(false)
                }
            }
    }

    override fun signInUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: UserModelInterface.AuthEmailCallback
    ) {
        Log.d(TAG, "signIn:$email")

         auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    callback.onReady(true)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    callback.onReady(false)
                }
            }
    }

    override fun startPhoneNumberVerification(
        phoneNumber: String,
        callback: UserModelInterface.AuthPhoneCallback
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(PhoneAuthVerificationHandler(callback))
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    override fun resendVerificationCode(
        phoneNumber: String,
        callback: UserModelInterface.AuthPhoneCallback
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(PhoneAuthVerificationHandler(callback))
            .setForceResendingToken(mResendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyPhoneNumberWithCode(
        code: String,
        callback: UserModelInterface.AuthPhoneCallback
    ) {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(mVerificationId!!, code)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    callback.onReady(PhoneAuthState.STATE_SIGNIN_SUCCESS)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    callback.onReady(PhoneAuthState.STATE_SIGNIN_FAILED)
                }
            }
    }


    inner class PhoneAuthVerificationHandler(private val callback: UserModelInterface.AuthPhoneCallback) :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$phoneAuthCredential")
            mVerificationInProgress = false
            callback.onReady(PhoneAuthState.STATE_VERIFY_SUCCESS)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            mVerificationInProgress = false
            if (e is FirebaseAuthInvalidCredentialsException) {
                callback.onReady(PhoneAuthState.STATE_VERIFY_FAILED) //Invalid phone number
            } else if (e is FirebaseTooManyRequestsException) {
                callback.onReady(PhoneAuthState.STATE_VERIFY_FAILED_SMS_QUOTA) // SMS quota has been exceeded
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")
            mVerificationId = verificationId
            mResendToken = token
            callback.onReady(PhoneAuthState.STATE_CODE_SENT)
        }

    }

}