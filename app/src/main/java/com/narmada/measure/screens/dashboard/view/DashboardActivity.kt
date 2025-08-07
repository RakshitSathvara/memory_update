package com.narmada.measure.screens.dashboard.view

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.database.DBHelper
import com.narmada.measure.databinding.ActivityDashboardBinding
import com.narmada.measure.face_recognization.Supervisor
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.admin_user.admin_login.AdminLoginActivity
import com.narmada.measure.screens.attendance.view.AttendanceListActivity
import com.narmada.measure.screens.badeli_serdi_report.view.BadeliSerdiReportActivity
import com.narmada.measure.screens.bareliserdi.view.BareliSerdiActivity
import com.narmada.measure.screens.dashboard.model.YearIntentModel
import com.narmada.measure.screens.dashboard.model.YearsItem
import com.narmada.measure.screens.dashboard.viewmodel.DashboardRepository
import com.narmada.measure.screens.dashboard.viewmodel.DashboardViewModel
import com.narmada.measure.screens.dashboard.viewmodel.DashboardViewModelFactory
import com.narmada.measure.screens.kapni_complete_report.view.KapniCompleteReportActivity
import com.narmada.measure.screens.kapni_complete_report_view.view.KapniPuriThayaniMahitiActivity
import com.narmada.measure.screens.khetar_mapni_report.view.KhetarMapniReportActivity
import com.narmada.measure.screens.khetarmapni.view.KhetarMapniActivity
import com.narmada.measure.screens.khetarmapni_offline.view.OfflineMapniListActivity
import com.narmada.measure.screens.login.view.LoginActivity
import com.narmada.measure.screens.pani_bandh_register.view.PaniBandhRegisterActivity
import com.narmada.measure.screens.pani_bandh_yaadi.view.PaniBandhYaadiActivity
import com.narmada.measure.screens.ropan_register_report.view.RopanRegisterReportActivity
import com.narmada.measure.screens.sabhasad_mahiti.view.SabhasadMahitiActivity
import com.narmada.measure.screens.sabhasad_mahiti_update.view.SabhasadMahitiUpdateActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showToast
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil

class DashboardActivity : AppCompatActivity(), View.OnClickListener {
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val binding by lazy { ActivityDashboardBinding.inflate(layoutInflater) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: DashboardViewModel
    var yearList: List<YearsItem> = arrayListOf()
    var backPressedTime: Long = 0
    var selectedYear = ""

    var dbHelper: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
            binding.linearZone.visibility = View.GONE
            binding.linearAttendance.visibility = View.GONE
            binding.linearChaheroOdakh.visibility = View.GONE
            binding.linearKapniPuriAheval.visibility = View.GONE
            binding.linearKapniPuriMahiti.visibility = View.GONE
        }

        binding.txtZone.text = SharedPreferenceUtil.getString(Constants.ZONE_NAME, "")
        binding.txtName.text = SharedPreferenceUtil.getString(Constants.OFFICER_NAME, "")
        binding.linearLogout.setOnClickListener(this)
        binding.linearMeasure.setOnClickListener(this)
        binding.linearForm.setOnClickListener(this)
        binding.linearOfflineMapni.setOnClickListener(this)
        binding.linearAttendance.setOnClickListener(this)
        binding.linearPaniBandhRegister.setOnClickListener(this)
        binding.linearBareliSerdiReport.setOnClickListener(this)
        binding.linearKhetarMapniGaamList.setOnClickListener(this)
        binding.linearRopanRegister.setOnClickListener(this)
        binding.linearSabhasadMahiti.setOnClickListener(this)
        binding.linearSabhasadMahitiUpdate.setOnClickListener(this)
        binding.linearKapniPuriAheval.setOnClickListener(this)
        binding.linearPaniBandhYadi.setOnClickListener(this)
        binding.linearKapniPuriMahiti.setOnClickListener(this)
        binding.linearChaheroOdakh.setOnClickListener(this)

        setupViewModel()

        if (Const.isInternetAvailable(this))
            viewModel.yearList(this, BuildConfig.VERSION_CODE.toString())

    }


    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(
                DashboardRepository(retrofitService!!)
            )
        )[DashboardViewModel::class.java]

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

        viewModel.logoutResponse.observe(this) {
            try {
                showToast(this, it.message.toString())
                SharedPreferenceUtil.clear()
                SharedPreferenceUtil.save()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.yearListResponse.observe(this) {
            try {
                yearList = it.data!!.years as List<YearsItem>
                val adapter = ArrayAdapter(
                    this,
                    R.layout.spinner_item, yearList
                )
                binding.spYear.adapter = adapter
                binding.spYear.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedYear = yearList[position].yearId.toString()
                        }

                    }

                if (it.data.isForceUpgrade == true)
                    showUpdateDialog()
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            binding.linearLogout.id -> {
                if (Const.isInternetAvailable(this)) {
                    showLogoutDialog()
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearMeasure.id -> {
                if (Const.isInternetAvailable(this)) {
                    val yearData = YearIntentModel(yearList, selectedYear)
                    val intent = Intent(this, KhetarMapniActivity::class.java)
                    intent.putExtra("yearData", yearData)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearForm.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, BareliSerdiActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearOfflineMapni.id -> {
                val yearData = YearIntentModel(yearList, selectedYear)
                val intent = Intent(this, OfflineMapniListActivity::class.java)
                intent.putExtra("yearData", yearData)
                startActivity(intent)
            }

            binding.linearAttendance.id -> {
                if (Const.isInternetAvailable(this)) {
                    val intent = Intent(this, AttendanceListActivity::class.java)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearPaniBandhRegister.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, PaniBandhRegisterActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearBareliSerdiReport.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, BadeliSerdiReportActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearKhetarMapniGaamList.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, KhetarMapniReportActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearRopanRegister.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, RopanRegisterReportActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearSabhasadMahiti.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, SabhasadMahitiActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearSabhasadMahitiUpdate.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, SabhasadMahitiUpdateActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearKapniPuriAheval.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, KapniCompleteReportActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearPaniBandhYadi.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, PaniBandhYaadiActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearKapniPuriMahiti.id -> {
                if (Const.isInternetAvailable(this)) {
                    val workingYear = binding.spYear.selectedItem.toString()
                    val intent = Intent(this, KapniPuriThayaniMahitiActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearChaheroOdakh.id -> {
                if (Const.isInternetAvailable(this)) {
                    val intent = Intent(this, AdminLoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

        }
    }



    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout
        val textView = dialog.findViewById(R.id.txt_view) as TextView
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            viewModel.logout(this)
        }
        textView.setOnClickListener {
            dialog.dismiss()
        }
        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showUpdateDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_update)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout

        relativeContinue.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            onBackPressedDispatcher.onBackPressed()
            finish()
        } else {
            showToast(this, getString(R.string.press_again_to_exit))
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
            val supervisorId = SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")
            val supervisorList: List<Supervisor> = dbHelper!!.getAllSupervisor(supervisorId)
            if (supervisorList.isNotEmpty()) {
                Glide.with(this).load(supervisorList[0].image).into(binding.imgSupervisorFace)
            } else {
                binding.imgSupervisorFace.setImageResource(R.drawable.ic_scan)
            }
        }
    }

}