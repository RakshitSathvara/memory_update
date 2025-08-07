package com.narmada.measure.utils

import android.content.Context
import android.graphics.*
import android.location.Location
import android.os.Environment
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.narmada.measure.R
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Rajnit Gajera on 09,June,2023
 */
object MapUtil {


    const val LINE_WIDTH = 5.0f

//    const val LINE_COLOR = Color.parseColor("#FF0A4600") // green
//    const val FILL_COLOR = Color.parseColor("#240A4600")
    val LINE_COLOR = Color.parseColor("#FF0000FF") // blue
    val FILL_COLOR = Color.parseColor("#240000FF")

    private var userPositionMarkerBitmapDescriptor: BitmapDescriptor? = null

    fun drawLocationAccuracyCircle(googleMap: GoogleMap, location: Location): Circle {
        val latLng = LatLng(location.latitude, location.longitude)
        return googleMap.addCircle(
            CircleOptions()
                .center(latLng)
                .fillColor(Color.argb(64, 0, 0, 0))
                .strokeColor(Color.argb(64, 0, 0, 0))
                .strokeWidth(0.0f)
                .radius(location.accuracy.toDouble())
        )
    }

    fun drawUserPositionMarker(googleMap: GoogleMap, location: Location): Marker? {
        val latLng = LatLng(location.latitude, location.longitude)
        if (this.userPositionMarkerBitmapDescriptor == null) {
            userPositionMarkerBitmapDescriptor =
                BitmapDescriptorFactory.fromResource(R.drawable.user_position_point)
        }
        return googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .flat(true)
                .anchor(0.5f, 0.5f)
                .icon(this.userPositionMarkerBitmapDescriptor)
        )
    }

    fun addPolyline(googleMap: GoogleMap, locationList: ArrayList<LatLng>): Polyline {
            return googleMap.addPolyline(
                PolylineOptions()
                    .width(LINE_WIDTH)
                    .color(LINE_COLOR)
                    .geodesic(true)
            ).also {
                it.points = locationList
            }
    }

    fun updatePolyline(runningPathPolyline: Polyline, locationList: ArrayList<LatLng>) {
        if (locationList.size > 2) {
            runningPathPolyline.points = locationList
        }
    }

    fun addPolygon(googleMap: GoogleMap, locationList: ArrayList<LatLng>): Polygon {
        return googleMap.addPolygon(
            PolygonOptions()
                .addAll(locationList)
                .strokeWidth(LINE_WIDTH)
                .strokeColor(LINE_COLOR)
                .fillColor(FILL_COLOR)
                .zIndex(100f)
                .geodesic(true)
        )
    }

    fun updatePolygon(runningPathPolygon: Polygon, locationList: ArrayList<LatLng>) {
        if (locationList.size > 2) {
            runningPathPolygon.points = locationList
        }
    }

    fun showPolygonInCenter(googleMap: GoogleMap, locationList: ArrayList<LatLng>, isAnimate: Boolean = true) {
        if (locationList.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            for (location in locationList) {
                builder.include(location)
            }
            val bounds = builder.build()
            val padding = 150 // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            if(isAnimate) {
                googleMap.animateCamera(cu)
            } else {
                googleMap.moveCamera(cu)
            }

            // Move map to center left by 100px for proper display of map with polygon in center...
            if(!isAnimate) {
                val centerPoint = googleMap.projection.toScreenLocation(googleMap.cameraPosition.target)
//                Log.d("TAG", "showPolygonInCenter: (${centerPoint.x}, ${centerPoint.y})")
                centerPoint.x = centerPoint.x + 100
//                Log.d("TAG", "showPolygonInCenter: (${centerPoint.x}, ${centerPoint.y})")
                val newCenterLatLng = googleMap.projection.fromScreenLocation(centerPoint)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(newCenterLatLng))
            }
        }
    }

    fun addTextMarker(context: Context, map: GoogleMap, location: LatLng, text: String, @ColorInt textColor: Int = Color.WHITE): Marker? {
        val padding = 2
        val fontSize = 12

        val marker: Marker?

        val textView = TextView(context)
        textView.text = text
        textView.textSize = fontSize.toFloat()

        val paintText: Paint = textView.paint
        val boundsText = Rect()

        paintText.getTextBounds(text, 0, textView.length(), boundsText)
        paintText.textAlign = Paint.Align.CENTER
        paintText.color = textColor

        val conf = Bitmap.Config.ARGB_8888
        val bmpText = Bitmap.createBitmap(boundsText.width() + 2 * padding, boundsText.height() + 2 * padding, conf)

        val canvasText = Canvas(bmpText)
        canvasText.drawText(text, (canvasText.width / 2).toFloat(), (canvasText.height - padding - boundsText.bottom).toFloat(), paintText)

        val markerOptions = MarkerOptions()
            .position(location)
            .icon(BitmapDescriptorFactory.fromBitmap(bmpText))
            .anchor(0.0f, 0.0f)
        marker = map.addMarker(markerOptions)
        return marker
    }


    fun captureMapScreenshot(googleMap: GoogleMap, resultCallback: (status: Boolean, imgFilePath: String?) -> Any) {
        val snapReadyCallback: GoogleMap.SnapshotReadyCallback = object :
            GoogleMap.SnapshotReadyCallback {
            var bitmap: Bitmap? = null
            override fun onSnapshotReady(snapshot: Bitmap?) {
                bitmap = snapshot

                try {
                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "map_${System.currentTimeMillis()}.png")
                    val fout = FileOutputStream(file)
                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, fout)
                    resultCallback(true, file.absolutePath)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    resultCallback(false, null)
                }
            }
        }

        googleMap.snapshot(snapReadyCallback)
    }

}