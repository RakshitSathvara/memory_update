package com.narmada.measure.screens.admin_user.admin_dashboard.view

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.narmada.measure.R
import com.narmada.measure.database.DBHelper
import com.narmada.measure.databinding.ActivityAdminDashboardBinding
import com.narmada.measure.face_recognization.Supervisor
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.admin_user.admin_dashboard.view_model.AdminRepository
import com.narmada.measure.screens.admin_user.admin_dashboard.view_model.AdminViewModel
import com.narmada.measure.screens.admin_user.admin_dashboard.view_model.AdminViewModelFactory
import com.narmada.measure.screens.admin_user.register_supervisor.RegisterSupervisorActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showToast
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil

class AdminDashboardActivity : AppCompatActivity(), View.OnClickListener {

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val binding by lazy { ActivityAdminDashboardBinding.inflate(layoutInflater) }

    private val retrofitService by lazy { RetrofitService.getAdminInstance() }
    private lateinit var viewModel: AdminViewModel
    var dbHelper: DBHelper? = null
    private val supervisor: ArrayList<Supervisor> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dbHelper = DBHelper(this)
        binding.txtName.text = Constants.ADMIN_NAME
        binding.linearFaceRegister.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)

        fetchAndShowData()
        setupViewModel()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            AdminViewModelFactory(AdminRepository(retrofitService!!))
        )[AdminViewModel::class.java]

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

        viewModel.deleteFaceResponse.observe(this) {
            dbHelper!!.deleteSupervisor(binding.tvCode.text.toString().toInt())
            showToast(this, it.message ?: "")
            binding.linearRegisteredFace.visibility = View.GONE
            binding.linearFaceRegister.visibility = View.VISIBLE
        }

    }

    private fun fetchAndShowData() {
        val supervisorId = SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")
        val supervisorList: List<Supervisor> = dbHelper!!.getAllSupervisor(supervisorId)
        supervisor.clear()
        supervisor.addAll(supervisorList)

        if (supervisor.isEmpty()) {
            binding.linearFaceRegister.visibility = View.VISIBLE
            binding.linearRegisteredFace.visibility = View.GONE
        } else {
            binding.linearFaceRegister.visibility = View.GONE
            binding.linearRegisteredFace.visibility = View.VISIBLE

            binding.tvSupervisor.text = supervisor[0].name
            binding.tvCode.text = supervisor[0].code
            Glide.with(this).load(supervisor[0].image).into(binding.roundedImageView)
        }
    }

    private fun deleteConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_delete_confirmation)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeYes: RelativeLayout = dialog.findViewById(R.id.relative_yes)
        val relativeNo: RelativeLayout = dialog.findViewById(R.id.relative_no)
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)

        relativeYes.setOnClickListener {
            dialog.dismiss()
            viewModel.deleteSupervisor(binding.tvCode.text.toString())

        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        relativeNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            binding.ivBack.id -> {
                finish()
            }

            binding.ivDelete.id -> {
                deleteConfirmationDialog()
            }

            binding.linearFaceRegister.id -> {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || (checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_DENIED)
                ) {
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, 121)
                } else {
                    if (Const.isInternetAvailable(this)) {
                        val intent = Intent(this, RegisterSupervisorActivity::class.java)
                        startActivity(intent)
                    } else {
                        Const.showSnackBar(this, getString(R.string.check_internet_connection))
                    }
                }
            }


        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handlePermissionsResult(requestCode, permissions, grantResults)
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            121 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (Const.isInternetAvailable(this)) {
                        val intent = Intent(this, RegisterSupervisorActivity::class.java)
                        startActivity(intent)
                    } else {
                        Const.showSnackBar(this, getString(R.string.check_internet_connection))
                    }
                else
                    showToast(this, getString(R.string.we_need_this_permission))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (dbHelper != null) {
            fetchAndShowData()
        }
    }



}