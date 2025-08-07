package com.narmada.measure.screens.badeli_serdi_report.view

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
import com.narmada.measure.databinding.ActivityBadeliSerdiReportBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.badeli_serdi_report.model.BadeliSerdiReportRequest
import com.narmada.measure.screens.badeli_serdi_report.viewmodel.BadeliSerdiReportRepository
import com.narmada.measure.screens.badeli_serdi_report.viewmodel.BadeliSerdiReportViewModel
import com.narmada.measure.screens.badeli_serdi_report.viewmodel.BadeliSerdiReportViewModelFactory
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerItem
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BadeliSerdiReportActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityBadeliSerdiReportBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: BadeliSerdiReportViewModel

    private lateinit var workingYear: String

    var zoneOfficerList: ArrayList<ZoneOfficerItem> = ArrayList()
    var zoneList: ArrayList<SupervisorZoneItem> = ArrayList()

    var zoneOfficerItems: ArrayList<String> = ArrayList()
    var zoneItems: ArrayList<String> = ArrayList()

    var zoneOfficerSpinnerDialog: SpinnerDialog? = null
    var zoneSpinnerDialog: SpinnerDialog? = null

    var zoneOfficerPosition: Int? = null
    var zonePosition: Int? = null

    var downloadID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.bareli_serdi_report)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.etFromDate.setOnClickListener(this)
        binding.etToDate.setOnClickListener(this)
        binding.etZoneOfficer.setOnClickListener(this)
        binding.etZone.setOnClickListener(this)
        binding.linearDownload.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        setupViewModel()

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
            binding.etZoneOfficer.setText(SharedPreferenceUtil.getString(Constants.OFFICER_NAME, ""))
            viewModel.supervisorZoneList(SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")!!)
        } else {
            viewModel.zoneOfficerList()
        }
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

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, BadeliSerdiReportViewModelFactory(
            BadeliSerdiReportRepository(retrofitService!!)
        ))[BadeliSerdiReportViewModel::class.java]

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

        viewModel.zoneOfficerListResponse.observe(this) {
            try {
                resetSupervisorSelection()
                resetZonesSelection()

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

                zoneList.addAll(it.data!! as List<SupervisorZoneItem>)
                for (item in zoneList) {
                    zoneItems.add(item.zoneName.toString())
                }
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
                    if (zoneItems.isNotEmpty()) {
                        binding.etZone.setText(zoneItems[0])
                        zonePosition = 0
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.badeliSerdiReportResponse.observe(this) {
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
                }
            }

            binding.etToDate.id -> {
                if (binding.etFromDate.text.toString().isNotEmpty()) {
                    val fromDate = SimpleDateFormat(dateDisplay, Locale.US).parse(binding.etFromDate.text.toString())?.time
                    showDatePicker(this, fromDate, Calendar.getInstance().timeInMillis) { date ->
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
                    binding.etZone.setText(item)
                    zonePosition = position

                    zoneSpinnerDialog!!.closeSpinerDialog()
                }
                zoneSpinnerDialog!!.showSpinerDialog()

            }

            binding.linearDownload.id -> {
                validateAndSubmitApi()
            }
        }
    }

    private fun validateAndSubmitApi() {

        if (binding.etFromDate.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_from_date))
            return
        }

        if (binding.etToDate.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_to_date))
            return
        }

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2") && zoneOfficerPosition == null) {
            Const.showToast(this, getString(R.string.please_select_supervisor))
            return
        }

        if (zonePosition == null) {
            Const.showToast(this, getString(R.string.please_select_zone))
            return
        }

        val reportRequest = BadeliSerdiReportRequest(
            working_year = workingYear,
            from_date = dateFormatDynamic(binding.etFromDate.text.toString(), dateDisplay, dateApi),
            to_date = dateFormatDynamic(binding.etToDate.text.toString(), dateDisplay, dateApi),
            zone_id = zoneList[zonePosition!!].zoneId.toString(),
        )

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
            reportRequest.supervisor_id = zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
        } else {
            reportRequest.supervisor_id = SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")
        }

        Log.e("TAG", "BadeliSerdiReportRequest: $reportRequest")

        viewModel.getBadeliSerdiReportApi(reportRequest)
    }

    private fun downloading(fileUrl: String) {
        var downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        downloadFolder = File(downloadFolder.path, "${getString(R.string.app_name)}/${getString(R.string.bareli_serdi_report)}")
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs()
        }

        val fileName = getFileNameFromUri(fileUrl)
        downloadID = PRDownloader.download(fileUrl, downloadFolder.path, fileName)
            .build()
            .setOnStartOrResumeListener {
//                Const.showToast(this@BadeliSerdiReportActivity, getString(R.string.please_wait))
                dialog.show()
            }
            .setOnProgressListener { progress -> // getting the progress of download
                val progressPer = progress.currentBytes * 100 / progress.totalBytes
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    dialog.dismiss()
                    openPDF(downloadFolder.path, fileName)
                    MediaScannerConnection.scanFile(this@BadeliSerdiReportActivity, arrayOf(downloadFolder.path), null, null)
                }

                override fun onError(error: com.downloader.Error?) {
                    dialog.dismiss()
                    downloadID = 0
                    Const.showToast(this@BadeliSerdiReportActivity, getString(R.string.something_went_wrong))
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