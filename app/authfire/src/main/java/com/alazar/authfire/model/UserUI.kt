package com.alazar.authfire.model

data class UserUI(
    var isAuthenticated: Boolean = false,
    var id: String? = null,
    var status: Int? = null
)
