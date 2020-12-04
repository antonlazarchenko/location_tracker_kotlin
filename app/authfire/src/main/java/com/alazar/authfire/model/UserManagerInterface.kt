package com.alazar.authfire.model

interface UserManagerInterface {

    fun isAuthenticated(): Boolean

    fun getUserId(): String?

    fun signOut()
}