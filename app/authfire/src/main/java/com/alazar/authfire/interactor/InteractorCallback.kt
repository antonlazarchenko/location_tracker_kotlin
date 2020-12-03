package com.alazar.authfire.interactor

import com.alazar.authfire.model.UserUI

fun interface InteractorCallback {
    fun onReady(userUI: UserUI)
}
