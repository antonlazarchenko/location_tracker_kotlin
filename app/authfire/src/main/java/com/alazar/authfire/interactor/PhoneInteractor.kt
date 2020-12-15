package com.alazar.authfire.interactor

import android.app.Activity
import com.alazar.authfire.model.UserModel
import com.alazar.authfire.model.UserUI
import com.alazar.base.core.Interactor
import javax.inject.Inject

class PhoneInteractor @Inject constructor(val model: UserModel) : Interactor {

    private var userUI = UserUI()

    fun startVerification(phone: String, activity: Activity, onReady: (userUI: UserUI) -> Unit) {
        model.startPhoneNumberVerification(phone, activity) { isAuth: Boolean, id: String?, status: Int ->
            userUI.status = status
            onReady(userUI)
        }
    }

    fun resendVerification(phone: String, activity: Activity, onReady: (userUI: UserUI) -> Unit) {
        model.resendVerificationCode(phone, activity) { isAuth: Boolean, id: String?, status: Int ->
            userUI.status = status
            onReady(userUI)
        }
    }

    fun verify(code: String, onReady: (userUI: UserUI) -> Unit) {
        model.verifyPhoneNumberWithCode(code) { isAuth: Boolean, id: String?, status: Int ->
            userUI.id = id
            userUI.status = status
            userUI.isAuthenticated = isAuth
            onReady(userUI)
        }
    }
}