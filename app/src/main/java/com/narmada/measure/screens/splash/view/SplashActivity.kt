package com.narmada.measure.screens.splash.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import com.narmada.measure.databinding.ActivitySplashBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.Constants.Companion.DEVICE_ID
import com.narmada.measure.screens.dashboard.view.DashboardActivity
import com.narmada.measure.screens.kapni_supervisor.dashboard.view.KhetarKapniDashboardActivity
import com.narmada.measure.screens.login.view.LoginActivity
import com.narmada.measure.utils.SharedPreferenceUtil

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DEVICE_ID = getDeviceIMEI(this)
        Handler(Looper.getMainLooper()).postDelayed({
            val i: Intent = if (SharedPreferenceUtil.getBoolean(Constants.IS_LOGIN, false)) {
                if(SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("3")) {
                    Intent(this, KhetarKapniDashboardActivity::class.java)
                } else {
                    Intent(this@SplashActivity, DashboardActivity::class.java)
                }
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            startActivity(i)
            finish()
        }, 2000)
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

}