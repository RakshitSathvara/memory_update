package com.narmada.measure.screens.admin_user.register_supervisor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.narmada.measure.R
import com.narmada.measure.database.DBHelper
import com.narmada.measure.databinding.ActivityRegisterSupervisorBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.admin_user.admin_dashboard.view_model.AdminRepository
import com.narmada.measure.screens.admin_user.admin_dashboard.view_model.AdminViewModel
import com.narmada.measure.screens.admin_user.admin_dashboard.view_model.AdminViewModelFactory
import com.narmada.measure.screens.admin_user.scan_face.ScanFaceActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.Constant
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import java.io.File
import java.io.FileOutputStream


class RegisterSupervisorActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityRegisterSupervisorBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val retrofitService by lazy { RetrofitService.getAdminInstance() }
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }

    lateinit var viewModel: AdminViewModel

    private var existingSupervisorIds: List<String> = ArrayList()
    private var dbHelper: DBHelper? = null

    private var faceBitmapLocal: Bitmap? = null
    private var faceEmbeddingLocal: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.imgFace.setOnClickListener(this)
        toolbarBinding.txtTitle.text = getString(R.string.chehro_odakh_nondhni)

        dbHelper = DBHelper(this)
        existingSupervisorIds = dbHelper!!.getExistingSupervisorIds()

        binding.etSupervisor.setText(SharedPreferenceUtil.getString(Constants.OFFICER_NAME, ""))
        binding.etSupervisorCode.setText(
            SharedPreferenceUtil.getString(
                Constants.ZONE_SUPERVISOR_ID,
                ""
            )
        )

        setupViewModel()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, AdminViewModelFactory(AdminRepository(retrofitService!!))
        )[AdminViewModel::class.java]

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

        viewModel.addFaceResponse.observe(this) {
            if (it.response.equals(Constants.SUCCESS)) {
                val supervisorId: String = binding.etSupervisorCode.getText().toString().trim()
                val supervisorName: String = binding.etSupervisor.getText().toString().trim()
                val isInserted = dbHelper!!.insertFace(
                    supervisorName,
                    supervisorId,
                    faceEmbeddingLocal,
                    it.data?.faceImagePath ?: ""
                )

                if (isInserted) {
                    Toast.makeText(this@RegisterSupervisorActivity, it.message ?: getString(R.string.supervisor_registered_success), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterSupervisorActivity, getString(R.string.supervisor_registered_failed), Toast.LENGTH_SHORT).show()
                }
            } else {
                showSnackBar(this, it.message.toString())
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {
            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.imgFace.id -> {
                val intent = Intent(this@RegisterSupervisorActivity, ScanFaceActivity::class.java)
                intent.putExtra("isRegister", true)
                faceResultLauncher.launch(intent)
            }

            binding.btnSubmit.id -> {
                val supervisorId: String = binding.etSupervisorCode.getText().toString().trim()
                if (binding.etSupervisor.text.toString().isEmpty()) {
                    showSnackBar(this, getString(R.string.supervisor_name_not_found))
                } else if (binding.etSupervisor.text.toString().isEmpty()) {
                    showSnackBar(this, getString(R.string.supervisor_code_not_found))
                } else if (faceBitmapLocal == null) {
                    showSnackBar(this, getString(R.string.please_select_profile))
                } else if (existingSupervisorIds.contains(supervisorId)) {
                    showSnackBar(this, getString(R.string.supervisor_code_already_exist))
                } else {
                    saveBitmapToFile(
                        this,
                        faceBitmapLocal!!,
                        System.currentTimeMillis().toString() + "face.png",
                        supervisorId
                    )
                }
            }

        }
    }

    private var faceResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            faceBitmapLocal = Constant.faceBitmap
            faceEmbeddingLocal = Constant.faceEmbeeding
            binding.imgFace.setImageBitmap(faceBitmapLocal)
            Constant.faceBitmap = null
            Constant.faceEmbeeding = null
        } else {
            Constant.faceBitmap = null
            Constant.faceEmbeeding = null
            binding.imgFace.setImageResource(R.drawable.ic_scan)

            faceBitmapLocal = null
            faceEmbeddingLocal = null
        }
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String, supervisorId: String) {
        val faceFile = File(context.cacheDir, filename)
        FileOutputStream(faceFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
        }

        if (faceFile != null) {
            viewModel.addSupervisor(
                Const.getRBofText(supervisorId),
                Const.getRBofText(getFaceDataAsString(faceEmbeddingLocal!!)),
                faceFile
            )
        }
    }

    private fun getFaceDataAsString(embedding: Any): String {
        val floatList = embedding as Array<FloatArray>
        var embeddingString = ""
        for (f in floatList[0]) {
            embeddingString += "$f,"
        }

        return embeddingString
    }

    companion object {
        private const val TAG = "FaceRegisterActivity"
    }

}