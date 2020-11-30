package com.alazar.authfire.model

interface UserManagerInterface {

    fun isAuthorized(): Boolean

    fun getUserId(): String?

    fun signOut()
}