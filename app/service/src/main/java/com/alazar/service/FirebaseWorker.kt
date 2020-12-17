package com.alazar.service

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.base.util.NetworkUtil
import com.alazar.service.data.LocationData
import com.alazar.service.di.ServiceApp
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val TAG = "FirebaseWorker"
    }

    @Inject
    lateinit var user: UserManagerInterface

    private var dbFirebaseModel: DbFirebaseModel

    init {
        ServiceApp.appComponent.inject(this)

        dbFirebaseModel = DbFirebaseModel(user.getUserId().toString())
    }

    override fun doWork(): Result {
        Log.d(TAG, "+++++ WORK START")

        Hawk.init(applicationContext).build()

        val isConnected =
            NetworkUtil(applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).isConnected()

        if (Hawk.count() > 0 && isConnected) {
            GlobalScope.launch(Dispatchers.IO) {
                val locations = withContext(Dispatchers.IO) { getLocationsList() }

                locations.forEach {
                    dbFirebaseModel.save(it.value)
                    Log.d(
                        TAG,
                        "WORK SAVE TO FIREBASE " + it.value.toString()
                    )
                }
            }
        } else {
            Log.d(TAG,"WORK - Network Disabled")
        }
        return Result.success()
    }

    private fun getLocationsList(): HashMap<Long, LocationData> {
        val locations = HashMap<Long, LocationData>()
        val count = Hawk.count()

        var i: Long = 0

        while (i != count) {
            if (Hawk.contains(i.toString())) {
                locations[i] = Hawk.get(i.toString())
                i++
            }
        }

        Hawk.deleteAll()
        return locations
    }


    override fun onStopped() {
        super.onStopped()
        Log.d(TAG, "+++++ WORK STOP")
    }
}