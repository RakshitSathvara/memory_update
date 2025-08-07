package com.narmada.measure.screens.bareliserdi.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUriUtils
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityBareliSerdiBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.bareliserdi.model.AddBareliSerdiRequest
import com.narmada.measure.screens.bareliserdi.model.CommonData
import com.narmada.measure.screens.bareliserdi.model.GetFormNumberRequest
import com.narmada.measure.screens.bareliserdi.model.PaniBandhDetail
import com.narmada.measure.screens.bareliserdi.model.PaniBandhDetailRequest
import com.narmada.measure.screens.bareliserdi.viewmodel.BareliSerdiRepository
import com.narmada.measure.screens.bareliserdi.viewmodel.BareliSerdiViewModel
import com.narmada.measure.screens.bareliserdi.viewmodel.BareliSerdiViewModelFactory
import com.narmada.measure.screens.dashboard.view.DashboardActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BareliSerdiActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityBareliSerdiBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: BareliSerdiViewModel

    var isKhetarCodeVerified: Boolean? = false
    private var commonData: CommonData? = null
    private lateinit var workingYear: String

    private var khetarCode: String? = null
    private var paniBandhDetail: PaniBandhDetail? = null
    private var kapatId: String? = null

    private var khetarFile: File? = null
    var downloadID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.serdi_form)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.etTarikh.setOnClickListener(this)
        binding.etSamay.setOnClickListener(this)
        binding.relativeSubmit.setOnClickListener(this)
        binding.btnFetchDetails.setOnClickListener(this)
        binding.etSerdiBareliTarikh.setOnClickListener(this)
        binding.etKapatName.setOnClickListener(this)
        binding.etPhotoUpload.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        setupViewModel()
        viewTextChangeListener()
        viewModel.getCommonDataApi(this)

        // auto-fill Current Date & Time
        binding.etTarikh.setText(
            SimpleDateFormat(
                dateDisplay,
                Locale.US
            ).format(Calendar.getInstance().time)
        )
        binding.etSamay.setText(
            SimpleDateFormat(
                timeDisplay,
                Locale.US
            ).format(Calendar.getInstance().time)
        )
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, BareliSerdiViewModelFactory(BareliSerdiRepository(retrofitService!!))
        )[BareliSerdiViewModel::class.java]

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
                commonData = it.data
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }

            val requestModel = GetFormNumberRequest(workingYear)
            viewModel.getFormNumberApi(this, requestModel)
        }

        viewModel.paniBandObservable.observe(this) {
            try {
                if (it.data != null) {
                    khetarCode = binding.etKhetarCode.text.toString().trim()
                    paniBandhDetail = it.data
                    setPaniBandhDetails()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.formNumberObservable.observe(this) {
            try {
                if (it.Burn_Form_No != null) {
                    binding.etBurnFormNo.setText(it.Burn_Form_No)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.addBareliSerdiObservable.observe(this) {
            try {
                showDialog(it.url.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }
    }

    private fun resetPaniBandhDetails() {
        khetarCode = null
        paniBandhDetail = null
        kapatId = null

        binding.etSabhaCode.setText("")
        binding.etSabhaName.setText("")
        binding.etSabhaGaam.setText("")
        binding.etMojeGaam.setText("")
        binding.etKulVistar.setText("")
        binding.etBareloVistar.setText("")
        binding.etLaamDate.setText("")
        binding.etSerdiBareliTarikh.setText("")
        binding.etLaamRopan.setText("")
        binding.etKapatName.setText("")
        binding.etItemName.setText("")
//        binding.etBurnFormNo.setText("")
    }

    private fun viewTextChangeListener() {
        binding.etKhetarCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.txtKhetarCode.visibility = View.GONE
                isKhetarCodeVerified = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun setPaniBandhDetails() {
        paniBandhDetail?.let {
            binding.txtKhetarCode.visibility = View.VISIBLE
            isKhetarCodeVerified = true
            binding.etSabhaCode.setText(it.AccountId)
            binding.etSabhaName.setText(it.AccountName)
            binding.etSabhaGaam.setText(it.VillageName)
            binding.etMojeGaam.setText(it.MojeVillageId)
            binding.etKulVistar.setText(it.RopanArea)
            binding.etLaamDate.setText(
                dateFormatDynamic(
                    it.ApproxRopanDate,
                    dateApiResponse,
                    dateDisplay
                )
            )
            binding.etLaamDate.setText(dateFormatDynamic(it.ApproxRopanDate, dateApi, dateDisplay))
            binding.etLaamRopan.setText(
                commonData?.mapniTypes?.firstOrNull { mapniTypes -> mapniTypes.id.toString() == it.LamRopan }?.name
                    ?: ""
            )  // show name using id from common api

            if (it.ItemId != null) {
                binding.etItemName.setText(it.ItemName + "/" + it.ItemId)
            }
            binding.etBareloVistar.setText(it.RopanArea)                // user can edit...
            binding.etSerdiBareliTarikh.setText("")                     // user select manually
            binding.etKapatName.setText("")                             // select from dropdown
//            binding.etBurnFormNo.setText("")                            // user will enter
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.btnFetchDetails.id -> {
                if (binding.etKhetarCode.text.toString().trim().isNotEmpty()) {

                    resetPaniBandhDetails()

                    val requestModel = PaniBandhDetailRequest(
                        workingYear,
                        binding.etKhetarCode.text.toString().trim()
                    )
                    viewModel.getPaniBandhDetailsApi(this, requestModel)
                } else {
                    Const.showToast(this, getString(R.string.enter_khetar_code))
                }
            }

            binding.etTarikh.id -> {
                showDatePicker(this, null) { date ->
                    binding.etTarikh.setText(date)
                }
            }

            binding.etSamay.id -> {
                showTimePicker(this) { time ->
                    binding.etSamay.setText(time)
                }
            }

            binding.etSerdiBareliTarikh.id -> {
                showDatePicker(this, Calendar.getInstance().time.time) { date ->
                    binding.etSerdiBareliTarikh.setText("$date")
//                    showTimePicker(this) {
//                        time ->
//                        binding.etSerdiBareliTarikh.setText("$date $time")
//                    }
                }
            }

            binding.etKapatName.id -> {

                val list: ArrayList<String> =
                    commonData?.kapats?.map { it.KapatName } as ArrayList<String>

                val zoneSpinnerDialog = SpinnerDialog(
                    this@BareliSerdiActivity,
                    list,
                    getString(R.string.kapat_name_guj),
                    getString(R.string.close)
                ) // With No Animation

                zoneSpinnerDialog.setCancellable(true)
                zoneSpinnerDialog.setShowKeyboard(false)
                zoneSpinnerDialog.bindOnSpinerListener { item, position ->
                    kapatId = commonData!!.kapats[position].KapatID
                    binding.etKapatName.setText(item)
                }
                zoneSpinnerDialog.showSpinerDialog()
            }

            binding.relativeSubmit.id -> {
                validateAndSubmitApi()
            }

            binding.etPhotoUpload.id -> {
                pickCameraImage()
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

    private fun validateAndSubmitApi() {

        if (binding.etTarikh.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.select_date))
            return
        }

        if (binding.etSamay.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.select_time))
            return
        }

        if (binding.etKhetarCode.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.enter_valid_khetar_code))
            return
        }

        if (paniBandhDetail == null) {
            Const.showToast(this, getString(R.string.get_details_using_khetar_code))
            return
        }

        val bareloVistar = binding.etBareloVistar.text.toString().trim().toFloatOrNull()
        if (bareloVistar == null || bareloVistar <= 0) {
            Const.showToast(this, getString(R.string.enter_barelo_vistar))
            return
        }

        if (binding.etSerdiBareliTarikh.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.enter_serdi_bareli_date))
            return
        }

        if (kapatId.isNullOrEmpty()) {
            Const.showToast(this, getString(R.string.select_kapat_name_no))
            return
        }

        if (binding.etBurnFormNo.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.enter_burn_form_number))
            return
        }

        if (khetarFile == null) {
            Const.showToast(this, getString(R.string.please_select_khetar_image))
            return
        }

        val addRequest = AddBareliSerdiRequest(
            account_id = paniBandhDetail?.AccountId,
            computer_code = khetarCode,
            moje_village_id = paniBandhDetail?.MojeVillageId,
            ropan_or_laam_date = paniBandhDetail?.ApproxRopanDate,
            sherdi_badeli_date = dateFormatDynamic(
                binding.etSerdiBareliTarikh.text.toString(),
                dateDisplay,
                dateApi
            ),
            mapni_type = paniBandhDetail?.LamRopan,
            item_id = paniBandhDetail?.ItemId,
            kapat_id = kapatId,
            total_area = paniBandhDetail?.RopanArea,
            burned_area = binding.etBareloVistar.text.toString().trim(),
            working_year = workingYear,
            burn_form_no = binding.etBurnFormNo.text.toString().trim(),
            date = dateFormatDynamic(binding.etTarikh.text.toString(), dateDisplay, dateApi),
            time = dateFormatDynamic(binding.etSamay.text.toString(), timeDisplay, timeApi),
        )

        Log.e("TAG", "AddBareliSerdiRequest: $addRequest")

        // TODO: NEED TO CHECK FOR API CALL >>>>
        viewModel.addBareliSerdiApi(this, addRequest, khetarFile!!)

    }

    private fun showDialog(url: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_submit_bareli_serdi)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout
        val relativeShare = dialog.findViewById(R.id.relative_share) as RelativeLayout
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            val i = Intent(this, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

        relativeShare.setOnClickListener {
            downloading(url)
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            val i = Intent(this, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

        dialog.show()
    }

    private fun showDatePicker(
        context: Context,
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
                val dat = String.format(Locale.US,
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
        datePickerDialog.show()
    }

    private fun showTimePicker(context: Context, onTimeSelected: (time: String) -> Any) {
        val c = Calendar.getInstance()
        val mTimePicker: TimePickerDialog
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->

                onTimeSelected(String.format(Locale.US,"%02d:%02d", hourOfDay, minute))

            }, hour, minute, true
        )
        mTimePicker.show()
    }

    private val dateApiResponse = "yyyy-MM-dd"
    private val dateDisplay = "dd-MM-yyyy"
    private val dateApi = "yyyy-MM-dd"
    private val timeDisplay = "HH:mm"
    private val timeApi = "HH:mm:ss"

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

    private fun downloading(fileUrl: String) {
        var downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        downloadFolder = File(downloadFolder.path, "${getString(R.string.app_name)}/${getString(R.string.serdi_form)}")
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs()
        }

        val fileName = getFileNameFromUri(fileUrl)
        downloadID = PRDownloader.download(fileUrl, downloadFolder.path, fileName)
            .build()
            .setOnStartOrResumeListener {
                Const.showToast(this@BareliSerdiActivity, getString(R.string.please_wait))
            }
            .setOnProgressListener { progress -> // getting the progress of download
                val progressPer = progress.currentBytes * 100 / progress.totalBytes
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    openPDF(downloadFolder.path, fileName)
                    MediaScannerConnection.scanFile(this@BareliSerdiActivity, arrayOf(downloadFolder.path), null, null)
                }

                override fun onError(error: com.downloader.Error?) {
                    downloadID = 0
                    Const.showToast(this@BareliSerdiActivity, getString(R.string.something_went_wrong))
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