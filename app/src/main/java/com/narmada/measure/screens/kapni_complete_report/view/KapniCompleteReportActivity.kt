package com.narmada.measure.screens.kapni_complete_report.view

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityKapniCompleteReportBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.admin_user.scan_face.ScanFaceActivity
import com.narmada.measure.screens.kapni_complete_report.model.KapniCompleteReportRequest
import com.narmada.measure.screens.kapni_complete_report.model.KhetarCode
import com.narmada.measure.screens.kapni_complete_report.model.WeightListItem
import com.narmada.measure.screens.kapni_complete_report.view.adapter.KapniKhetarInfoAdapter
import com.narmada.measure.screens.kapni_complete_report.viewmodel.KapniCompleteReportRepository
import com.narmada.measure.screens.kapni_complete_report.viewmodel.KapniCompleteReportViewModel
import com.narmada.measure.screens.kapni_complete_report.viewmodel.KapniCompleteReportViewModelFactory
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerItem
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Constant
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.io.File
import java.io.FileOutputStream


class KapniCompleteReportActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityKapniCompleteReportBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: KapniCompleteReportViewModel

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

    var zoneOfficerPosition: Int? = null
    var zonePosition: Int? = null
    var khetarCodePosition: Int? = null

    private var faceBitmapLocal: Bitmap? = null
    private var faceEmbeddingLocal: Any? = null

    private var weightList: ArrayList<WeightListItem> = ArrayList()
    lateinit var weightListAdapter: KapniKhetarInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.kapni_puri_aheval)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.etZoneOfficer.setOnClickListener(this)
        binding.etZone.setOnClickListener(this)
        binding.btnSabhasadCode.setOnClickListener(this)
        binding.etKhetarCode.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.linearFaceRecognition.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        setupViewModel()

        binding.recyclerInfo.layoutManager = LinearLayoutManager(this)
        weightListAdapter = KapniKhetarInfoAdapter(weightList)
        binding.recyclerInfo.adapter = weightListAdapter

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

        binding.etSabhaCode.addTextChangedListener { text ->
            resetKhetarCodeSelection()
            resetKapniReportNumber()
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

    private fun resetKapniReportNumber() {
        // reset weight list adapter
        weightList.clear()
        weightListAdapter.notifyDataSetChanged()

        binding.etKapniReportNumber.setText("")
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, KapniCompleteReportViewModelFactory(
                KapniCompleteReportRepository(retrofitService!!)
            )
        )[KapniCompleteReportViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            Const.showSnackBar(this, it)
        }

        viewModel.errorDialogMessage.observe(this) {
            showInfoDialog(it)
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
                resetKhetarCodeSelection()
                resetKapniReportNumber()

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
                resetKapniReportNumber()

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

        viewModel.weightListResponse.observe(this) {
            try {
                resetKapniReportNumber()

                binding.etKapniReportNumber.setText(it.data?.reportNo ?: "")

                weightList.clear()
                it.data?.weightList?.let { it1 -> weightList.addAll(it1) }
                weightListAdapter.notifyDataSetChanged()

                if (it.data?.weightList.isNullOrEmpty()) {
                    it.message?.let { message -> Const.showSnackBar(this, message) }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.kapniCompleteReportResponse.observe(this) {
            try {
                val finalMessage =
                    "${it.message ?: ""}\n\n${getString(R.string.kapni_report_number)} : ${it.data?.reportNo ?: ""}"
                showSuccessDialog(finalMessage)
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_common)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val tvMessage: TextView = dialog.findViewById(R.id.tv_message)
        val tvClose: TextView = dialog.findViewById(R.id.tv_close)
        val relativeContinue: RelativeLayout = dialog.findViewById(R.id.btn_close)
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)

        tvMessage.text = message
        tvClose.text = getString(R.string.cont)

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun showInfoDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_common)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val tvMessage: TextView = dialog.findViewById(R.id.tv_message)
        val tvClose: TextView = dialog.findViewById(R.id.tv_close)
        val relativeContinue: RelativeLayout = dialog.findViewById(R.id.btn_close)
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)

        tvMessage.text = message
        tvClose.text = getString(R.string.okay)

        relativeContinue.setOnClickListener {
            dialog.dismiss()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
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

                if (binding.etSabhaCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_enter_sabhasad_number))
                    return
                }

                Const.closeKeyboard(this)
                viewModel.getKhetarCodeList(binding.etSabhaCode.text.toString().trim(), workingYear)
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

                if (binding.etSabhaCode.text.toString().trim().isEmpty()) {
                    Const.showSnackBar(this, getString(R.string.please_enter_sabhasad_number))
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

                    resetKapniReportNumber()

                    binding.etKhetarCode.setText(item)
                    khetarCodePosition = position

                    khetarCodeSpinnerDialog!!.closeSpinerDialog()
                    viewModel.getWeightList(
                        khetarCodeList[khetarCodePosition!!].computerCode.toString(),
                        workingYear
                    )
                }
                khetarCodeSpinnerDialog!!.showSpinerDialog()

            }

            binding.btnSubmit.id -> {
                validateAndSubmitApi()
            }

            binding.linearFaceRecognition.id -> {
                if (!isPermissionGranted()) {
                    requestPermission()
                    return
                }

                val intent = Intent(this@KapniCompleteReportActivity, ScanFaceActivity::class.java)
                faceResultLauncher.launch(intent)
            }
        }
    }

    private var faceResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                binding.linearFaceInfo.visibility = View.VISIBLE

                binding.tvSupervisorName.text = data?.getStringExtra("name")
                binding.tvSupervisorCode.text = data?.getStringExtra("id")

                faceBitmapLocal = Constant.faceBitmap
                faceEmbeddingLocal = Constant.faceEmbeeding
                binding.imgFace.setImageBitmap(faceBitmapLocal)

                Constant.faceBitmap = null
                Constant.faceEmbeeding = null
            } else {
                binding.linearFaceInfo.visibility = View.GONE
                Constant.faceBitmap = null
                Constant.faceEmbeeding = null

                faceBitmapLocal = null
                faceEmbeddingLocal = null
            }
        }

    private fun validateAndSubmitApi() {

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                .equals("2") && zoneOfficerPosition == null
        ) {
            Const.showToast(this, getString(R.string.please_select_supervisor))
            return
        }

        if (zonePosition == null) {
            Const.showSnackBar(this, getString(R.string.please_select_zone))
            return
        }

        if (binding.etSabhaCode.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_enter_sabhasad_number))
            return
        }

        if (khetarCodePosition == null) {
            Const.showToast(this, getString(R.string.please_select_khetar_code))
            return
        }

        if (binding.etKapniReportNumber.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.kapni_report_number_view_only_not_found))
            return
        }

        if (binding.etKapniReportNumberEnter.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_enter_kapni_report_number))
            return
        }

        if (validateKapniReportNumber()) {
            Const.showToast(this, getString(R.string.please_enter_valid_kapni_report_number))
            return
        }

        if (faceBitmapLocal == null) {
            Const.showSnackBar(this, getString(R.string.please_select_profile))
            return
        }

        val supervisorId =
            if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
            } else {
                SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")!!
            }

        val reportRequest = KapniCompleteReportRequest(
            working_year = workingYear,
            zone_id = zoneList[zonePosition!!].zoneId.toString(),
            account_id = binding.etSabhaCode.text.toString().trim(),
            computer_code = khetarCodeList[khetarCodePosition!!].computerCode.toString(),
            report_no_view_only = binding.etKapniReportNumber.text.toString(),
            report_no = binding.etKapniReportNumberEnter.text.toString(),
            supervisor_id = supervisorId
        )

        Log.e("TAG", "KapniCompleteReportRequest: $reportRequest")

        saveBitmapToFile(
            this,
            faceBitmapLocal!!,
            System.currentTimeMillis().toString() + "face.png",
            reportRequest
        )
    }

    private fun validateKapniReportNumber(): Boolean {
        val items = binding.etKapniReportNumberEnter.text.toString().split(",")
        return items.any { it.trim().isEmpty() }
    }

    private fun saveBitmapToFile(
        context: Context,
        bitmap: Bitmap,
        filename: String,
        reportRequest: KapniCompleteReportRequest
    ) {
        val faceFile = File(context.cacheDir, filename)
        FileOutputStream(faceFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
        }

        viewModel.addKapniCompleteReportApi(
            reportRequest,
            faceFile
        )
    }


    private fun isPermissionGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = arrayOf(Manifest.permission.CAMERA)
        requestPermissions(permission, 121)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handlePermissionsResult(requestCode, permissions, grantResults)
    }

    private fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            121 -> {
                if (isPermissionGranted())
                    binding.linearFaceRecognition.performClick()
                else
                    Const.showToast(this, getString(R.string.we_need_this_permission))
            }
        }
    }


}