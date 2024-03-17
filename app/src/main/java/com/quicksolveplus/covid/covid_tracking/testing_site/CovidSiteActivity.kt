package com.quicksolveplus.covid.covid_tracking.testing_site

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_tracking.testing_site.models.CovidSite
import com.quicksolveplus.covid.covid_tracking.testing_site.models.CovidSiteItem
import com.quicksolveplus.covid.covid_tracking.testing_site.viewmodel.CovidSiteViewModel
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityCovidSiteBinding
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.utils.dismissQSProgress
import com.quicksolveplus.utils.showQSProgress
import com.quicksolveplus.utils.toast


class CovidSiteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityCovidSiteBinding
    private var latitude = 0.0
    private var longitude = 0.0
    private var latTemp = 0.0
    private var lngTemp = 0.0
    private val viewModel: CovidSiteViewModel by viewModels()
    private var mMap: GoogleMap? = null
    private var covidSiteList: ArrayList<CovidSiteItem> = arrayListOf()
    private var sheetBehavior: BottomSheetBehavior<*>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCovidSiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
    }


    private fun initUI() {
        Log.e("TAG", "initUI: "+Preferences.instance!!.latitude )
        Log.e("TAG", "initUI: "+Preferences.instance!!.longitude )
        checkForLocationPermission()
        initMapData()
        sheetBehavior = BottomSheetBehavior.from<LinearLayoutCompat>(binding.layoutBottomSheet.bottomSheetLayout)
        sheetBehavior!!.isDraggable = false
        binding.apply {
            toolBar.tvTitle.text = getString(R.string.str_testing_site)
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            ivMapSearch.setOnClickListener {
                if (etCounty.text.toString().isNotEmpty() || etZip.text.toString().isNotEmpty()) {
                    getCovidSiteData()
                }
            }
        }
    }

    private fun initMapData() {
        val mapFragment = supportFragmentManager.findFragmentById(binding.googleMap.id) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    private fun setObservers() {
        viewModel.responseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(this)
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(this, it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(this, "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }


    private fun getCovidSiteData() {
        val country = binding.etCounty.text.toString()
        val zip = binding.etZip.text.toString()
        val body = RequestParameters.forGetTestingSites(country, zip)
        viewModel.getTestingSites(body = body)
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getTestingSites -> {
                if (success.data is CovidSite) {
                    for (i in 0 until success.data.size) {
                        covidSiteList.add(success.data[i])
                    }
                    loadCovidCenters(success.data)
                }
            }
        }
    }

    private fun loadCovidCenters(covidSite: CovidSite) {
        mMap!!.clear()
        for ((tag, testingSiteModel) in covidSite.withIndex()) {
            val marker = LatLng(
                testingSiteModel.SiteLatitude.toDouble(),
                testingSiteModel.SiteLongitude.toDouble()
            )
            mMap!!.addMarker(MarkerOptions().position(marker))!!.tag = tag
        }

        val currentLatLng =
            LatLng(covidSite[0].SiteLatitude.toDouble(), covidSite[0].SiteLongitude.toDouble())
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
    }

    private fun checkForLocationPermission() {
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .check()
    }


    private var permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {}
        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        latitude = getLastKnownLocation()!!.latitude
//        longitude = getLastKnownLocation()!!.longitude


//        if (latitude != 0.0 && longitude != 0.0) {
//            val currentLatLng = LatLng(latitude, longitude)
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
//            googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Marker"))
//        } else {
//
//        }
        mMap!!.setOnMarkerClickListener(markerListener)
    }


    @SuppressLint("SetTextI18n")
    private var markerListener = OnMarkerClickListener { marker ->

        latTemp = covidSiteList[marker.tag as Int].SiteLatitude.toDouble()
        lngTemp = covidSiteList[marker.tag as Int].SiteLongitude.toDouble()

        binding.layoutBottomSheet.apply {
            tvSiteName.text = covidSiteList[(marker.tag as Int)].SiteName
            tvNumber.text = covidSiteList[(marker.tag as Int)].PhoneNumber
            llNumber.visibility = View.VISIBLE
            tvWebsite.text = covidSiteList[(marker.tag as Int)].AgencyUrl
            llLink.visibility = View.VISIBLE
            tvAddress.text = covidSiteList[(marker.tag as Int)].FullAddress
            tvDrive.text = getString(R.string.str_drive_thru) + " " + covidSiteList[(marker.tag as Int)].HasDriveThrough
            tvAppoinment.text = getString(R.string.str_appointment_only) + " " + covidSiteList[(marker.tag as Int)].IsAppointmentOnly
            tvTestType.text = getString(R.string.str_type_of_test) + " " + covidSiteList[(marker.tag as Int)].TestType
            tvSameResult.text = getString(R.string.str_same_day_results) + " " + covidSiteList[(marker.tag as Int)].HasSameDayResults
            tvHours.text = getString(R.string.str_hours_of_operation) + " " + covidSiteList[(marker.tag as Int)].OperationHours
            sheetBehavior!!.isDraggable = true
            if (sheetBehavior!!.state != BottomSheetBehavior.STATE_HALF_EXPANDED) {
                sheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
        false
    }

    private fun getLastKnownLocation(): Location? {
        var l: Location? = null
        val mLocationManager =
            applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                l = mLocationManager.getLastKnownLocation(provider)
            }
            if (l == null) {
                continue
            }
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }
        return bestLocation
    }


}