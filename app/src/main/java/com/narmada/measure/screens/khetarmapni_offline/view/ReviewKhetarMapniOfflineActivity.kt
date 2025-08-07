package com.narmada.measure.screens.khetarmapni_offline.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUriUtils
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityReviewKhetarMapniOfflineBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.room.AppDatabase
import com.narmada.measure.room.entity.OfflineMapni
import com.narmada.measure.screens.dashboard.view.DashboardActivity
import com.narmada.measure.screens.khetarmapni_offline.model.OfflineMapniModel
import com.narmada.measure.screens.khetarmapni_offline.viewmodel.KhetarMapniOfflineRepository
import com.narmada.measure.screens.khetarmapni_offline.viewmodel.KhetarMapniOfflineViewModel
import com.narmada.measure.screens.khetarmapni_offline.viewmodel.KhetarMapniOfflineViewModelFactory
import com.narmada.measure.screens.login.view.LoginActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import java.io.File


class ReviewKhetarMapniOfflineActivity : AppCompatActivity(), View.OnClickListener {

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val roomService by lazy { AppDatabase.getInstance(applicationContext) }
    private val binding by lazy { ActivityReviewKhetarMapniOfflineBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    private lateinit var viewModel: KhetarMapniOfflineViewModel
    private var mapniData: OfflineMapniModel? = null
    private var khetarFile: File? = null
    private var mapFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.relativeSubmit.setOnClickListener(this)
        toolbarBinding.ivBack.setOnClickListener(this)
        binding.etPhotoUpload.setOnClickListener(this)
        toolbarBinding.txtTitle.setText(R.string.offline_khetar_mapni)

        mapniData = intent.getSerializable("data", OfflineMapniModel::class.java)
        mapFile = File(mapniData!!.mapImage!!)

        setupViewModel()
        setMapniData()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            KhetarMapniOfflineViewModelFactory(
                KhetarMapniOfflineRepository(roomService)
            )
        )[KhetarMapniOfflineViewModel::class.java]

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

        viewModel.insertOfflineMapni.observe(this) {
            showDialog()
        }
    }

    private fun setMapniData() {
        binding.etKhetarCode.setText(mapniData!!.khetarCode.toString())
        binding.etSabhaCode.setText(mapniData!!.sabhasadCode.toString())
        binding.etVistar.setText(mapniData!!.ropanArea.toString())
        binding.etNorthKhetarName.setText(mapniData?.northKhetarName ?: "")
        binding.etSouthKhetarName.setText(mapniData?.southKhetarName ?: "")
        binding.etEastKhetarName.setText(mapniData?.eastKhetarName ?: "")
        binding.etWestKhetarName.setText(mapniData?.westKhetarName ?: "")
        binding.etChasNumber.setText(mapniData?.chasNumber ?: "")
        binding.etChasDirection.setText(mapniData?.chasDirectionName ?: "")

        Glide.with(this).load(File(mapniData!!.mapImage!!)).into(binding.imgMapniPhoto)
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
                    mapniData?.khetarImage = khetarFile?.absolutePath
                    viewModel.insertOfflineMapni(
                        OfflineMapni(
                            id = null,
                            khetarCode = mapniData!!.khetarCode.toString(),
                            sabhaCode = mapniData!!.sabhasadCode.toString(),
                            polygon = mapniData!!.polygonJson.toString(),
                            polygonImage = mapniData!!.mapImage.toString(),
                            khetarImage = mapniData!!.khetarImage.toString(),
                            totalAcre = mapniData!!.ropanArea.toString(),
                            northKhetarName = mapniData!!.northKhetarName.toString(),
                            southKhetarName = mapniData!!.southKhetarName.toString(),
                            eastKhetarName = mapniData!!.eastKhetarName.toString(),
                            westKhetarName = mapniData!!.westKhetarName.toString(),
                            chasNumber = mapniData!!.chasNumber.toString(),
                            chasDirection = mapniData!!.chasDirection.toString(),
                        )
                    )
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

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_submitted)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            if (SharedPreferenceUtil.getBoolean(Constants.IS_LOGIN, false)) {
                val i = Intent(this, DashboardActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            } else {
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            if (SharedPreferenceUtil.getBoolean(Constants.IS_LOGIN, false)) {
                val i = Intent(this, DashboardActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            } else {
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
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
}


