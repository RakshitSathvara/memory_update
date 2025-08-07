package com.narmada.measure.screens.khetarmapni.view

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
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
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUriUtils
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityReviewKhetarMapniBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.room.AppDatabase
import com.narmada.measure.screens.dashboard.view.DashboardActivity
import com.narmada.measure.screens.khetarmapni.model.KhetarMapniRequest
import com.narmada.measure.screens.khetarmapni.viewmodel.KhetarMapniRepository
import com.narmada.measure.screens.khetarmapni.viewmodel.KhetarMapniViewModel
import com.narmada.measure.screens.khetarmapni.viewmodel.KhetarMapniViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.Const.showToast
import com.narmada.measure.utils.LoadingDialog
import java.io.File


class ReviewKhetarMapniActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: KhetarMapniViewModel
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val roomService by lazy { AppDatabase.getInstance(applicationContext) }
    private val binding by lazy { ActivityReviewKhetarMapniBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    private var mapniData: KhetarMapniRequest? = null
    private lateinit var mapImage: String
    private var khetarFile: File? = null
    private var mapFile: File? = null
    var downloadID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        PRDownloader.initialize(this);

        binding.relativeSubmit.setOnClickListener(this)
        toolbarBinding.ivBack.setOnClickListener(this)
        binding.etPhotoUpload.setOnClickListener(this)
        toolbarBinding.txtTitle.setText(R.string.farm_measure_guj)

        mapniData = intent.getSerializable("data", KhetarMapniRequest::class.java)
        mapImage = intent.getStringExtra("mapImage")!!
        mapFile = File(mapImage)

        setMapniData()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, KhetarMapniViewModelFactory(
                KhetarMapniRepository(retrofitService!!, roomService),
                selectedYear = ""
            )
        )[KhetarMapniViewModel::class.java]

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

        viewModel.addKhetarMapniResponse.observe(this) {
            try {
                showSubmitDialog(it.url.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }
    }

    private fun setMapniData() {
        binding.etZone.setText(mapniData!!.zoneOfficerName.toString())
        binding.etRopanSeason.setText(mapniData!!.workingYear.toString())
        binding.etPilanSeason.setText(mapniData!!.pilanSeason.toString())
        binding.etKhetarCode.setText(mapniData!!.computerCode.toString())
        binding.etMojeGaam.setText(mapniData!!.mojeVillageName.toString())
        binding.etNondhGaam.setText(mapniData!!.nondhVillageName.toString())
        binding.etSabhaCode.setText(mapniData!!.sabhaCode.toString())
        binding.etSabhaName.setText(mapniData!!.sabhaName.toString())
        binding.etSabhaGaam.setText(mapniData!!.sabhaGaam.toString())
        binding.etGaamCode.setText(mapniData!!.gaamCode.toString())
        binding.etShareNo.setText(mapniData!!.sherTotal.toString())
        binding.etLaamDate.setText(mapniData!!.approxRopanDate.toString())
        binding.etLaamRopan.setText(mapniData!!.mapniName.toString())
        binding.etKhetarName.setText(mapniData!!.khetarName.toString())
        binding.etItemName.setText(mapniData!!.itemName.toString())
        binding.etPiyatSadhan.setText(mapniData!!.piyatSadhanName.toString())
        binding.etSerialNo.setText(mapniData!!.serialNumber.toString())
        binding.etBiyaranName.setText(mapniData!!.biyaranName.toString())
        binding.etOrganic.setText(mapniData!!.notOrganicName.toString())
        binding.etVistar.setText(mapniData!!.ropanArea.toString())

        binding.etNorthKhetarName.setText(mapniData!!.northKhetarName.toString())
        binding.etSouthKhetarName.setText(mapniData!!.southKhetarName.toString())
        binding.etEastKhetarName.setText(mapniData!!.eastKhetarName.toString())
        binding.etWestKhetarName.setText(mapniData!!.westKhetarName.toString())
        binding.etChasNumber.setText(mapniData!!.chasNumber.toString())
        binding.etChasDirection.setText(mapniData!!.chasDirectionName.toString())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            toolbarBinding.ivBack.id -> {
                showWarningDialog()
            }

            binding.relativeSubmit.id -> {
                if (khetarFile == null) {
                    showSnackBar(this, getString(R.string.please_select_khetar_image))
                } else {
                    viewModel.addKhetarMapni(mapniData!!, mapFile, khetarFile!!)
                }
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
            if (it.resultCode == RESULT_OK) {
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

    private fun showSubmitDialog(url: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_submit_mapni)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout
        val relativeShare = dialog.findViewById(R.id.relative_share) as RelativeLayout
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            val i = Intent(this, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        relativeShare.setOnClickListener {
            downloading(url)
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            val i = Intent(this, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        dialog.show()
    }

    private fun showWarningDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_warning)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showWarningDialog()
    }

    private fun downloading(fileUrl: String) {
        var downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        downloadFolder = File(downloadFolder.path, "${getString(R.string.app_name)}/${getString(R.string.farm_measure_guj)}")
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs()
        }

        val fileName = getFileNameFromUri(fileUrl)
        downloadID = PRDownloader.download(fileUrl, downloadFolder.path, fileName)
            .build()
            .setOnStartOrResumeListener {
                showToast(this@ReviewKhetarMapniActivity, getString(R.string.please_wait))
            }
            .setOnProgressListener { progress -> // getting the progress of download
                val progressPer = progress.currentBytes * 100 / progress.totalBytes
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    openPDF(downloadFolder.path, fileName)
                    MediaScannerConnection.scanFile(this@ReviewKhetarMapniActivity, arrayOf(downloadFolder.path), null, null)
                }

                override fun onError(error: Error?) {
                    downloadID = 0
                    showToast(this@ReviewKhetarMapniActivity, getString(R.string.something_went_wrong))
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


