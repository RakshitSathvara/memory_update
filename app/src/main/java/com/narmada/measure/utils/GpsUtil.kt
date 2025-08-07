package com.narmada.measure.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

/**
 * Created by Rajnit Gajera on 01,June,2023
 */
class GpsUtil(private val context: Context) {

    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val mSettingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private val locationRequest: LocationRequest = LocationRequest.Builder(LOCATION_TIME_INTERVAL)
        .setMinUpdateIntervalMillis(LOCATION_FAST_TIME_INTERVAL)
        .setMinUpdateDistanceMeters(LOCATION_DISTANCE_INTERVAL)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setWaitForAccurateLocation(true)
        .build()
    private val mLocationSettingsRequest: LocationSettingsRequest

    init {

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        mLocationSettingsRequest = builder.build()

        //**************************
        builder.setAlwaysShow(true) //this is the key ingredient
        //**************************
    }

    // method for turn on GPS
    fun turnGPSOn(onGpsListener: (isGPSOn: Boolean) -> Unit) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGpsListener(true)
        } else {
            mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((context as Activity)) {
                    //  GPS is already enable, callback GPS status through listener
                    onGpsListener(true)
                }
                .addOnFailureListener(context) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(context, GPS_REQUEST)
                        } catch (sie: SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings."
                            Log.e(TAG, errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    companion object {
        private const val TAG = "GpsUtils"

        const val GPS_REQUEST = 1001
        const val LOCATION_REQUEST = 1000

        const val LOCATION_TIME_INTERVAL: Long = 2 * 1000 // In milliseconds
        const val LOCATION_FAST_TIME_INTERVAL: Long = 1 * 1000 // In milliseconds
        const val LOCATION_DISTANCE_INTERVAL: Float = 1.0f // In meters

        const val ONE_GUNTHA_IN_METERS: Double = 101.17140961 // square meters
        const val ONE_ACRE_IN_GUNTHA: Double = 40.0          // guntha

        fun isValidLocation(location: Location): Boolean {
            if(location.accuracy < 100.0) {
                return  true
            }
            return false
        }
    }
}