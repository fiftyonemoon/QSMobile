package com.quicksolveplus.qsbase

import android.Manifest.permission
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.utils.toast

abstract class LocationBase : Base(), LocationListener {

    private val updateInterval = 5000 // 5 sec
    private val fastestInterval = 5000 // 5 sec
    private val minAutoDisplacement = 1 // 1 meters
    private var mLastLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {
                mLastLocation = location
                displayLocation()
            }
        }
    }

    private fun displayLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    mLastLocation = location
                    onLocationUpdate()
                }
            }
        }
    }

    private fun onLocationUpdate() {
        if (mLastLocation == null)
            return

        Preferences.instance?.latitude = mLastLocation?.latitude.toString()
        Preferences.instance?.longitude = mLastLocation?.longitude.toString()

        getLocation(mLastLocation)
    }

    abstract fun getLocation(mLastLocation: Location?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        checkPlayServices()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    open fun checkPlayServices() {
        val api = GoogleApiAvailability.getInstance()
        val code = api.isGooglePlayServicesAvailable(this@LocationBase)
        if (code == ConnectionResult.SUCCESS) {
            createLocationRequest()
            startLocationUpdates()
        } else {
            toast(this@LocationBase, api.getErrorString(code))
        }
    }

    private fun createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, updateInterval.toLong())
                    .apply {
                        setMinUpdateDistanceMeters(minAutoDisplacement.toFloat())
                        setMinUpdateIntervalMillis(fastestInterval.toLong())
                    }
                    .build()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationRequest?.let { locationRequest ->
                fusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    private fun init() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        getPermissionForLocation()
    }

    private fun getPermissionForLocation() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                showGPSSettingsAlertIfRequired(this@LocationBase)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                toast(this@LocationBase, getString(R.string.str_qsClock_no_lat_long_recorded))
            }

        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setRationaleMessage(getString(R.string.request_location_permission))
            .setDeniedMessage(getString(R.string.on_denied_permission))
            .setGotoSettingButtonText(getString(R.string.setting))
            .setPermissions(permission.ACCESS_FINE_LOCATION)
            .check()
    }

    private fun showGPSSettingsAlertIfRequired(activity: Activity) {
        if (!isGPSSettingEnable(activity)) {
            showGPSSettingsAlert(activity)
        }
    }

    open fun showGPSSettingsAlert(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(activity.getString(R.string.location_msg))
            .setCancelable(true)
            .setPositiveButton(
                activity.getString(R.string.str_setting)
            ) { dialog: DialogInterface?, id: Int ->
                activity.startActivity(
                    Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                )
            }

        val alert = builder.create()
        alert.show()
    }

    private fun isGPSSettingEnable(activity: Activity): Boolean {
        val manager: LocationManager =
            activity.getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        displayLocation()
    }

    private fun isLocationPermissionGranted(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}