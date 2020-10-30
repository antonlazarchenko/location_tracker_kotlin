package com.alazar.authfire.model

object PhoneAuthState {
    const val STATE_INITIALIZED = 1
    const val STATE_CODE_SENT = 2
    const val STATE_VERIFY_FAILED = 3
    const val STATE_VERIFY_FAILED_SMS_QUOTA = 4
    const val STATE_VERIFY_SUCCESS = 5
    const val STATE_SIGNIN_FAILED = 6
    const val STATE_SIGNIN_SUCCESS = 7
}