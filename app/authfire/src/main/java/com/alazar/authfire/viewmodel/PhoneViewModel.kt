package com.alazar.authfire.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alazar.authfire.interactor.PhoneInteractor
import com.alazar.authfire.model.UserUI
import javax.inject.Inject

class PhoneViewModel @Inject constructor(val interactor: PhoneInteractor) : ViewModel()  {

    private val user = MutableLiveData<UserUI>()

    fun getStatus(): LiveData<UserUI> {
        return user
    }

    fun startVerification(phone: String, activity: Activity) {
        interactor.startVerification(phone, activity) {
            user.postValue(it)
        }
    }

    fun resendVerification(phone: String, activity: Activity) {
        interactor.resendVerification(phone, activity) {
            user.postValue(it)
        }
    }

    fun verify(code: String) {
        interactor.verify(code) {
            user.postValue(it)
        }
    }
}