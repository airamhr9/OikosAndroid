package com.example.oikos.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat


// A simple Android based positioning implementation.
class PlatformPositioningProvider(private val context: Context) : LocationListener {
    private var locationManager: LocationManager? = null

    private var platformLocationListener: PlatformLocationListener? = null

    interface PlatformLocationListener {
        fun onLocationUpdated(location: Location?)
    }

    override fun onLocationChanged(location: Location) {
        if (platformLocationListener != null) {
            platformLocationListener!!.onLocationUpdated(location)
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.OUT_OF_SERVICE -> Log.d(LOG_TAG, "PlatformPositioningProvider status: OUT_OF_SERVICE")
            LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.d(LOG_TAG, "PlatformPositioningProvider status: TEMPORARILY_UNAVAILABLE")
            else -> Log.d(LOG_TAG, "PlatformPositioningProvider status: UNKNOWN")
        }
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(LOG_TAG, "PlatformPositioningProvider enabled.")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(LOG_TAG, "PlatformPositioningProvider disabled.")
    }

    fun startLocating(locationCallback: PlatformLocationListener?) : Boolean{
        if (platformLocationListener != null) {
            throw RuntimeException("Please stop locating before starting again.")
        }
        if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Positioning permissions denied.")
            println("SIN PERMISOS")
            return false
        }
        platformLocationListener = locationCallback
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL_IN_MS.toLong(), 1f, this)
            return true
        } else if (locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL_IN_MS.toLong(), 1f, this)
            return true
        } else {
            Log.d(LOG_TAG, "Positioning not possible.")
            stopLocating()
            return false
        }
    }

    private fun stopLocating() {
        if (locationManager == null) {
            return
        }
        locationManager!!.removeUpdates(this)
        platformLocationListener = null
    }

    companion object {
        val LOG_TAG = PlatformPositioningProvider::class.java.name
        const val LOCATION_UPDATE_INTERVAL_IN_MS = 100
    }
}
