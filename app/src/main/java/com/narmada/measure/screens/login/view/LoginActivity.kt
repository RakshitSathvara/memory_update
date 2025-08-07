package com.narmada.measure.screens.login.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.database.DBHelper
import com.narmada.measure.databinding.ActivityLoginBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.dashboard.model.YearIntentModel
import com.narmada.measure.screens.dashboard.view.DashboardActivity
import com.narmada.measure.screens.kapni_supervisor.dashboard.view.KhetarKapniDashboardActivity
import com.narmada.measure.screens.khetarmapni_offline.view.OfflineMapniListActivity
import com.narmada.measure.screens.login.model.LoginRequest
import com.narmada.measure.screens.login.viewmodel.LoginRepository
import com.narmada.measure.screens.login.viewmodel.LoginViewModel
import com.narmada.measure.screens.login.viewmodel.LoginViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.floatSubmit.setOnClickListener(this)
        binding.linearOfflineMapni.setOnClickListener(this)
        Constants.DEVICE_ID = getDeviceIMEI(this)
        setupViewModel()

        if(BuildConfig.FLAVOR == "kapniSupervisor") {
            binding.linearOfflineMapni.visibility = View.GONE
        }

        if (BuildConfig.DEBUG) {
            if (BuildConfig.FLAVOR == "kapniSupervisor") {
                // khetar kapni officer login
                binding.etUserid.setText("20210001")
                binding.etPassword.setText("123456")
            } else {
                // supervisor login
                binding.etUserid.setText("SP1.Niraj")
                binding.etPassword.setText("123456")

                // agriculture officer login
//                binding.etUserid.setText("AG1.Harendra")
//                binding.etPassword.setText("123456")
            }
        }
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                LoginViewModelFactory(LoginRepository(retrofitService!!))
            )[LoginViewModel::class.java]

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

        viewModel.loginResponse.observe(this) {
            try {
                Const.showToast(this, it.message.toString())
                SharedPreferenceUtil.putValue(Constants.IS_LOGIN, true)
                SharedPreferenceUtil.putValue(Constants.TOKEN, it.data!!.token.toString())
                SharedPreferenceUtil.putValue(Constants.USER_TYPE, it.data.userType.toString())
                SharedPreferenceUtil.putValue(Constants.ZONE_ID, it.data.zoneId.toString())
                SharedPreferenceUtil.putValue(Constants.ZONE_SUPERVISOR_ID, it.data.zoneSupervisorId.toString())
                SharedPreferenceUtil.putValue(Constants.ZONE_NAME, it.data.zoneName.toString())
                SharedPreferenceUtil.putValue(Constants.OFFICER_NAME, it.data.officerName.toString())
                SharedPreferenceUtil.putValue(Constants.WORKING_YEAR, it.data.workingYear.toString())
                SharedPreferenceUtil.save()

                if(it.data.userType.toString() == "3") {
                    startActivity(Intent(this, KhetarKapniDashboardActivity::class.java))
                    finish()
                } else {
                    if(it.data.userType.toString() == "1") {
                        // get face data and then redirect to dashboard...
                        viewModel.getSupervisorFaceData(it.data.zoneSupervisorId.toString())
                    } else {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.supervisorFaceResponse.observe(this) {
            try {
                // save data to database...
                val dbHelper = DBHelper(this)
                dbHelper.deleteSupervisorTable()

                if(it.data?.faceData?.isNotEmpty() == true) {
                    dbHelper.insertFaceFromApi(
                        it.data.fullName,
                        SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")!!,
                        it.data.faceData,
                        it.data.faceImagePath
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }

            // Redirect to dashboard...
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.floatSubmit.id -> {
                Const.closeKeyboard(this)
                if (binding.etUserid.text!!.isEmpty()) {
                    showSnackBar(this, getString(R.string.please_enter_userid))
                } else if (binding.etPassword.text!!.isEmpty()) {
                    showSnackBar(this, getString(R.string.please_enter_password))
                } else if (binding.etPassword.text!!.length < 5) {
                    showSnackBar(this, getString(R.string.password_must_be_above_5_character))
                } else {
                    val requestJSON = JSONObject()
                    requestJSON.put("username", binding.etUserid.text!!.trim().toString())
                    requestJSON.put("password", binding.etPassword.text!!.trim().toString())

                    val loginRequest = LoginRequest(
                        binding.etPassword.text!!.trim().toString(),
                        binding.etUserid.text!!.trim().toString()
                    )
                    viewModel.login(loginRequest)

                }

            }

            binding.linearOfflineMapni.id -> {
                val yearData = YearIntentModel()
                val intent = Intent(this, OfflineMapniListActivity::class.java)
                intent.putExtra("yearData", yearData)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceIMEI(context: Context): String {
        var deviceUniqueIdentifier: String?
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceUniqueIdentifier =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            @Suppress("DEPRECATION")
            deviceUniqueIdentifier = tm.deviceId

            if (deviceUniqueIdentifier.isNullOrBlank()) {
                deviceUniqueIdentifier = tm.simSerialNumber
            }
        }
        return deviceUniqueIdentifier.toString()
    }

    override fun onStop() {
        super.onStop()
        if (dialog.isShowing)
            dialog.dismiss() // Dismiss any active dialog
    }

}