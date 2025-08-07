package com.narmada.measure.screens.khetarmapni.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityMapKhetarMapniManualBinding
import com.narmada.measure.screens.khetarmapni.model.KhetarMapniRequest
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.DNDUtil
import com.narmada.measure.utils.GpsUtil
import com.narmada.measure.utils.MapUtil
import org.json.JSONArray
import org.json.JSONObject


class MapKhetarMapniManualActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityMapKhetarMapniManualBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    private lateinit var googleMap: GoogleMap
    var mapniData: KhetarMapniRequest? = null

    private lateinit var gpsUtil: GpsUtil
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var lastLatLng: Location? = null

    private var locationAccuracyCircle: Circle? = null
    private var userPositionMarker: Marker? = null
    private var runningPathPolyline: Polyline? = null
    private var runningPathPolygon: Polygon? = null

    private val locationList = arrayListOf<LatLng>()
    private var isTrackingStarted = false
    private var isTrackingCompleted = false

    // to show distance on map...
    private val markerList = arrayListOf<Marker>()

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

    private val finalLocationList = JSONArray()
    private val finalDistanceList = JSONArray()
    private var finalAreaInSqMeters: String? = null
    private var finalAreaInAcre: String? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mapniData = intent.getSerializable("data", KhetarMapniRequest::class.java)
        binding.relativeCaptureLocation.setOnClickListener(this)
        binding.relativeSubmit.setOnClickListener(this)
        toolbarBinding.ivBack.setOnClickListener(this)
        toolbarBinding.txtTitle.setText(R.string.farm_measure_guj)
        binding.mapView.onCreate(savedInstanceState)

        gpsUtil = GpsUtil(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(GpsUtil.LOCATION_TIME_INTERVAL)
            .setMinUpdateIntervalMillis(GpsUtil.LOCATION_FAST_TIME_INTERVAL)
            .setMinUpdateDistanceMeters(GpsUtil.LOCATION_DISTANCE_INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setWaitForAccurateLocation(true)
            .build()

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

        binding.mapView.getMapAsync { map ->
            googleMap = map
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            googleMap.uiSettings.isRotateGesturesEnabled = false

            // set default view of gujarat...
            val cameraUpdate: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(22.3039, 70.8022), 10f)
            googleMap.moveCamera(cameraUpdate)

            mapInitialized()
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null && GpsUtil.isValidLocation(location)) {
                        Log.e(TAG, "onLocationResult() called with: locationResult = $location")

                        binding.relativeSubmit.visibility = View.VISIBLE

                        moveCameraToNewLocation(location)
//                        verifyLocationForPolyline(location)

                        if (!isTrackingCompleted) {
                            if (locationAccuracyCircle == null) {
                                locationAccuracyCircle =
                                    MapUtil.drawLocationAccuracyCircle(googleMap, location)
                            } else {
                                locationAccuracyCircle?.center =
                                    LatLng(location.latitude, location.longitude)
                            }

                            if (userPositionMarker == null) {
                                userPositionMarker =
                                    MapUtil.drawUserPositionMarker(googleMap, location)
                            } else {
                                userPositionMarker?.position =
                                    LatLng(location.latitude, location.longitude)
                            }
                        }

                    }
                }
            }
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 17.5f))
        }
