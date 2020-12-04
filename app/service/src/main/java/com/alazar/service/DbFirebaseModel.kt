package com.alazar.service

import android.location.Location
import android.util.Log
import com.alazar.service.data.LocationData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DbFirebaseModel constructor(private val userId: String) {

    companion object {
        private const val TAG = "DbFirebaseAdapter"
    }

    private val db = Firebase.firestore

    private fun save(locationData: LocationData) {
        db.collection(userId).document(locationData.timestamp.toString())
            .set(locationData.getMap())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "FIREBASE: DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "FIREBASE: Error adding document", e)
            }
    }

    fun saveLocation(location: Location) {
        val locationData = LocationData(location)
        save(locationData)
    }

}