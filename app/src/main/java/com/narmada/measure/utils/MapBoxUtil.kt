package com.narmada.measure.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.location.Location
import android.util.Log
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.maps.android.SphericalUtil
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.*
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.io.*

/**
 * Created by Rajnit Gajera on 09,June,2023
 */
object MapBoxUtil {

    const val STYLE_MARKER_ID = "user_position_marker"
    private const val POLYLINE_LAYER_ID = "polyline_layer_id"
    private const val POLYLINE_SOURCE_ID = "polyline_source_id"
    private const val POLYGON_LAYER_ID = "polygon_layer_id"
    private const val POLYGON_SOURCE_ID = "polygon_source_id"
    private const val POLYGON_LAYER_FILL_ID = "polygon_layer_fill_id"
    private const val POLYGON_SOURCE_FILL_ID = "polygon_source_fill_id"

    const val LINE_WIDTH = 5.0f

//    const val LINE_COLOR = Color.parseColor("#FF0A4600") // green
//    const val FILL_COLOR = Color.parseColor("#240A4600")
    val LINE_COLOR = Color.parseColor("#FF0000FF") // blue
    val FILL_COLOR = Color.parseColor("#240000FF")

    fun drawLocationAccuracyCircle(circleManager: CircleManager, location: Location): Circle {
        val latLng = LatLng(location.latitude, location.longitude)
        return circleManager.create(
            CircleOptions()
                .withLatLng(latLng)
                .withCircleColor("#0000FF")
                .withCircleOpacity(0.2f)
                .withCircleRadius(location.accuracy)
        )
    }

    fun drawUserPositionMarker(mapBoxSymbolManager: SymbolManager, location: Location): Symbol {
        val latLng = LatLng(location.latitude, location.longitude)

        return mapBoxSymbolManager.create(
            SymbolOptions()
                .withIconImage(STYLE_MARKER_ID)
                .withLatLng(latLng)
                .withIconSize(1.0f)
        )
    }

