package com.narmada.measure.screens.admin_user.admin_login

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
import com.narmada.measure.databinding.ActivityAdminLoginBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.admin_user.admin_dashboard.view.AdminDashboardActivity
import com.narmada.measure.screens.login.model.LoginRequest
import com.narmada.measure.screens.login.viewmodel.LoginRepository
import com.narmada.measure.screens.login.viewmodel.LoginViewModel
import com.narmada.measure.screens.login.viewmodel.LoginViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.LoadingDialog
import org.json.JSONObject

class AdminLoginActivity : AppCompatActivity(), View.OnClickListener {
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val binding by lazy { ActivityAdminLoginBinding.inflate(layoutInflater) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.floatSubmit.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)

        Constants.DEVICE_ID = getDeviceIMEI(this)
        setupViewModel()

        if (BuildConfig.DEBUG) {
            binding.etUserid.setText("AO1.Admin")
            binding.etPassword.setText("password")
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

                if (it.data!!.userType.toString() == "4") {
                    Constants.ADMIN_NAME = it.data.officerName.toString()
                    Constants.ADMIN_TOKEN = it.data.token.toString()
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                } else {
                    showSnackBar(this, getString(R.string.only_admin_user_can_login))
                }
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
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

            binding.ivBack.id -> {
                finish()
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