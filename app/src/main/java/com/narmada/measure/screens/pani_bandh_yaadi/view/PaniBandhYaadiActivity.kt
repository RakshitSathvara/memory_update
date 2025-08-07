package com.narmada.measure.screens.pani_bandh_yaadi.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityPaniBandhYaadiBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetarmapni.model.VillageItem
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerItem
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiIntent
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiRequest
import com.narmada.measure.screens.pani_bandh_yaadi.viewmodel.PaniBandhYaadiRepository
import com.narmada.measure.screens.pani_bandh_yaadi.viewmodel.PaniBandhYaadiViewModel
import com.narmada.measure.screens.pani_bandh_yaadi.viewmodel.PaniBandhYaadiViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class PaniBandhYaadiActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityPaniBandhYaadiBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: PaniBandhYaadiViewModel

    private lateinit var workingYear: String

    var zoneOfficerList: ArrayList<ZoneOfficerItem> = ArrayList()
    var zoneList: ArrayList<SupervisorZoneItem> = ArrayList()
    var villageList: ArrayList<VillageItem> = ArrayList()
    var dateList: ArrayList<String> = ArrayList()

    var zoneOfficerItems: ArrayList<String> = ArrayList()
    var zoneItems: ArrayList<String> = ArrayList()
    var villageItems: ArrayList<String> = ArrayList()

    var zoneOfficerSpinnerDialog: SpinnerDialog? = null
    var zoneSpinnerDialog: SpinnerDialog? = null
    var villageSpinnerDialog: SpinnerDialog? = null
    var dateSpinnerDialog: SpinnerDialog? = null

    var zoneOfficerPosition: Int? = null
    var zonePosition: Int? = null
    var villagePosition: Int? = null
    var datePosition: Int? = null

    var downloadID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.pani_bandh_yadi)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.etZoneOfficer.setOnClickListener(this)
        binding.etZone.setOnClickListener(this)
        binding.etVillage.setOnClickListener(this)
        binding.etDate.setOnClickListener(this)
        binding.linearDownload.setOnClickListener(this)
        binding.btnView.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        setupViewModel()

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
            binding.etZoneOfficer.setText(
                SharedPreferenceUtil.getString(
                    Constants.OFFICER_NAME,
                    ""
                )
            )
            viewModel.supervisorZoneList(
                SharedPreferenceUtil.getString(
                    Constants.ZONE_SUPERVISOR_ID,
                    ""
                )!!
            )
        } else {
            viewModel.zoneOfficerList()
        }
        viewModel.getRopanYearWiseDatesApi(workingYear)
    }

    private fun resetSupervisorSelection() {
        binding.etZoneOfficer.setText("")
        zoneOfficerList.clear()
        zoneOfficerItems.clear()
        zoneOfficerPosition = null
    }

    private fun resetZonesSelection() {
        binding.etZone.setText("")
        zoneList.clear()
        zoneItems.clear()
        zonePosition = null
    }

    private fun resetVillageSelection() {
        binding.etVillage.setText("")
        villageList.clear()
        villageItems.clear()
        villagePosition = null
    }

    private fun resetDateSelection() {
        binding.etDate.setText("")
        dateList.clear()
        dateList.clear()
        datePosition = null
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            PaniBandhYaadiViewModelFactory(PaniBandhYaadiRepository(retrofitService!!))
        )[PaniBandhYaadiViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            Const.showSnackBar(this, it)
        }

        viewModel.errorIntMessage.observe(this) {
            Const.showSnackBar(this, getString(it))
        }

        viewModel.progressObservable.observe(this) {
            if (it == true) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

        viewModel.ropanYearDatesResponse.observe(this) {
            try {
                dateList.clear()

                it.data.orEmpty().forEach { date ->
                    dateList.add(dateFormatDynamic(date, dateApi, dateDisplay))
                }
                Log.e("===>", "YearListSize => ${dateList.size}")

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.zoneOfficerListResponse.observe(this) {
            try {
                resetSupervisorSelection()
                resetZonesSelection()
                resetVillageSelection()

                zoneOfficerList.addAll(it.data!! as List<ZoneOfficerItem>)
                for (item in zoneOfficerList) {
                    zoneOfficerItems.add(item.zoneOfficerName.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.zoneListResponse.observe(this) {
            try {
                resetZonesSelection()
                resetVillageSelection()

                zoneList.addAll(it.data!! as List<SupervisorZoneItem>)
                for (item in zoneList) {
                    zoneItems.add(item.zoneName.toString())
                }
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
                    if (zoneItems.isNotEmpty()) {
                        binding.etZone.setText(zoneItems[0])
                        zonePosition = 0
                        viewModel.villageList(VillageListRequest(zoneList[zonePosition!!].zoneId.toString()))

                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.villageListResponse.observe(this) {
            try {
                resetVillageSelection()

                villageList.addAll(it.data!! as List<VillageItem>)
                for (item in villageList) {
                    villageItems.add(item.villageName.toString())
                }

                Log.e("===>", "VillageListSize => ${villageList.size}")

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.paniBandhYaadiReportResponse.observe(this) {
            try {

                it.url?.let { url ->
                    downloading(url)
                } ?: Const.showSnackBar(
                    this,
                    it.message ?: getString(R.string.something_went_wrong)
                )

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }
    }

    private val dateDisplay = "dd-MM-yyyy"
    private val dateApi = "yyyy-MM-dd"

    private fun dateFormatDynamic(
        dateString: String?,
        fromFormat: String,
        toFormat: String
    ): String {
        if (dateString == null) return ""
        try {
            val from = SimpleDateFormat(fromFormat, Locale.US)
            val date = from.parse(dateString)
            val to = SimpleDateFormat(toFormat, Locale.US)
            val result = to.format(date)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.etZoneOfficer.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                    zoneOfficerSpinnerDialog = SpinnerDialog(
                        this,
                        zoneOfficerItems,
                        getString(R.string.supervisor),
                        getString(R.string.close)
                    )

                    zoneOfficerSpinnerDialog!!.setCancellable(true)
                    zoneOfficerSpinnerDialog!!.setShowKeyboard(false)
                    zoneOfficerSpinnerDialog!!.bindOnSpinerListener { item, position ->

                        resetZonesSelection()
                        resetVillageSelection()

                        binding.etZoneOfficer.setText(item)
                        zoneOfficerPosition = position

                        zoneOfficerSpinnerDialog!!.closeSpinerDialog()
                        viewModel.supervisorZoneList(zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString())
                    }
                    zoneOfficerSpinnerDialog!!.showSpinerDialog()
                }
            }

            binding.etZone.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    Const.showSnackBar(this, getString(R.string.please_select_supervisor))
                    return
                }

                zoneSpinnerDialog = SpinnerDialog(
                    this,
                    zoneItems,
                    getString(R.string.zone),
                    getString(R.string.close)
                )
                zoneSpinnerDialog!!.setCancellable(true)
                zoneSpinnerDialog!!.setShowKeyboard(false)
                zoneSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    resetVillageSelection()
                    binding.etZone.setText(item)
                    zonePosition = position

                    zoneSpinnerDialog!!.closeSpinerDialog()
                    viewModel.villageList(VillageListRequest(zoneList[zonePosition!!].zoneId.toString()))
                }
                zoneSpinnerDialog!!.showSpinerDialog()

            }

            binding.etVillage.id -> {
                if (zonePosition == null) {
                    Const.showSnackBar(this, getString(R.string.please_select_zone))
                    return
                }

                villageSpinnerDialog = SpinnerDialog(
                    this,
                    villageItems,
                    getString(R.string.gaam),
                    getString(R.string.close)
                )
                villageSpinnerDialog!!.setCancellable(true)
                villageSpinnerDialog!!.setShowKeyboard(false)
                villageSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etVillage.setText(item)
                    villagePosition = position
                    villageSpinnerDialog!!.closeSpinerDialog()
                }
                villageSpinnerDialog!!.showSpinerDialog()

            }

            binding.etDate.id -> {
                dateSpinnerDialog = SpinnerDialog(
                    this,
                    dateList,
                    getString(R.string.pani_bandh_date),
                    getString(R.string.close)
                )
                dateSpinnerDialog!!.setCancellable(true)
                dateSpinnerDialog!!.setShowKeyboard(false)
                dateSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etDate.setText(item)
                    datePosition = position
                    dateSpinnerDialog!!.closeSpinerDialog()
                }
                dateSpinnerDialog!!.showSpinerDialog()

            }

            binding.linearDownload.id -> {
                if (!validateForm()) return

                validateAndSubmitApi()
            }

            binding.btnView.id -> {
                if (!validateForm()) return

                var supervisorId = ""
                var supervisorName = ""

                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                    supervisorId = zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
                    supervisorName =
                        zoneOfficerList[zoneOfficerPosition!!].zoneOfficerName.toString()
                } else {
                    supervisorId =
                        SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "") ?: ""
                    supervisorName =
                        SharedPreferenceUtil.getString(Constants.OFFICER_NAME, "") ?: ""
                }

                val intentData = PaniBandhYaadiIntent(
                    workingYear = workingYear,
                    supervisorId = supervisorId,
                    supervisorName = supervisorName,
                    zone = zoneList[zonePosition!!],
                    paniBandhDate = dateList[datePosition!!],
                    village = if (villagePosition != null) villageList[villagePosition!!] else null,
                    computerCode = binding.etKapniCode.text.toString(),
                )

                val intent = Intent(this, PaniBandhYaadiListActivity::class.java)
                intent.putExtra("intent", intentData)
                startActivity(intent)
            }
        }
    }

    private fun validateForm(): Boolean {
        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                .equals("2") && zoneOfficerPosition == null
        ) {
            Const.showToast(this, getString(R.string.please_select_supervisor))
            return false
        }

        if (zonePosition == null) {
            Const.showToast(this, getString(R.string.please_select_zone))
            return false
        }

//        if (villagePosition == null) {
//            Const.showToast(this, getString(R.string.please_select_to_date))
//            return false
//        }

        if (datePosition == null) {
            Const.showToast(this, getString(R.string.please_select_pani_bandh_date))
            return false
        }

        return true
    }

    private fun validateAndSubmitApi() {

        val reportRequest = PaniBandhYaadiRequest(
            working_year = workingYear,
            zone_id = zoneList[zonePosition!!].zoneId.toString(),
            date = dateFormatDynamic(dateList[datePosition!!], dateDisplay, dateApi),
            village_id = if (villagePosition != null) villageList[villagePosition!!].villageId.toString() else "",
            computer_code = binding.etKapniCode.text.toString(),
        )

//        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
//            reportRequest.supervisor_id = zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
//        } else {
//            reportRequest.supervisor_id = SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")
//        }

        Log.e("TAG", "PaniBandhYaadiRequest: $reportRequest")

        viewModel.generatePaniBandhYaadiReportApi(reportRequest)
    }

    private fun downloading(fileUrl: String) {
        var downloadFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        downloadFolder = File(
            downloadFolder.path,
            "${getString(R.string.app_name)}/${getString(R.string.pani_bandh_yadi)}"
        )
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs()
        }

        val fileName = getFileNameFromUri(fileUrl)
        downloadID = PRDownloader.download(fileUrl, downloadFolder.path, fileName)
            .build()
            .setOnStartOrResumeListener {
//                Const.showToast(this@PaniBandhRegisterActivity, getString(R.string.please_wait))
                dialog.show()
            }
            .setOnProgressListener { progress -> // getting the progress of download
                val progressPer = progress.currentBytes * 100 / progress.totalBytes
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    dialog.dismiss()
                    openPDF(downloadFolder.path, fileName)
                    MediaScannerConnection.scanFile(
                        this@PaniBandhYaadiActivity,
                        arrayOf(downloadFolder.path),
                        null,
                        null
                    )
                }

                override fun onError(error: com.downloader.Error?) {
                    dialog.dismiss()
                    downloadID = 0
                    Const.showToast(
                        this@PaniBandhYaadiActivity,
                        getString(R.string.something_went_wrong)
                    )
                }
            })
    }

    fun openPDF(path: String, name: String) {
        val newIntent = Intent(Intent.ACTION_VIEW)
        newIntent.data = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider", File("$path/$name")
        )
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(newIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileNameFromUri(url: String): String {
        return url.substring(url.lastIndexOf('/') + 1, url.length);
    }

}