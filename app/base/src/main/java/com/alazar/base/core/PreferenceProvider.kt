package com.alazar.base.core

interface PreferenceProvider {
    fun saveServiceStatus(status: Boolean)
    fun getServiceStatus(): Boolean
}