    fun addPolyline(mapBoxStyle: Style, locationList: ArrayList<LatLng>) {

        //Delete previously added polyline...
        deletePolyline(mapBoxStyle)

        // We create a GeoJSON polygon containing the coordinates we want to be parsed.
        val lineString : LineString = LineString.fromLngLats(locationList.map { com.mapbox.geojson.Point.fromLngLat(it.longitude, it.latitude) }.toList())
        val parisBoundariesFeature = Feature.fromGeometry(lineString)

        // Create a GeoJson Source from our feature.
        val geoJsonSource = GeoJsonSource(POLYLINE_SOURCE_ID, parisBoundariesFeature)
        // Add it to the map
        mapBoxStyle.addSource(geoJsonSource)

        // Create a layer with the desired style for our source.
        val layer = LineLayer(POLYLINE_LAYER_ID, POLYLINE_SOURCE_ID)
            .withProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                PropertyFactory.lineWidth(2f),
                PropertyFactory.lineOpacity(1f),
                PropertyFactory.lineColor(LINE_COLOR),
            )
        // Add it to the map
        mapBoxStyle.addLayer(layer)
    }

    fun deletePolyline(mapBoxStyle: Style) {
        try {
            mapBoxStyle.removeLayer(POLYLINE_LAYER_ID)
            mapBoxStyle.removeSource(POLYLINE_SOURCE_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addPolygon(mapBoxStyle: Style, locationList: ArrayList<LatLng>, isFill: Boolean = false) {

        //Delete previously added polyline...
        deletePolygon(mapBoxStyle)

        // We create a GeoJSON polygon containing the coordinates we want to be parsed.
        val polygon : Polygon = Polygon.fromLngLats(listOf(locationList.map { com.mapbox.geojson.Point.fromLngLat(it.longitude, it.latitude) }.toList()))
        val parisBoundariesFeature = Feature.fromGeometry(polygon)

        // Create a GeoJson Source from our feature.
        val geoJsonSource = GeoJsonSource(POLYGON_SOURCE_ID, parisBoundariesFeature)
        // Add it to the map
        mapBoxStyle.addSource(geoJsonSource)

        // Create a layer with the desired style for our source.
        val layer = LineLayer(POLYGON_LAYER_ID, POLYGON_SOURCE_ID)
            .withProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                PropertyFactory.lineWidth(2f),
                PropertyFactory.lineOpacity(1f),
                PropertyFactory.lineColor(LINE_COLOR),
            )
        // Add it to the map
        mapBoxStyle.addLayer(layer)

        if(isFill) {
            val geoJsonSourceFill = GeoJsonSource(POLYGON_SOURCE_FILL_ID, parisBoundariesFeature)
            mapBoxStyle.addSource(geoJsonSourceFill)

            val fillLayer = FillLayer(POLYGON_LAYER_FILL_ID, POLYGON_SOURCE_FILL_ID)
                .withProperties(
                    PropertyFactory.fillColor(FILL_COLOR),
                )
            mapBoxStyle.addLayerBelow(fillLayer, POLYGON_LAYER_ID)
        }
    }

    private fun deletePolygon(mapBoxStyle: Style) {
        try {
            mapBoxStyle.removeLayer(POLYGON_LAYER_ID)
            mapBoxStyle.removeSource(POLYGON_SOURCE_ID)

            mapBoxStyle.removeLayer(POLYGON_LAYER_FILL_ID)
            mapBoxStyle.removeSource(POLYGON_SOURCE_FILL_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showPolygonInCenter(mapBoxMap: MapboxMap, locationList: ArrayList<LatLng>, isAnimate: Boolean = true) {
        if (locationList.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            for (location in locationList) {
                builder.include(location)
            }
            val bounds = builder.build()
            val padding = 150 // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            if(isAnimate) {
                mapBoxMap.animateCamera(cu)
            } else {
                mapBoxMap.moveCamera(cu)
            }

            // Move map to center left by 100px for proper display of map with polygon in center...
            if(!isAnimate) {
                val centerPoint = mapBoxMap.projection.toScreenLocation(mapBoxMap.cameraPosition.target)
//                Log.d("TAG", "showPolygonInCenter: (${centerPoint.x}, ${centerPoint.y})")
                centerPoint.x = centerPoint.x + 100
//                Log.d("TAG", "showPolygonInCenter: (${centerPoint.x}, ${centerPoint.y})")
                val newCenterLatLng = mapBoxMap.projection.fromScreenLocation(centerPoint)
                mapBoxMap.moveCamera(CameraUpdateFactory.newLatLng(newCenterLatLng))
            }
        }
    }

    fun addTextMarker(context: Context, mapView: MapView, mapboxMap: MapboxMap, location: LatLng, iconText: String, @ColorInt textColor: Int = Color.WHITE): Symbol {

        mapboxMap.style!!.addImage(iconText, getTextBitmap(context, iconText, textColor))

        val symbolManager = SymbolManager(mapView, mapboxMap, mapboxMap.style!!)
        symbolManager.iconAllowOverlap = true

        val symbolOptions = SymbolOptions()
            .withLatLng(location)
            .withIconImage(iconText)
            .withIconSize(1.0f)
            .withIconAnchor(Property.ICON_ANCHOR_LEFT)

        // Use the manager to draw the annotations...
        return symbolManager.create(symbolOptions)
    }

    private fun getTextBitmap(context: Context, text: String, @ColorInt textColor: Int): Bitmap {
        val padding = 2
        val fontSize = 12

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

        return bmpText
    }


    fun captureMapScreenshot(context: Context, mapBoxMap: MapboxMap, resultCallback: (status: Boolean, imgFilePath: String?) -> Any) {
        val snapReadyCallback: MapboxMap.SnapshotReadyCallback = MapboxMap.SnapshotReadyCallback {bitmap ->
                try {
                    val file = File(context.filesDir, "map_${System.currentTimeMillis()}.png")
                    val fout = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout)
                    resultCallback(true, file.absolutePath)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    resultCallback(false, null)
                }
        }

        mapBoxMap.snapshot(snapReadyCallback)
    }

    fun computeDistanceBetween(prevLocation: LatLng, newLocation: LatLng) : Double {
        val googlePrevLoc = com.google.android.gms.maps.model.LatLng(prevLocation.latitude, prevLocation.longitude)
        val googleNewLoc = com.google.android.gms.maps.model.LatLng(newLocation.latitude, newLocation.longitude)

        val distance = SphericalUtil.computeDistanceBetween(googlePrevLoc, googleNewLoc)
        Log.d("", "verifyLocationForPolyline() called with: distance = $distance")

        return distance
    }

    fun computeArea(locationList: ArrayList<LatLng>): Double {
        val areaInMeter = SphericalUtil.computeArea(locationList.map { com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude) }.toList())
        return areaInMeter
    }

    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File {
        return File(context.filesDir, fileName)
            .also {
                if (!it.exists()) {
                    it.outputStream().use { cache ->
                        context.assets.open(fileName).use { inputStream ->
                            inputStream.copyTo(cache)
                            inputStream.close()
                            cache.close()
                        }
                    }
                }
            }
    }

    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    fun InputStream.readToString(): String {
        val r = BufferedReader(InputStreamReader(this))
        val total = StringBuilder("")
        var line: String?
        while (r.readLine().also { line = it } != null) {
            total.append(line).append('\n')
        }
        return total.toString()
    }

    fun getLatLngBounds(file: File): LatLngBounds {

        Log.d("getLatLngBounds", "absolutePath = ${file.absoluteFile}")

        val openDatabase =
            SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor = openDatabase.query(
            "metadata",
            arrayOf("name", "value"),
            "name=?",
            arrayOf("bounds"),
            null,
            null,
            null,
        )
        cursor?.moveToFirst()
        val boundsStr = cursor.getString(1).split(",")

        Log.d("showMBTilesFile", "boundsStr = $boundsStr")

        cursor.close()
        openDatabase.close()

        return LatLngBounds
            .Builder()
            .include(LatLng(boundsStr[1].toDouble(), boundsStr[0].toDouble()))
            .include(LatLng(boundsStr[3].toDouble(), boundsStr[2].toDouble()))
            .build()
    }

    fun getMinZoom(file: File): Int {

        Log.d("getLatLngBounds", "absolutePath = ${file.absoluteFile}")

        val openDatabase =
            SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor = openDatabase.query(
            "metadata",
            arrayOf("name", "value"),
            "name=?",
            arrayOf("minzoom"),
            null,
            null,
            null,
        )
        cursor?.moveToFirst()
        val minZoomLevel = cursor.getString(1)

        Log.d("showMBTilesFile", "minZoomLevel = $minZoomLevel")

        cursor.close()
        openDatabase.close()

        return minZoomLevel.toInt()
    }

    fun getMaxZoom(file: File): Int {

        Log.d("getLatLngBounds", "absolutePath = ${file.absoluteFile}")

        val openDatabase =
            SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor = openDatabase.query(
            "metadata",
            arrayOf("name", "value"),
            "name=?",
            arrayOf("maxzoom"),
            null,
            null,
            null,
        )
        cursor?.moveToFirst()
        val maxZoomLevel = cursor.getString(1)

        Log.d("showMBTilesFile", "maxZoomLevel = $maxZoomLevel")

        cursor.close()
        openDatabase.close()

        return maxZoomLevel.toInt()
    }

}