package com.narmada.measure.screens.pani_bandh_register.view

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
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
import com.narmada.measure.databinding.ActivityPaniBandhRegisterBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.bareliserdi.model.MapniTypes
import com.narmada.measure.screens.khetarmapni.model.VillageItem
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerItem
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterIntent
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterRequest
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import com.narmada.measure.screens.pani_bandh_register.viewmodel.PaniBandhRegisterRepository
import com.narmada.measure.screens.pani_bandh_register.viewmodel.PaniBandhRegisterViewModel
import com.narmada.measure.screens.pani_bandh_register.viewmodel.PaniBandhRegisterViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PaniBandhRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityPaniBandhRegisterBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: PaniBandhRegisterViewModel

    private lateinit var workingYear: String

    var mapniTypeList: ArrayList<MapniTypes> = ArrayList()
    var mapniTypeItems: ArrayList<String> = ArrayList()
    var mapniTypeSpinnerDialog: SpinnerDialog? = null
    var mapniTypePosition: Int? = null

    var zoneOfficerList: ArrayList<ZoneOfficerItem> = ArrayList()
    var zoneList: ArrayList<SupervisorZoneItem> = ArrayList()
    var villageList: ArrayList<VillageItem> = ArrayList()

    var zoneOfficerItems: ArrayList<String> = ArrayList()
    var zoneItems: ArrayList<String> = ArrayList()
    var villageItems: ArrayList<String> = ArrayList()

    var zoneOfficerSpinnerDialog: SpinnerDialog? = null
    var zoneSpinnerDialog: SpinnerDialog? = null
    var villageSpinnerDialog: SpinnerDialog? = null

    var zoneOfficerPosition: Int? = null
    var zonePosition: Int? = null
    var villagePosition: Int? = null

    var downloadID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.pani_bandh_register)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.etFromDate.setOnClickListener(this)
        binding.etToDate.setOnClickListener(this)
        binding.etZoneOfficer.setOnClickListener(this)
        binding.etZone.setOnClickListener(this)
        binding.etLaamRopan.setOnClickListener(this)
        binding.etVillage.setOnClickListener(this)
        binding.linearDownload.setOnClickListener(this)
        binding.btnView.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        setupViewModel()

        viewModel.getCommonDataApi(this)
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
        villagePosition = null
        villageList.clear()
        villageItems.clear()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, PaniBandhRegisterViewModelFactory(PaniBandhRegisterRepository(retrofitService!!)))[PaniBandhRegisterViewModel::class.java]

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

        viewModel.commonDataObservable.observe(this) {
            try {
                mapniTypeList.addAll(it.data!!.mapniTypes)
                mapniTypeList.add(MapniTypes(null, getString(R.string.mapni_type_all_shree_guj)))
                for (item in mapniTypeList) {
                    mapniTypeItems.add(item.name.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }

            if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
                binding.etZoneOfficer.setText(SharedPreferenceUtil.getString(Constants.OFFICER_NAME, ""))
                viewModel.supervisorZoneList(SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")!!)
            } else {
                viewModel.zoneOfficerList()
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

        viewModel.paniBandhRegisterResponse.observe(this) {
            try {
                it.url?.let { url -> downloading(url) } ?: Const.showSnackBar(this, it.message ?: getString(R.string.something_went_wrong))
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.etFromDate.id -> {
                showDatePicker(this, null, Calendar.getInstance().timeInMillis) { date ->
                    binding.etFromDate.setText(date)
                    if(binding.etToDate.text.toString().isNotEmpty()) {
                        val toDate = SimpleDateFormat(dateDisplay, Locale.US).parse(binding.etToDate.text.toString())?.time
                        val fromDate = SimpleDateFormat(dateDisplay, Locale.US).parse(date)?.time
                        if(fromDate!! > toDate!!) {
                            binding.etToDate.setText("")
                        }
                    }
                }
            }

            binding.etToDate.id -> {
                if (binding.etFromDate.text.toString().isNotEmpty()) {
                    val fromDate = SimpleDateFormat(dateDisplay, Locale.US).parse(binding.etFromDate.text.toString())?.time
                    showDatePicker(this, fromDate, null) { date ->
                        binding.etToDate.setText(date)
                    }
                } else {
                    Const.showSnackBar(this, getString(R.string.please_select_from_date))
                }
            }

            binding.etZoneOfficer.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                    zoneOfficerSpinnerDialog = SpinnerDialog(this, zoneOfficerItems, getString(R.string.supervisor), getString(R.string.close))

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
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2") && zoneOfficerPosition == null) {
                    Const.showSnackBar(this, getString(R.string.please_select_supervisor))
                    return
                }

                zoneSpinnerDialog = SpinnerDialog(this, zoneItems, getString(R.string.zone), getString(R.string.close))
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

                villageSpinnerDialog = SpinnerDialog(this, villageItems, getString(R.string.gaam), getString(R.string.close))

                villageSpinnerDialog!!.setCancellable(true)
                villageSpinnerDialog!!.setShowKeyboard(false)
                villageSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etVillage.setText(item)
                    villagePosition = position
                    villageSpinnerDialog!!.closeSpinerDialog()
                }
                villageSpinnerDialog!!.showSpinerDialog()

            }

            binding.etLaamRopan.id -> {

                mapniTypeSpinnerDialog = SpinnerDialog(this, mapniTypeItems, getString(R.string.laam_ropan_guj), getString(R.string.close))

                mapniTypeSpinnerDialog!!.setCancellable(true)
                mapniTypeSpinnerDialog!!.setShowKeyboard(false)
                mapniTypeSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etLaamRopan.setText(item)
                    mapniTypePosition = position
                    mapniTypeSpinnerDialog!!.closeSpinerDialog()
                }
                mapniTypeSpinnerDialog!!.showSpinerDialog()

            }

            binding.linearDownload.id -> {
                if (!validateForm()) return

                submitApi()
            }

            binding.btnView.id -> {
                if (!validateForm()) return

                var supervisorId = ""
                var supervisorName = ""

                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                    supervisorId = zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
                    supervisorName = zoneOfficerList[zoneOfficerPosition!!].zoneOfficerName.toString()
                } else {
                    supervisorId = SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "") ?: ""
                    supervisorName = SharedPreferenceUtil.getString(Constants.OFFICER_NAME, "") ?: ""
                }

                val intentData = PaniBandhRegisterIntent(
                    workingYear = workingYear,
                    supervisorId = supervisorId,
                    supervisorName = supervisorName,
                    fromDate = dateFormatDynamic(binding.etFromDate.text.toString(), dateDisplay, dateApi),
                    toDate = dateFormatDynamic(binding.etToDate.text.toString(), dateDisplay, dateApi),
                    zone = zoneList[zonePosition!!],
                    village = villageList[villagePosition!!],
                    mapniType = mapniTypeList[mapniTypePosition!!],
                    khetarCode = binding.etKhetarCode.text.toString(),
                    sabhasadCode = binding.etSabhaCode.text.toString(),
                )

                val intent = Intent(this, PaniBandhRegisterListActivity::class.java)
                intent.putExtra("intent", intentData)
                startActivity(intent)
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.etFromDate.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_from_date))
            return false
        }

        if (binding.etToDate.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_to_date))
            return false
        }

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2") && zoneOfficerPosition == null) {
            Const.showToast(this, getString(R.string.please_select_supervisor))
            return false
        }

        if (zonePosition == null) {
            Const.showToast(this, getString(R.string.please_select_zone))
            return false
        }

        if (mapniTypePosition == null) {
            Const.showToast(this, getString(R.string.please_select_laam_ropan))
            return false
        }

        if (villagePosition == null) {
            Const.showToast(this, getString(R.string.please_select_village))
            return false
        }

        return true
    }

    private fun submitApi() {

        val reportRequest = PaniBandhRegisterRequest(
            working_year = workingYear,
            from_date = dateFormatDynamic(binding.etFromDate.text.toString(), dateDisplay, dateApi),
            to_date = dateFormatDynamic(binding.etToDate.text.toString(), dateDisplay, dateApi),
            zone_id = zoneList[zonePosition!!].zoneId.toString(),
            village_id = villageList[villagePosition!!].villageId.toString(),
            mapni_type = mapniTypeList[mapniTypePosition!!].id?.toString() ?: "",
        )

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
            reportRequest.supervisor_id = zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
        } else {
            reportRequest.supervisor_id = SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")
        }

        Log.e("TAG", "PaniBandhRegisterRequest: $reportRequest")

        viewModel.getPaniBandhRegisterApi(reportRequest)
    }

    private fun downloading(fileUrl: String) {
        var downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        downloadFolder = File(downloadFolder.path, "${getString(R.string.app_name)}/${getString(R.string.pani_bandh_register)}")
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
                    MediaScannerConnection.scanFile(this@PaniBandhRegisterActivity, arrayOf(downloadFolder.path), null, null)
                }

                override fun onError(error: com.downloader.Error?) {
                    dialog.dismiss()
                    downloadID = 0
                    Const.showToast(this@PaniBandhRegisterActivity, getString(R.string.something_went_wrong))
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

    private fun showDatePicker(
        context: Context,
        minDate: Long?,
        maxDate: Long?,
        onDateSelected: (date: String) -> Any
    ) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { view, year, monthOfYear, dayOfMonth ->
                val dat = String.format(
                    "%02d-%02d-%d",
                    dayOfMonth,
                    (monthOfYear + 1),
                    year
                )
                onDateSelected(dat)
            }, year, month, day
        )
        maxDate?.let {
            datePickerDialog.datePicker.maxDate = it
        }
        minDate?.let {
            datePickerDialog.datePicker.minDate = it
        }
        datePickerDialog.show()
    }

    private val dateDisplay = "dd-MM-yyyy"
    private val dateApi = "yyyy-MM-dd"

    private fun dateFormatDynamic(date: String?, from: String, to: String): String {
        if (date == null) return ""
        try {
            val from = SimpleDateFormat(from, Locale.US)
            val date = from.parse(date)
            val to = SimpleDateFormat(to, Locale.US)
            val result = to.format(date)
            return result
        } catch (e: Exception) {
            return ""
        }
    }

}