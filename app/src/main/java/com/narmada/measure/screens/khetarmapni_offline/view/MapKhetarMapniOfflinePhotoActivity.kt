package com.narmada.measure.screens.khetarmapni_offline.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityMapKhetarMapniOfflinePhotoBinding
import com.narmada.measure.screens.khetarmapni_offline.model.OfflineMapniModel
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.MapBoxUtil
import com.narmada.measure.utils.MapBoxUtil.readToString
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class MapKhetarMapniOfflinePhotoActivity : AppCompatActivity(), View.OnClickListener {

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val binding by lazy { ActivityMapKhetarMapniOfflinePhotoBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    var mapniData: OfflineMapniModel? = null

    private val mapView by lazy { binding.mapView }
    private lateinit var mapBoxMap: MapboxMap
    private lateinit var mapBoxStyle: Style

    private val locationList = arrayListOf<LatLng>()

    private lateinit var mapJson: JSONObject

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.mapView.onCreate(savedInstanceState)

        mapniData = intent.getSerializable("data", OfflineMapniModel::class.java)
        toolbarBinding.ivBack.setOnClickListener(this)
        toolbarBinding.txtTitle.setText(R.string.offline_khetar_mapni)

        try {
            mapJson = JSONObject(mapniData!!.polygonJson.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        dialog.show()


        mapView.getMapAsync { map ->
            mapBoxMap = map
            mapBoxMap.uiSettings.setAllGesturesEnabled(false)
            // for hide logo...
//            mapBoxMap.uiSettings.isLogoEnabled = false
//            mapBoxMap.uiSettings.isAttributionEnabled = false

            // set default view of gujarat...
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(22.3039, 70.8022), 14.0)
            mapBoxMap.moveCamera(cameraUpdate)

            showMbTilesMap(MapBoxUtil.getFileFromAssets(this, MBTILES_FILE_NAME))
        }

    }

    private fun showMbTilesMap(mbtilesFile: File) {
        val styleJsonInputStream = assets.open(STYLE_FILE_NAME)

        val dir = File(filesDir.absolutePath)
        val styleFile = File(dir, STYLE_FILE_NAME)

        //Copying the original JSON content to new file
        MapBoxUtil.copyStreamToFile(styleJsonInputStream, styleFile)

        //Replacing placeholder with uri of the mbtiles file
        val newFileStr = styleFile.inputStream().readToString().replace("___FILE_URI___", "mbtiles:///${mbtilesFile.absolutePath}")

        //Writing new content to file
        val gpxWriter = FileWriter(styleFile)
        val out = BufferedWriter(gpxWriter)
        out.write(newFileStr)
        out.close()

        //Setting the map style using the new edited JSON file
        mapBoxMap.setStyle(Style.Builder().fromUri(Uri.fromFile(styleFile).toString())) { style ->
            mapBoxStyle = style

            mapInitialized()
        }

    }

    private fun mapInitialized() {

        val locationArray = mapJson.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0)
        for (i in 0 until locationArray.length()) {
            val location = locationArray.getJSONArray(i)
            locationList.add(LatLng(location.getDouble(1), location.getDouble(0)))
        }

        if (locationList.isNotEmpty()) {
             MapBoxUtil.addPolygon(mapBoxStyle, locationList, true)

            val distanceArray = mapJson.getJSONArray("features").getJSONObject(0).getJSONArray("coordinateDistances")
            for (i in 0 until distanceArray.length()) {
                val distance = distanceArray.getJSONArray(i)

                val displayString = "${distance.getDouble(2)}m (${i + 1})"
                MapBoxUtil.addTextMarker(this, mapView, mapBoxMap, locationList[distance.getInt(1)], displayString, textColor = Color.BLACK)
            }

            MapBoxUtil.showPolygonInCenter(mapBoxMap, locationList, false)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            MapBoxUtil.captureMapScreenshot(this, mapBoxMap) { status, imgFilePath ->
                dialog.dismiss()

                if (status && imgFilePath != null) {
                    Log.d(TAG, "captureMapScreenshot() called with: status = $status, imgFilePath = $imgFilePath")
                    startNextActivity(imgFilePath)
                } else {
                    Toast.makeText(this@MapKhetarMapniOfflinePhotoActivity, "Map Screenshot Failed!", Toast.LENGTH_SHORT).show()
                }
            }

        }, 1000)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            toolbarBinding.ivBack.id -> {
                finish()
            }
        }
    }

    private fun startNextActivity(imgFilePath: String) {
        mapniData?.mapImage = imgFilePath

        val intent = Intent(this, ReviewKhetarMapniOfflineActivity::class.java)
        intent.putExtra("data", mapniData)
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
        private const val TAG = "MapKhetarMapniOfflinePhotoActivity"

        private const val STYLE_FILE_NAME = "bright.json"
        // Note: Don't change this mbtiles,
        // This is used for offline map photo capture with clean background...
        private const val MBTILES_FILE_NAME = "planet_for_offline_photo.mbtiles"
    }

}