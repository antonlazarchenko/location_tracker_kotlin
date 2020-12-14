package com.alazar.service

import android.location.Location
import android.util.Log
import com.alazar.service.data.LocationData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class DbFirebaseModel constructor(private val userId: String) {

    fun interface FirebaseCallback {
        fun onReady(locationData: ArrayList<LocationData>)
    }

    companion object {
        private const val TAG = "DbFirebaseModel"

        private const val FIELD_TIMESTAMP = "timestamp"
    }

    private val db = Firebase.firestore

    fun save(locationData: LocationData) {

        GlobalScope.launch(Dispatchers.IO) {

            db.collection(userId).document(locationData.timestamp.toString())
                .set(locationData.getMap())
                .addOnSuccessListener {
                    Log.d(TAG, "FIREBASE: DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "FIREBASE: Error adding document", e)
                }
        }
    }

    fun saveLocation(location: Location) {
        val locationData = LocationData(location)
        save(locationData)
    }

    fun getLocationForDay(timestamp: Long, callback: FirebaseCallback) {

        GlobalScope.launch(Dispatchers.IO) {

            val gson = Gson()
            val resultList = ArrayList<LocationData>()
            val nextDayTimestamp = timestamp + 24 * 60 * 60 * 1000

            Log.d(TAG, timestamp.toString())
            Log.d(TAG, nextDayTimestamp.toString())

            db.collection(userId)
                .whereGreaterThanOrEqualTo(FIELD_TIMESTAMP, timestamp)
                .whereLessThanOrEqualTo(FIELD_TIMESTAMP, nextDayTimestamp)
                .orderBy(FIELD_TIMESTAMP)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        Log.d(TAG, task.result.toString())
                        for (document in task.result) {
                            Log.d(
                                TAG,
                                "FIREBASE: DocumentSnapshot " + document.id + " => " + document.data
                            )

                            val locationData =
                                gson.fromJson(document.data.toString(), LocationData::class.java)
                            resultList.add(locationData)
                        }

                        callback.onReady(resultList)
                    } else {
                        Log.d(TAG, "FIREBASE: Task failed: ", task.exception)
                    }
                }.addOnFailureListener { e: Exception? ->
                    Log.d(TAG, "FIREBASE: Error getting documents: ", e)
                }
        }
    }


    fun getLocationUpdates(timestamp: Long, callback: FirebaseCallback) {
        val gson = Gson()
        val resultList = ArrayList<LocationData>()

        GlobalScope.launch(Dispatchers.IO) {

            db.collection(userId)
                .whereGreaterThan(FIELD_TIMESTAMP, timestamp)
                .addSnapshotListener(EventListener addSnapshotListener@{ snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        Log.d(TAG, "Current data: " + snapshot.documents)
                        for (document in snapshot.documents) {
                            val locationData = gson.fromJson(
                                document.data.toString(),
                                LocationData::class.java
                            )
                            resultList.add(locationData)
                            callback.onReady(resultList)
                        }
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                })
        }
    }
}