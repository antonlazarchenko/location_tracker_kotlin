package com.alazar.base.core

interface NetworkProvider {
    fun isConnected(): Boolean
    fun runNetworkConnectionMonitor()
}