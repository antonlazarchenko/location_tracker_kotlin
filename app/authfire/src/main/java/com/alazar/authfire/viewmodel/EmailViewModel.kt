package com.alazar.authfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alazar.authfire.model.UserModel
import javax.inject.Inject


class EmailViewModel @Inject constructor(userModel: UserModel) : ViewModel() {

    var model: UserModel = userModel

    private val isAuthenticated = MutableLiveData<Boolean>()

    fun getIsAuthenticated(): LiveData<Boolean> {
        return isAuthenticated
    }

    fun signIn(
        email: String,
        password: String
    ) {
        model.signInUserWithEmailAndPassword(email, password) {
            isAuthenticated.postValue(it)
        }
    }

    fun createAccount(
        email: String,
        password: String
    ) {
        model.createUserWithEmailAndPassword(email, password) {
            isAuthenticated.postValue(it)
        }
    }

}