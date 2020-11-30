package com.alazar.service.data

import android.location.Location
import com.google.gson.annotations.SerializedName

class LocationData constructor(location: Location) {
    @SerializedName("timestamp")
    var timestamp: Long = 0

    @SerializedName("longitude")
    var longitude: String

    @SerializedName("latitude")
    var latitude: String

    init {
        timestamp = location.time
        latitude = location.latitude.toString()
        longitude = location.longitude.toString()
    }

    fun getMap(): HashMap<String, Any?> {
        val map = HashMap<String, Any?>()
        map["latitude"] = latitude
        map["longitude"] = longitude
        map["timestamp"] = timestamp
        return map
    }
}