//        else {
//            googleMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation))
//        }

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

            val distance = SphericalUtil.computeDistanceBetween(prevLocation, newLocation)
            Log.d(TAG, "verifyLocationForPolyline() called with: distance = $distance")

            // if new location found make sure it's have 1 meter distance...
            if (distance > 1) {
                locationList.add(newLocation)

                if (runningPathPolyline == null) {
                    runningPathPolyline = MapUtil.addPolyline(googleMap, locationList)
                } else {
                    MapUtil.updatePolyline(runningPathPolyline!!, locationList)
                }
            }
        }
    }

    private fun drawPolygonWithDistance() {
        // clear polyline from map
        locationAccuracyCircle?.remove()
        userPositionMarker?.remove()
        runningPathPolyline?.remove()

        if (runningPathPolygon == null) {
            runningPathPolygon = MapUtil.addPolygon(googleMap, locationList)
        } else {
            MapUtil.updatePolygon(runningPathPolygon!!, locationList)
        }

        MapUtil.showPolygonInCenter(googleMap, locationList)
        calculateArea()
        addMarkers()
    }

    private fun calculateArea() {
        val areaInMeter = SphericalUtil.computeArea(locationList)
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

            // set to 0.0 to draw every marker
            val minDistance = 0.0 // meters

            var position = 1
            var distanceSum = 0.0
            var prevIndex = 0

            for (index in locationList.indices) {
                if (index > 0) {
                    val distance = SphericalUtil.computeDistanceBetween(
                        locationList[index - 1],
                        locationList[index]
                    )
                    distanceSum += distance

                    // show marker if distance greaterThan minDistance or last location in list...
                    if (distanceSum > minDistance || index == (locationList.size - 1)) {
                        val distanceInString = String.format("%.2f", distanceSum)
                        val displayString = "${distanceInString}m ($position)"
                        val distanceMarker = MapUtil.addTextMarker(
                            this,
                            googleMap,
                            locationList[index],
                            displayString
                        )
                        markerList.add(distanceMarker!!)

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
            val distance = SphericalUtil.computeDistanceBetween(
                locationList.first(),
                locationList.last()
            )
            val distanceInString = String.format("%.2f", distance)
            val displayString = "${distanceInString}m ($position)"
            val distanceMarker =
                MapUtil.addTextMarker(this, googleMap, locationList[0], displayString)

            markerList.add(distanceMarker!!)

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

            binding.relativeCaptureLocation.id -> {
                verifyLocationForPolyline(lastLatLng!!)
            }

            binding.relativeSubmit.id -> {
                if (!isTrackingStarted && !isTrackingCompleted) {

                    gpsUtil.turnGPSOn { isGPSOn ->
                        if (isGPSOn) {
                            isTrackingStarted = true
                            binding.txtSubmit.setText(R.string.prakriya_purn_guj)
                            binding.relativeCaptureLocation.visibility = View.VISIBLE

                            verifyLocationForPolyline(lastLatLng!!)
                        }
                    }

                } else if (!isTrackingCompleted) {

//                    if(BuildConfig.DEBUG) {
//                        locationList.clear()
//                        locationList.addAll(tempLocationList)
//                    }

                    if(locationList.size < 3) {
                        Const.showSnackBar(this, getString(R.string.continue_mapni_to_complete))
                        return
                    }

                    isTrackingCompleted = true
                    isTrackingStarted = false
                    stopLocationUpdates()

                    binding.txtSubmit.setText(R.string.cont)
                    binding.cardArea.visibility = View.VISIBLE
                    binding.relativeCaptureLocation.visibility = View.GONE

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

        MapUtil.showPolygonInCenter(googleMap, locationList, isAnimate = false)

        startNextActivity(requestJson)
    }

    private fun startNextActivity(mapJson: JSONObject) {
        DNDUtil.disableDNDMode(this)

        mapniData!!.ropanArea = finalAreaInAcre
        mapniData!!.polygonJson = mapJson.toString()

        val intent = Intent(this, MapKhetarMapniPhotoActivity::class.java)
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
        private const val TAG = "MapKhetarMapniManualActivity"
    }

}

/**
 * https://stackoverflow.com/a/14226682
 *
 * public class MarkerDemoActivity extends android.support.v4.app.FragmentActivity
implements OnMarkerClickListener
{
private Marker myMarker;

private void setUpMap()
{
.......
googleMap.setOnMarkerClickListener(this);

myMarker = googleMap.addMarker(new MarkerOptions()
.position(latLng)
.title("My Spot")
.snippet("This is my spot!")
.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
......
}

@Override
public boolean onMarkerClick(final Marker marker) {

if (marker.equals(myMarker))
{
//handle click here
}
}
}
 */

/**
 * https://stackoverflow.com/a/24623593
 *
 * map. addMarker(new MarkerOptions().position(new LatLng(lat, lng)).draggable(true));
 *  map.setOnMarkerDragListener(new OnMarkerDragListener()
{
@Override
public void onMarkerDragStart(Marker marker)
{
// TODO Auto-generated method stub
}

@Override
public void onMarkerDragEnd(Marker marker)
{
// TODO Auto-generated method stub
lat     = marker.getPosition().latitude;
lng     = marker.getPosition().longitude;
}

@Override
public void onMarkerDrag(Marker marker)
{
// TODO Auto-generated method stub
}
});
 */