package com.narmada.measure.screens.khetarmapni.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityMapKhetarMapniPhotoBinding
import com.narmada.measure.screens.khetarmapni.model.KhetarMapniRequest
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.MapUtil
import org.json.JSONException
import org.json.JSONObject


class MapKhetarMapniPhotoActivity : AppCompatActivity(), View.OnClickListener {

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val binding by lazy { ActivityMapKhetarMapniPhotoBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    var mapniData: KhetarMapniRequest? = null
    private lateinit var googleMap: GoogleMap

    private var runningPathPolygon: Polygon? = null

    private val locationList = arrayListOf<LatLng>()
    private val markerList = arrayListOf<Marker>()

    private lateinit var mapJson: JSONObject

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mapniData = intent.getSerializable("data", KhetarMapniRequest::class.java)
        binding.relativeSubmit.setOnClickListener(this)
        toolbarBinding.ivBack.setOnClickListener(this)
        toolbarBinding.txtTitle.setText(R.string.farm_measure_guj)
        binding.mapView.onCreate(savedInstanceState)

        try {
            mapJson = JSONObject(mapniData!!.polygonJson.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        dialog.show()

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

        binding.mapView.getMapAsync { map ->
            googleMap = map
            googleMap.uiSettings.isRotateGesturesEnabled = false

            try {
                // loading custom map style for clear background...
                val success: Boolean = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this,
                        R.raw.google_map_style
                    )
                )
                if (!success) {
                    Log.e("MapsActivity", "Style parsing failed.")
                }
            } catch (exception: Exception) {
                Log.e(TAG, "MapsActivity: ", exception)
            }

            // set default view of gujarat...
            val cameraUpdate: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(22.3039, 70.8022), 10f)
            googleMap.moveCamera(cameraUpdate)
            googleMap.isBuildingsEnabled = false
            googleMap.setOnMapLoadedCallback {
                mapInitialized()
            }
        }

    }

    private fun mapInitialized() {
        val locationArray = mapJson.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0)
        for (i in 0 until locationArray.length()) {
            val location = locationArray.getJSONArray(i)
            locationList.add(LatLng(location.getDouble(1), location.getDouble(0)))
        }

        if (locationList.isNotEmpty()) {
            if (runningPathPolygon == null) {
                runningPathPolygon = MapUtil.addPolygon(googleMap, locationList)
            } else {
                MapUtil.updatePolygon(runningPathPolygon!!, locationList)
            }

            val distanceArray =
                mapJson.getJSONArray("features").getJSONObject(0).getJSONArray("coordinateDistances")
            for (i in 0 until distanceArray.length()) {
                val distance = distanceArray.getJSONArray(i)

                val displayString = "${distance.getDouble(2)}m (${i + 1})"
                val distanceMarker = MapUtil.addTextMarker(
                    this,
                    googleMap,
                    locationList[distance.getInt(1)],
                    displayString,
                    textColor = Color.BLACK
                )
                markerList.add(distanceMarker!!)
            }

            MapUtil.showPolygonInCenter(googleMap, locationList, false)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            MapUtil.captureMapScreenshot(googleMap) { status, imgFilePath ->
                dialog.dismiss()

                if (status && imgFilePath != null) {
                    Log.d(
                        TAG,
                        "captureMapScreenshot() called with: status = $status, imgFilePath = $imgFilePath"
                    )
                    startNextActivity(imgFilePath)
                } else {
                    Toast.makeText(
                        this@MapKhetarMapniPhotoActivity,
                        "Map Screenshot Failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }, 1000)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.relativeSubmit.id -> {
//                startNextActivity(mapJson, "")
            }
        }
    }

    private fun startNextActivity(imgFilePath: String) {
        val intent = Intent(this, ReviewKhetarMapniActivity::class.java)
        intent.putExtra("data", mapniData)
        intent.putExtra("mapImage", imgFilePath)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    companion object {
        private const val TAG = "MapKhetarMapniImageActivity"
    }

}