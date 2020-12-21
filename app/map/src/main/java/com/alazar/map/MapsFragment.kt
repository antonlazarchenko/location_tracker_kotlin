package com.alazar.map

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.base.util.CalendarUtil
import com.alazar.base.util.LocationPermissionUtil
import com.alazar.base.util.NetworkUtil
import com.alazar.map.databinding.FragmentMapsBinding
import com.alazar.map.di.MapComponentProvider
import com.alazar.service.data.LocationData
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class MapsFragment : Fragment(), View.OnClickListener {

    @Inject
    lateinit var userManager: UserManagerInterface

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MapViewModel

    private lateinit var map: GoogleMap

    private var myLastLocation = MutableLiveData<LatLng>()

    private lateinit var progressBar: ProgressBar

    private lateinit var binding: FragmentMapsBinding

    private var dateMillis by Delegates.notNull<Long>()
    private var todayMillis by Delegates.notNull<Long>()

    private lateinit var dialog: DatePickerDialog

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.setMinZoomPreference(8.0f)
        map.setMaxZoomPreference(20.0f)

        todayMillis = CalendarUtil.getTodayStartTimeMillis()
        dateMillis = CalendarUtil.getTodayStartTimeMillis()

        viewModel.getLocationForDay(dateMillis)

        listenForUpdates()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapComponentProvider.getComponent().inject(this)

        binding = FragmentMapsBinding.inflate(inflater, container, false)

        initCalendar()

        progressBar = binding.progressBar

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MapViewModel::class.java)

        viewModel.getLocations().observe(requireActivity(), {
            onResult(it)
        })

        binding.fab.setOnClickListener(this)
        binding.logoutFab.setOnClickListener(this)

        NetworkUtil.runNetworkConnectionMonitor(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun onResult(locationDataArrayList: ArrayList<LocationData>) {

        hideProgressBar()

        if (locationDataArrayList.size > 0) {

            setMarkers(locationDataArrayList, dateMillis == todayMillis)
        } else {
            Toast.makeText(requireContext(), R.string.nothing_to_show, Toast.LENGTH_SHORT)
                .show()

            detectMyLastLocation()
        }

    }

    private fun setMarkers(locationDataArrayList: ArrayList<LocationData>, maxZoom: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {

            val markerOptions = MarkerOptions()

            for (location in locationDataArrayList) {
                val marker = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
                markerOptions.title(Date(location.timestamp).toString())
                map.addMarker(markerOptions.position(marker))
            }

            val lastPosition = LatLng(
                locationDataArrayList[locationDataArrayList.size - 1].latitude.toDouble(),
                locationDataArrayList[locationDataArrayList.size - 1].longitude.toDouble()
            )

            val cameraPosition = CameraPosition.Builder()
                .target(lastPosition)
                .tilt(30f)

            if (maxZoom) cameraPosition.zoom(18f) else cameraPosition.zoom(12f)

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))
        }
    }

    private fun listenForUpdates() {
        if (todayMillis == dateMillis)
            viewModel.getLocationUpdates(todayMillis)
    }


    private val listener =
        OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
            showProgressBar()

            binding.fab.visibility = View.VISIBLE

            dateMillis = CalendarUtil.getStartTimeForDayMillis(year, month, day)

            viewModel.getLocationForDay(dateMillis)
        }

    private fun initCalendar() {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone(getString(R.string.gmt_timezone))

        dialog = DatePickerDialog(
            requireContext(), listener,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.fab.id -> {
                dialog.show()
            }

            binding.logoutFab.id -> {
                val alertDialog: AlertDialog =
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.logout_confirmation)
                        .setPositiveButton(R.string.yes) { alert, id ->
                            userManager.signOut()

                            requireActivity().supportFragmentManager.beginTransaction()
                                .detach(this).commit()
                            requireActivity().recreate()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { alert: DialogInterface, id: Int ->
                            alert.dismiss()
                        }.create()
                alertDialog.show()
            }
        }
    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }


    @SuppressLint("MissingPermission")
    private fun detectMyLastLocation() {

        myLastLocation.observe(requireActivity(), {
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(it.latitude, it.longitude))
                .tilt(30f)
            map.clear()
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))
        })

        GlobalScope.launch(Dispatchers.Default) {
            val fusedLocationProviderClient = getFusedLocationProviderClient(requireActivity())

            LocationPermissionUtil.requestLocationPermission(requireActivity())
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null)
                    myLastLocation.postValue(LatLng(it.latitude, it.longitude))
            }
        }

    }

}