package com.narmada.measure.screens.attendance.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUriUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityAttendanceInOutBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.attendance.viewmodel.AttendanceRepository
import com.narmada.measure.screens.attendance.viewmodel.AttendanceViewModel
import com.narmada.measure.screens.attendance.viewmodel.AttendanceViewModelFactory
import com.narmada.measure.screens.khetarmapni.model.VillageItem
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.Const.showToast
import com.narmada.measure.utils.GpsUtil
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Calendar


class AttendanceInOutActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityAttendanceInOutBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var gpsUtil: GpsUtil
    private var lastLatLng: Location? = null
    private val finalLocationList = JSONArray()
    lateinit var viewModel: AttendanceViewModel
    private var khetarFile: File? = null
    var mojeGaamSpinnerDialog: SpinnerDialog? = null
    var villageList: ArrayList<VillageItem> = ArrayList()
    var villageItems: ArrayList<String> = ArrayList()
    var mojeGaamPosition: Int? = null
    var type: Int? = null
    var locationJson: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        type = intent.getIntExtra("type", -1)
        toolbarBinding.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.etPhotoUpload.setOnClickListener(this)
        binding.etGaamCode.setOnClickListener(this)
        toolbarBinding.txtTitle.text = getString(R.string.hajri_samay)

        gpsUtil = GpsUtil(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(GpsUtil.LOCATION_TIME_INTERVAL)
            .setMinUpdateIntervalMillis(GpsUtil.LOCATION_FAST_TIME_INTERVAL)
            .setMinUpdateDistanceMeters(GpsUtil.LOCATION_DISTANCE_INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setWaitForAccurateLocation(true)
            .build()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val second = c.get(Calendar.SECOND)
        val date = String.format(
            "%02d-%02d-%d",
            day,
            (month + 1),
            year
        )

        val time = String.format("%02d:%02d:%02d", hour, minute, second)
        binding.etTarikh.setText(date)
        binding.etSamay.setText(time)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null && GpsUtil.isValidLocation(location)) {
                        Log.e(TAG, "onLocationResult() called with: locationResult = $location")
                        lastLatLng = location
                        finalLocationList.apply {
                            put(location.longitude)
                            put(location.latitude)
                        }
                        prepareJsonForApiCall()
                        stopLocationUpdates()
                    }
                }
            }
        }

        setupViewModel()

        viewModel.villageList(
            VillageListRequest(
                SharedPreferenceUtil.getString(Constants.ZONE_ID, ""),
                Constants.ATTENDANCE
            )
        )

    }

    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, AttendanceViewModelFactory(AttendanceRepository(retrofitService!!))
        )[AttendanceViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            showSnackBar(this, it)
        }

        viewModel.errorIntMessage.observe(this) {
            showSnackBar(this, getString(it))
        }

        viewModel.progressObservable.observe(this) {
            if (it == true) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

        viewModel.villageListResponse.observe(this) {
            try {
                villageList.clear()
                villageList.addAll(it.data!! as List<VillageItem>)
                for (item in villageList) {
                    villageItems.add(item.villageName.toString())
                }
                requestLocationPermission()
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.addAttendanceResponse.observe(this) {
            try {
                showToast(this, it.message.toString())
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {
            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.etPhotoUpload.id -> {
                pickCameraImage()
            }

            binding.btnSubmit.id -> {
                if (locationJson.isEmpty()) {
                    showSnackBar(this, getString(R.string.please_wait_fetching_location))
                } else if (mojeGaamPosition == null) {
                    showSnackBar(this, getString(R.string.select_moje_gaam))
                } else if (khetarFile == null) {
                    showSnackBar(this, getString(R.string.please_select_profile))
                } else {
                    val type = if (type == 1) {
                        Constants.IN_TIME
                    } else {
                        Constants.OUT_TIME
                    }

                    viewModel.addAttendance(
                        villageList[mojeGaamPosition!!].villageId!!,
                        type,
                        binding.etTarikh.text.toString(),
                        binding.etSamay.text.toString(),
                        locationJson,
                        khetarFile
                    )
                }
            }

            binding.etGaamCode.id -> {
                mojeGaamSpinnerDialog = SpinnerDialog(
                    this, villageItems,
                    getString(R.string.gaam_code), getString(R.string.close)
                )
                mojeGaamSpinnerDialog!!.setCancellable(true)
                mojeGaamSpinnerDialog!!.setShowKeyboard(false)
                mojeGaamSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etGaamCode.setText(item)
                    mojeGaamPosition = position
                    mojeGaamSpinnerDialog!!.closeSpinerDialog()
                }
                mojeGaamSpinnerDialog!!.showSpinerDialog()
            }
        }
    }

    private fun pickCameraImage() {
        cameraLauncher.launch(
            ImagePicker.with(this)
                .crop()
                .cameraOnly()
                .maxResultSize(1080, 1920, true)
                .createIntent()
        )
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                val filePath = FileUriUtils.getRealPath(this, uri)
                val filename: String = filePath!!.substring(filePath.lastIndexOf("/") + 1)
                binding.etPhotoUpload.setText(filename)
                khetarFile = File(filePath.toString())
            } else {
                parseError(it)
            }
        }

    private fun parseError(activityResult: ActivityResult) {
        if (activityResult.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(activityResult.data), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, getString(R.string.task_cancelled), Toast.LENGTH_SHORT).show()
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
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GpsUtil.GPS_REQUEST) {
            Log.d(
                TAG,
                "onActivityResult() called with: requestCode = $requestCode, resultCode = $resultCode, data = $data"
            )
            startLocationUpdates()
        }
    }

    private fun enableGPS() {
        gpsUtil.turnGPSOn { isGPSOn ->
            if (isGPSOn) {
                Log.d(TAG, "enableGPS() called with: isGPSOn = $isGPSOn")
                startLocationUpdates()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates() called")
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun prepareJsonForApiCall() {
        val geometryJson = JSONObject().apply {
            put("type", "Point")
            put("coordinates", finalLocationList)
        }

        val propertyJson = JSONObject().apply {
        }

        val featuresJson = JSONObject().apply {
            put("type", "Feature")
            put("geometry", geometryJson)
            put("properties", propertyJson)
        }

        val requestJson = JSONObject().apply {
            put("type", "FeatureCollection")
            put("features", JSONArray().put(featuresJson))
        }

        Log.d(TAG, "prepareJsonForApiCall() called ::==> $requestJson")

        locationJson = requestJson.toString()
    }

    companion object {
        private const val TAG = "AttendanceActivity"
    }

}