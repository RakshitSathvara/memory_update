package com.narmada.measure.screens.khetarmapni_offline.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Circle
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityMapKhetarMapniOfflineBinding
import com.narmada.measure.screens.khetarmapni_offline.model.OfflineMapniModel
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.DNDUtil
import com.narmada.measure.utils.GpsUtil
import com.narmada.measure.utils.MapBoxUtil
import com.narmada.measure.utils.MapBoxUtil.readToString
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class MapKhetarMapniOfflineActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityMapKhetarMapniOfflineBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    var mapniData: OfflineMapniModel? = null
    private val mapView by lazy { binding.mapView }
    private lateinit var mapBoxMap: MapboxMap
    private lateinit var mapBoxStyle: Style
    private lateinit var mapBoxSymbolManager: SymbolManager
    private var userPositionMarker: Symbol? = null
    private lateinit var mapBoxCircleManager: CircleManager
    private var userAccuracyCircle: Circle? = null

    private lateinit var gpsUtil: GpsUtil
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var lastLatLng: Location? = null

    private val locationList = arrayListOf<LatLng>()
    private var isTrackingStarted = false
    private var isTrackingCompleted = false

    private val finalLocationList = JSONArray()
    private val finalDistanceList = JSONArray()
    private var finalAreaInSqMeters: String? = null
    private var finalAreaInAcre: String? = null

    // for rajkot
//    private val tempLocationList = arrayListOf(
//        LatLng(22.301661, 70.790017),
//        LatLng(22.301989, 70.789974),
//        LatLng(22.302455, 70.789931),
//        LatLng(22.302742, 70.789846),
//        LatLng(22.303160, 70.789883),
//        LatLng(22.303819, 70.789737),
//        LatLng(22.303915, 70.790739),
//        LatLng(22.303985, 70.791668),
//        LatLng(22.303393, 70.791701),
//        LatLng(22.303067, 70.791718),
//        LatLng(22.302549, 70.791734),
//        LatLng(22.302112, 70.791749),
//        LatLng(22.301825, 70.791645),
//        LatLng(22.301746, 70.790938),
//        LatLng(22.301644, 70.790244),
//    )

    private val tempLocationList = arrayListOf(
        LatLng(23.0310185, 72.5624002),
        LatLng(23.0311492, 72.5624995),
        LatLng(23.031157, 72.5625777),
        LatLng(23.0311236, 72.5626366),
        LatLng(23.0312158, 72.5623361),
        LatLng(23.0312217, 72.5623525),
        LatLng(23.0310835, 72.5622564),
        LatLng(23.0310983, 72.5623199),
        LatLng(23.0310257, 72.5622716),
        LatLng(23.0310088, 72.5622172),
        LatLng(23.0310167, 72.5621948),
        LatLng(23.031029, 72.5621701),
        LatLng(23.0310484, 72.5621602),
        LatLng(23.0310818, 72.562173),
        LatLng(23.0311099, 72.5621963),
        LatLng(23.0311389, 72.5622144),
        LatLng(23.0311605, 72.5622233),
        LatLng(23.031179, 72.5622363),
        LatLng(23.0311943, 72.5622476),
        LatLng(23.0312435, 72.5622515),
        LatLng(23.0312573, 72.5622711),
        LatLng(23.0312492, 72.5623035),
        LatLng(23.0312446, 72.562334),
        LatLng(23.031241, 72.562362),
        LatLng(23.0312397, 72.5623953),
        LatLng(23.0312416, 72.5624256),
        LatLng(23.0312409, 72.5624534),
        LatLng(23.03124, 72.562481),
        LatLng(23.0312446, 72.5625077),
        LatLng(23.0312594, 72.562541),
        LatLng(23.0312681, 72.5625671),
        LatLng(23.031258, 72.5625912),
        LatLng(23.0312418, 72.562613),
        LatLng(23.0312234, 72.5626316),
        LatLng(23.031198, 72.5626407),
        LatLng(23.0311735, 72.5626379),
        LatLng(23.0311494, 72.5626357),
        LatLng(23.0311375, 72.5626387)
    )

