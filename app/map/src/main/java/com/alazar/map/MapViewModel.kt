package com.alazar.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.map.di.MapApp
import com.alazar.service.DbFirebaseModel
import com.alazar.service.data.LocationData
import javax.inject.Inject


class MapViewModel @Inject constructor() : ViewModel()  {

    @Inject
    lateinit var user: UserManagerInterface

    private var model: DbFirebaseModel

    private val locations = MutableLiveData<ArrayList<LocationData>>()

    init {
        MapApp.appComponent.inject(this)

        model = DbFirebaseModel(user.getUserId().toString())
    }

    fun getLocations(): LiveData<ArrayList<LocationData>> {
        return locations
    }

    fun getLocationForDay(timestamp: Long) {
        model.getLocationForDay(timestamp) {
            locations.postValue(it)
        }
    }

    fun getLocationUpdates(timestamp: Long) {
        model.getLocationUpdates(timestamp) {
            locations.postValue(it)
        }
    }

}