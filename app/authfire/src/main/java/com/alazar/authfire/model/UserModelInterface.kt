package com.alazar.authfire.model


interface UserModelInterface {

    fun interface AuthEmailCallback {
        fun onReady(isAuthorized: Boolean)
    }

    fun interface AuthPhoneCallback {
        fun onReady(phoneAuthState: Int)
    }


    fun isAuthorized(): Boolean

    fun getUserId(): String?

    fun signOut()


    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: AuthEmailCallback
    )

    fun signInUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: AuthEmailCallback
    )


    fun verifyPhoneNumberWithCode(code: String, callback: AuthPhoneCallback)

    fun resendVerificationCode(phoneNumber: String, callback: AuthPhoneCallback)

    fun startPhoneNumberVerification(phoneNumber: String, callback: AuthPhoneCallback)

}