//     for Rajpardi
//    private val tempLocationList = arrayListOf(
//        LatLng(21.766443, 73.236672),
//        LatLng(21.766682, 73.236532),
//        LatLng(21.766891, 73.236452),
//        LatLng(21.767038, 73.236374),
//        LatLng(21.767227, 73.236299),
//        LatLng(21.767426, 73.236771),
//        LatLng(21.767626, 73.237232),
//        LatLng(21.767506, 73.237382),
//        LatLng(21.767367, 73.237479),
//        LatLng(21.767237, 73.237500),
//        LatLng(21.767013, 73.237296),
//        LatLng(21.766804, 73.237050),
//        LatLng(21.766634, 73.236857),
//    )

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mapView.onCreate(savedInstanceState)

        mapniData = intent.getSerializable("data", OfflineMapniModel::class.java)
        binding.relativeSubmit.setOnClickListener(this)
        toolbarBinding.ivBack.setOnClickListener(this)
        toolbarBinding.txtTitle.setText(R.string.offline_khetar_mapni)

        gpsUtil = GpsUtil(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(GpsUtil.LOCATION_TIME_INTERVAL)
            .setMinUpdateIntervalMillis(GpsUtil.LOCATION_FAST_TIME_INTERVAL)
            .setMinUpdateDistanceMeters(GpsUtil.LOCATION_DISTANCE_INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setWaitForAccurateLocation(true)
            .build()

        mapView.getMapAsync { map ->
            mapBoxMap = map
            mapBoxMap.uiSettings.isRotateGesturesEnabled = false

            showMbTilesMap(MapBoxUtil.getFileFromAssets(this, MBTILES_FILE_NAME))
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null && GpsUtil.isValidLocation(location)) {
                        Log.e(TAG, "onLocationResult() called with: locationResult = $location")

                        binding.relativeSubmit.visibility = View.VISIBLE

                        moveCameraToNewLocation(location)
                        verifyLocationForPolyline(location)

                        if (!isTrackingCompleted) {
                            if (userAccuracyCircle == null) {
                                userAccuracyCircle = MapBoxUtil.drawLocationAccuracyCircle(
                                    mapBoxCircleManager,
                                    location
                                )
                            } else {
                                userAccuracyCircle?.latLng =
                                    LatLng(location.latitude, location.longitude)
                                userAccuracyCircle?.circleRadius = location.accuracy
                                mapBoxCircleManager.update(userAccuracyCircle)
                            }

                            if (userPositionMarker == null) {
                                userPositionMarker =
                                    MapBoxUtil.drawUserPositionMarker(mapBoxSymbolManager, location)
                            } else {
                                userPositionMarker!!.latLng =
                                    LatLng(location.latitude, location.longitude)
                                mapBoxSymbolManager.update(userPositionMarker)
                            }
                        }

                    }
                }
            }
        }

    }

    private fun showMbTilesMap(mbtilesFile: File) {
        val styleJsonInputStream = assets.open(STYLE_FILE_NAME)

        //Creating a new file to which to copy the json content to
        val dir = File(filesDir.absolutePath)
        val styleFile = File(dir, STYLE_FILE_NAME)
        //Copying the original JSON content to new file
        MapBoxUtil.copyStreamToFile(styleJsonInputStream, styleFile)

        val bounds = MapBoxUtil.getLatLngBounds(mbtilesFile)
        val minZoomLevel = MapBoxUtil.getMinZoom(mbtilesFile).toDouble()
        val maxZoomLevel = MapBoxUtil.getMaxZoom(mbtilesFile).toDouble()

        Log.d("showMBTilesFile", "bounds = $bounds")
        Log.d("showMBTilesFile", "minZoomLevel = $minZoomLevel")
        Log.d("showMBTilesFile", "maxZoomLevel = $maxZoomLevel")
        Log.d(
            "showMBTilesFile",
            "northeast = ${bounds.northEast}, southEast = ${bounds.southEast}, northWest = ${bounds.northWest}, southWest = ${bounds.southWest}"
        )

        //Replacing placeholder with uri of the mbtiles file
        val newFileStr = styleFile.inputStream().readToString()
            .replace("___FILE_URI___", "mbtiles:///${mbtilesFile.absolutePath}")

        Log.d("showMBTilesFile", "new_file_str = $newFileStr")

        //Writing new content to file
        val gpxWriter = FileWriter(styleFile)
        val out = BufferedWriter(gpxWriter)
        out.write(newFileStr)
        out.close()

        //Setting the map style using the new edited JSON file
        mapBoxMap.setStyle(Style.Builder().fromUri(Uri.fromFile(styleFile).toString())) { style ->
            mapBoxStyle = style

            //Now that the camera is showing the new bounds fully, the current zoom becomes the min zoom
            mapBoxMap.setMinZoomPreference(minZoomLevel)
            mapBoxMap.setMaxZoomPreference(maxZoomLevel)

            // set default view of gujarat...
//            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(22.3039, 70.8022), 10.0)
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    bounds.center.latitude,
                    bounds.center.longitude
                ), minZoomLevel
            )
            mapBoxMap.moveCamera(cameraUpdate)

            setCommonAnnotationStyle()

            mapInitialized()
        }
    }

    private fun setCommonAnnotationStyle() {
        val selectedMarkerIconDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.user_position_point, null)
        mapBoxStyle.addImage(
            MapBoxUtil.STYLE_MARKER_ID,
            BitmapUtils.getBitmapFromDrawable(selectedMarkerIconDrawable)!!
        )

        mapBoxSymbolManager = SymbolManager(mapView, mapBoxMap, mapBoxStyle)
        mapBoxCircleManager = CircleManager(mapView, mapBoxMap, mapBoxStyle)
    }

    private fun mapInitialized() {
        requestLocationPermission()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates() called")
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun enableGPS() {
        gpsUtil.turnGPSOn { isGPSOn ->
            if (isGPSOn) {
                Log.d(TAG, "enableGPS() called with: isGPSOn = $isGPSOn")
                startLocationUpdates()
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                GpsUtil.LOCATION_REQUEST
            )
        } else {
            enableGPS()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == GpsUtil.LOCATION_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d(
                    TAG,
                    "onRequestPermissionsResult() called with: requestCode = $requestCode, permissions = $permissions, grantResults = $grantResults"
                )
                enableGPS()
            }
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GpsUtil.GPS_REQUEST) {
            Log.d(
                TAG,
                "onActivityResult() called with: requestCode = $requestCode, resultCode = $resultCode, data = $data"
            )
            startLocationUpdates()
        }
    }

    private fun moveCameraToNewLocation(location: Location) {
        val newLocation = LatLng(location.latitude, location.longitude)

        if (lastLatLng == null) {
            mapBoxMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(newLocation)
                        .zoom(16.0)
                        .build()
                )
            )
        }

        lastLatLng = location
    }

    private fun verifyLocationForPolyline(location: Location) {
        // if tracking started & not completed then save location to list...
        if (isTrackingStarted && !isTrackingCompleted) {

            // if location list empty add first location...
            if (locationList.isEmpty()) {
                locationList.add(LatLng(location.latitude, location.longitude))
                return
            }

            val prevLocation = LatLng(locationList.last().latitude, locationList.last().longitude)
            val newLocation = LatLng(location.latitude, location.longitude)

            val distance = MapBoxUtil.computeDistanceBetween(prevLocation, newLocation)
            Log.d(TAG, "verifyLocationForPolyline() called with: distance = $distance")

            // if new location found make sure it's have 1 meter distance...
            if (distance > 1) {
                locationList.add(newLocation)
                MapBoxUtil.addPolyline(mapBoxStyle, locationList)
            }
        }
    }

    private fun drawPolygonWithDistance() {
        // clear map...
        userPositionMarker?.let { mapBoxSymbolManager.delete(it) }
        userAccuracyCircle?.let { mapBoxCircleManager.delete(it) }
        MapBoxUtil.deletePolyline(mapBoxStyle)

        // draw polygon & show in center of map...
        MapBoxUtil.addPolygon(mapBoxStyle, locationList, true)
        MapBoxUtil.showPolygonInCenter(mapBoxMap, locationList)

        calculateArea()
        addMarkers()
    }

    private fun calculateArea() {
        val areaInMeter = MapBoxUtil.computeArea(locationList)
        val totalGuntha = areaInMeter / GpsUtil.ONE_GUNTHA_IN_METERS
        val areaInAcer = (totalGuntha / GpsUtil.ONE_ACRE_IN_GUNTHA)
        val areaInGuntha = (totalGuntha % GpsUtil.ONE_ACRE_IN_GUNTHA)

        binding.txtArea.text = buildString {
            append("${getString(R.string.area)}:\n")
            append(areaInAcer.toInt())
            append(" ${getString(R.string.acres)}\n")
            append(areaInGuntha.toInt())
            append(" ${getString(R.string.guntha)}")
        }

        finalAreaInSqMeters = String.format("%.4f", areaInMeter)
        finalAreaInAcre = "${areaInAcer.toInt()}.${String.format("%02d", areaInGuntha.toInt())}"

        Log.d(TAG, "calculateArea() areaInMeter  :==> $areaInMeter")
        Log.d(TAG, "calculateArea() totalGuntha  :==> $totalGuntha")
        Log.d(TAG, "calculateArea() areaInAcer   :==> $areaInAcer")
        Log.d(TAG, "calculateArea() areaInGuntha :==> $areaInGuntha")
        Log.d(TAG, "calculateArea() final areaInSqMeters  :==> $finalAreaInSqMeters")
        Log.d(TAG, "calculateArea() final areaInAcre  :==> $finalAreaInAcre")
    }

    private fun addMarkers() {
        Log.e(TAG, "addMarkers() called ::==>> ${locationList.size}")
        if (locationList.size >= 2) {

            for (location in locationList) {
                finalLocationList.put(
                    JSONArray().apply {
                        put(location.longitude)
                        put(location.latitude)
                    }
                )
            }

            val minDistance = 50.0 // meters

            var position = 1
            var distanceSum = 0.0
            var prevIndex = 0

            for (index in locationList.indices) {
                if (index > 0) {
                    val distance = MapBoxUtil.computeDistanceBetween(
                        locationList[index - 1],
                        locationList[index]
                    )
                    distanceSum += distance

                    // show marker if distance greaterThan minDistance or last location in list...
                    if (distanceSum > minDistance || index == (locationList.size - 1)) {
                        val distanceInString = String.format("%.2f", distanceSum)
                        val displayString = "${distanceInString}m ($position)"
                        MapBoxUtil.addTextMarker(
                            this,
                            mapView,
                            mapBoxMap,
                            locationList[index],
                            displayString
                        )

                        finalDistanceList.put(
                            JSONArray().apply {
                                put(prevIndex)  // start location index
                                put(index)      // end location index
                                put(distanceInString.toFloat()) // distance between start & end location
                            }
                        )

                        prevIndex = index
                        distanceSum = 0.0
                        position++
                    }
                }
            }

            // show First & Last Location distance...
            val distance =
                MapBoxUtil.computeDistanceBetween(locationList.first(), locationList.last())
            val distanceInString = String.format("%.2f", distance)
            val displayString = "${distanceInString}m ($position)"
            MapBoxUtil.addTextMarker(this, mapView, mapBoxMap, locationList[0], displayString)

            finalDistanceList.put(
                JSONArray().apply {
                    put(locationList.size - 1)  // last location index
                    put(0)      // first location index
                    put(distanceInString.toFloat()) // distance between last & first location
                }
            )
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            toolbarBinding.ivBack.id -> {
                showWarningDialog()
            }

            binding.relativeSubmit.id -> {
                if (!isTrackingStarted && !isTrackingCompleted) {

                    gpsUtil.turnGPSOn { isGPSOn ->
                        if (isGPSOn) {
                            isTrackingStarted = true
                            binding.txtSubmit.setText(R.string.prakriya_purn_guj)

                            verifyLocationForPolyline(lastLatLng!!)
                        }
                    }

                } else if (!isTrackingCompleted) {

                    if (BuildConfig.DEBUG) {
                        locationList.clear()
                        locationList.addAll(tempLocationList)
                    }

                    if (locationList.size < 3) {
                        Const.showSnackBar(this, getString(R.string.continue_mapni_to_complete))
                        return
                    }

                    isTrackingCompleted = true
                    isTrackingStarted = false
                    stopLocationUpdates()

                    binding.txtSubmit.setText(R.string.cont)
                    binding.cardArea.visibility = View.VISIBLE

                    // save location list and draw polygon...
                    drawPolygonWithDistance()
                } else {
                    prepareJsonForApiCall()
                }
            }
        }
    }

    private fun prepareJsonForApiCall() {

        val geometryJson = JSONObject().apply {
            put("type", "Polygon")
            put("coordinates", JSONArray().put(finalLocationList))
        }

        val propertyJson = JSONObject().apply {
            put("fill", "#6492a6")
            put("stroke", "#808080")
            put("fill-opacity", 0.5)
            put("stroke-width", 3)
            put("stroke-opacity", 1)
        }

        val featuresJson = JSONObject().apply {
            put("type", "Feature")
            put("geometry", geometryJson)
            put("coordinateDistances", finalDistanceList)
            put("properties", propertyJson)
            put("totalAreaSquareMeter", finalAreaInSqMeters)
            put("totalAreaAcreGuntha", finalAreaInAcre)
        }

        val requestJson = JSONObject().apply {
            put("type", "FeatureCollection")
            put("features", JSONArray().put(featuresJson))
        }

        Log.d(TAG, "prepareJsonForApiCall() called ::==> $requestJson")

        MapBoxUtil.showPolygonInCenter(mapBoxMap, locationList, false)

        startNextActivity(requestJson)
    }

    private fun startNextActivity(mapJson: JSONObject) {
        DNDUtil.disableDNDMode(this)

        mapniData!!.ropanArea = finalAreaInAcre
        mapniData!!.polygonJson = mapJson.toString()
        val intent = Intent(this, MapKhetarMapniOfflinePhotoActivity::class.java)
        intent.putExtra("data", mapniData)
        startActivity(intent)
    }

    private fun showWarningDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_warning)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            DNDUtil.disableDNDMode(this)
            finish()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showWarningDialog()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    companion object {
        private const val TAG = "MapKhetarMapniOfflineActivity"

        private const val STYLE_FILE_NAME = "raster_style.json"
        public const val MBTILES_FILE_NAME = "narma_bharuch_final_9_16.mbtiles"
//        public const val MBTILES_FILE_NAME = "ahm_final_gj_11_16.mbtiles"
//        public const val MBTILES_FILE_NAME = "rajkot_test.mbtiles"
    }

}