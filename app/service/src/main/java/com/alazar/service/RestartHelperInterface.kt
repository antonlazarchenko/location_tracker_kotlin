package com.alazar.service


interface RestartHelperInterface {
    fun restartService(serviceClass: Class<*>?)
    fun sendRestartBroadcast()
    fun checkPermission(): Boolean
}
