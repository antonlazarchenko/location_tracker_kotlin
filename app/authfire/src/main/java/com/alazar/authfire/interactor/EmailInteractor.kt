package com.alazar.authfire.interactor

import com.alazar.authfire.model.UserModel
import com.alazar.authfire.model.UserUI
import com.alazar.base.core.Interactor
import javax.inject.Inject

class EmailInteractor @Inject constructor(val model: UserModel) : Interactor {

    private var userUI = UserUI()

    fun getUserForAuthentication(callback: InteractorCallback) {
        userUI.id = model.getUserId()
        userUI.isAuthenticated = model.isAuthenticated()
        callback.onReady(userUI)
    }

    fun signIn(
        email: String,
        password: String,
        callback: InteractorCallback
    ) {
        model.signInUserWithEmailAndPassword(
            email,
            password
        ) { isAuth: Boolean, id: String? ->
            userUI.isAuthenticated = isAuth
            userUI.id = id
            callback.onReady(userUI)
        }
    }

    fun createAccount(
        email: String,
        password: String,
        callback: InteractorCallback
    ) {
        model.createUserWithEmailAndPassword(
            email,
            password
        ) { isAuth: Boolean, id: String? ->
            userUI.isAuthenticated = isAuth
            userUI.id = id
            callback.onReady(userUI)
        }
    }
}