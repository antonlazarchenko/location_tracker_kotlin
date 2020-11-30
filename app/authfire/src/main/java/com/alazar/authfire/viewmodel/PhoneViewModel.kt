package com.alazar.authfire.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alazar.authfire.model.UserModel
import javax.inject.Inject

class PhoneViewModel @Inject constructor(val model: UserModel) : ViewModel()  {

    private val status = MutableLiveData<Int>()

    fun getStatus(): LiveData<Int> {
        return status
    }

    fun startVerification(phone: String, activity: Activity) {
        model.startPhoneNumberVerification(phone, activity) {
            status.postValue(it)
        }
    }

    fun resendVerification(phone: String, activity: Activity) {
        model.resendVerificationCode(phone, activity) {
            status.postValue(it)
        }
    }

    fun verify(code: String) {
        model.verifyPhoneNumberWithCode(code) {
            status.postValue(it)
        }
    }
}