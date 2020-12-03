package com.alazar.authfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alazar.authfire.interactor.EmailInteractor
import com.alazar.authfire.model.UserUI
import javax.inject.Inject

class EmailViewModel @Inject constructor(private val interactor: EmailInteractor) : ViewModel() {

    private val user = MutableLiveData<UserUI>()

    fun getUser(): LiveData<UserUI> {
        return user
    }

    fun signIn(
        email: String,
        password: String
    ) {
        interactor.signIn(email, password) {
            user.postValue(it)
        }
    }

    fun createAccount(
        email: String,
        password: String
    ) {
        interactor.createAccount(email, password) {
            user.postValue(it)
        }
    }

}