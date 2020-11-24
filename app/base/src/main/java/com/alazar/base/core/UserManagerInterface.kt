package com.alazar.base.core

interface UserManagerInterface {

    fun isAuthorized(): Boolean

    fun getUserId(): String?

    fun signOut()
}