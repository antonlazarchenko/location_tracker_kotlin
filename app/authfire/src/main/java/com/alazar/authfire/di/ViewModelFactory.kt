package com.alazar.authfire.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alazar.authfire.model.UserModel
import com.alazar.authfire.viewmodel.EmailAuthViewModel
import com.alazar.authfire.viewmodel.PhoneAuthViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor() : ViewModelProvider.Factory {
    @Inject
    lateinit var model: UserModel

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        AuthApp().getComponent().inject(this)

        if (modelClass.isAssignableFrom(EmailAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmailAuthViewModel(model) as T
        }
        if (modelClass.isAssignableFrom(PhoneAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhoneAuthViewModel(model) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}