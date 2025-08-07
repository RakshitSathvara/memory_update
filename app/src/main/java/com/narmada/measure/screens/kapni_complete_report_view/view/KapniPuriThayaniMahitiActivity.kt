package com.narmada.measure.screens.kapni_complete_report_view.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityKapniPuriThayaniMahitiBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.kapni_complete_report.model.KhetarCode
import com.narmada.measure.screens.kapni_complete_report_view.model.KapniPuriThayaniMahitiIntent
import com.narmada.measure.screens.kapni_complete_report_view.viewmodel.KapniPuriThayaniMahitiRepository
import com.narmada.measure.screens.kapni_complete_report_view.viewmodel.KapniPuriThayaniMahitiViewModel
import com.narmada.measure.screens.kapni_complete_report_view.viewmodel.KapniPuriThayaniMahitiViewModelFactory
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerItem
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.io.File

class KapniPuriThayaniMahitiActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityKapniPuriThayaniMahitiBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: KapniPuriThayaniMahitiViewModel

    private lateinit var workingYear: String

    private var zoneOfficerList: ArrayList<ZoneOfficerItem> = ArrayList()
    private var zoneList: ArrayList<SupervisorZoneItem> = ArrayList()
    private var khetarCodeList: ArrayList<KhetarCode> = ArrayList()

    private var zoneOfficerItems: ArrayList<String> = ArrayList()
    private var zoneItems: ArrayList<String> = ArrayList()
    private var khetarCodeItems: ArrayList<String> = ArrayList()

    private var zoneOfficerSpinnerDialog: SpinnerDialog? = null
    private var zoneSpinnerDialog: SpinnerDialog? = null
    private var khetarCodeSpinnerDialog: SpinnerDialog? = null

    private var zoneOfficerPosition: Int? = null
    private var zonePosition: Int? = null
    var khetarCodePosition: Int? = null

    var downloadID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.kapni_puri_thayani_mahiti)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.etZoneOfficer.setOnClickListener(this)
        binding.etZone.setOnClickListener(this)
        binding.btnView.setOnClickListener(this)
        binding.btnSabhasadCode.setOnClickListener(this)
        binding.etKhetarCode.setOnClickListener(this)
        binding.linearDownload.setOnClickListener(this)

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

        binding.etSabhasadCode.addTextChangedListener { text ->
            resetKhetarCodeSelection()
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

    private fun resetKhetarCodeSelection() {
        binding.etKhetarCode.setText("")
        khetarCodeList.clear()
        khetarCodeItems.clear()
        khetarCodePosition = null
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            KapniPuriThayaniMahitiViewModelFactory(KapniPuriThayaniMahitiRepository(retrofitService!!))
        )[KapniPuriThayaniMahitiViewModel::class.java]

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

        viewModel.khetarCodeListResponse.observe(this) {
            try {
                resetKhetarCodeSelection()

                khetarCodeList.addAll(it.data!! as List<KhetarCode>)
                for (item in khetarCodeList) {
                    khetarCodeItems.add(item.computerCode.toString())
                }

                if (khetarCodeList.isEmpty()) {
                    Const.showSnackBar(this, it.message ?: getString(R.string.something_went_wrong))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.kapniPuriMahitiDownloadResponse.observe(this) {
            try {
                it.url?.let { url ->
                    if (url.isNotEmpty()) {
                        for (i in url.indices) {
                            downloading(url[i])
                        }
                    }
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
                    binding.etZone.setText(item)
                    zonePosition = position

                    zoneSpinnerDialog!!.closeSpinerDialog()
                }
                zoneSpinnerDialog!!.showSpinerDialog()

            }

            binding.btnSabhasadCode.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    Const.showSnackBar(this, getString(R.string.please_select_supervisor))
                    return
                }

                if (zonePosition == null) {
                    Const.showSnackBar(this, getString(R.string.please_select_zone))
                    return
                }

                if (binding.etSabhasadCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_enter_sabhasad_number))
                    return
                }

                Const.closeKeyboard(this)
                viewModel.getKhetarCodeList(
                    binding.etSabhasadCode.text.toString().trim(),
                    workingYear
                )
            }

            binding.etKhetarCode.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    Const.showSnackBar(this, getString(R.string.please_select_supervisor))
                    return
                }

                if (zonePosition == null) {
                    Const.showSnackBar(this, getString(R.string.please_select_zone))
                    return
                }

                if (binding.etSabhasadCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_enter_sabhasad_number))
                    return
                }

                if (khetarCodeItems.isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.enter_valid_sabha_code))
                    return
                }

                khetarCodeSpinnerDialog = SpinnerDialog(
                    this,
                    khetarCodeItems,
                    getString(R.string.khetar_code_guj),
                    getString(R.string.close)
                )
                khetarCodeSpinnerDialog!!.setCancellable(true)
                khetarCodeSpinnerDialog!!.setShowKeyboard(false)
                khetarCodeSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etKhetarCode.setText(item)
                    khetarCodePosition = position

                    khetarCodeSpinnerDialog!!.closeSpinerDialog()
                }
                khetarCodeSpinnerDialog!!.showSpinerDialog()

            }

            binding.btnView.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    Const.showToast(this, getString(R.string.please_select_supervisor))
                    return
                }

                if (zonePosition == null) {
                    Const.showToast(this, getString(R.string.please_select_zone))
                    return
                }

                if (binding.etSabhasadCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_enter_sabhasad_number))
                    return
                }

                if (binding.etKhetarCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_select_khetar_code))
                    return
                }

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

                val intentData = KapniPuriThayaniMahitiIntent(
                    workingYear = workingYear,
                    supervisorId = supervisorId,
                    supervisorName = supervisorName,
                    zone = zoneList[zonePosition!!],
                    khetarCode = binding.etKhetarCode.text.toString(),
                    sabhasadCode = binding.etSabhasadCode.text.toString()
                )

                val intent = Intent(this, KapniPuriThayaniMahitiDetailActivity::class.java)
                intent.putExtra("intent", intentData)
                startActivity(intent)
            }

            binding.linearDownload.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    Const.showToast(this, getString(R.string.please_select_supervisor))
                    return
                }

                if (zonePosition == null) {
                    Const.showToast(this, getString(R.string.please_select_zone))
                    return
                }

                if (binding.etSabhasadCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_enter_sabhasad_number))
                    return
                }

                if (binding.etKhetarCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_select_khetar_code))
                    return
                }

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

                val intentData = KapniPuriThayaniMahitiIntent(
                    workingYear = workingYear,
                    supervisorId = supervisorId,
                    supervisorName = supervisorName,
                    zone = zoneList[zonePosition!!],
                    khetarCode = binding.etKhetarCode.text.toString(),
                    sabhasadCode = binding.etSabhasadCode.text.toString()
                )

                viewModel.kapniCompleteReportDownload(intentData)

            }
        }
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
                   // openPDF(downloadFolder.path, fileName)
                    MediaScannerConnection.scanFile(
                        this@KapniPuriThayaniMahitiActivity,
                        arrayOf(downloadFolder.path),
                        null,
                        null
                    )

                    Const.showToast(
                        this@KapniPuriThayaniMahitiActivity,
                        "File Download Successfully"
                    )
                }

                override fun onError(error: com.downloader.Error?) {
                    dialog.dismiss()
                    downloadID = 0
                    Const.showToast(
                        this@KapniPuriThayaniMahitiActivity,
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