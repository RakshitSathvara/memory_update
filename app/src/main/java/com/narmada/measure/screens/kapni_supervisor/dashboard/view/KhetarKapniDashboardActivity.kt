package com.narmada.measure.screens.kapni_supervisor.dashboard.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityKhetarKapniDashboardBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.dashboard.viewmodel.DashboardRepository
import com.narmada.measure.screens.dashboard.viewmodel.DashboardViewModel
import com.narmada.measure.screens.dashboard.viewmodel.DashboardViewModelFactory
import com.narmada.measure.screens.kapni_supervisor.chalan_history.view.SerdiDeliveryHistoryActivity
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.view.SerdiDeliveryChalanActivity
import com.narmada.measure.screens.login.view.LoginActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showToast
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil

class KhetarKapniDashboardActivity : AppCompatActivity(), View.OnClickListener {

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val binding by lazy { ActivityKhetarKapniDashboardBinding.inflate(layoutInflater) }

    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: DashboardViewModel

    private var backPressedTime: Long = 0
    private var workingYear = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        workingYear = SharedPreferenceUtil.getString(Constants.WORKING_YEAR, "")!!

        binding.txtName.text = SharedPreferenceUtil.getString(Constants.OFFICER_NAME, "")
        binding.spYear.text = workingYear

        binding.linearLogout.setOnClickListener(this)
        binding.linearSerdiDelivery.setOnClickListener(this)
        binding.linearSerdiDeliveryHistory.setOnClickListener(this)

        setupViewModel()
    }



    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, DashboardViewModelFactory(DashboardRepository(retrofitService!!)))[DashboardViewModel::class.java]

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

            binding.linearSerdiDelivery.id -> {
                if (Const.isInternetAvailable(this)) {
                    val intent = Intent(this, SerdiDeliveryChalanActivity::class.java)
                    intent.putExtra("working_year", workingYear)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.check_internet_connection))
                }
            }

            binding.linearSerdiDeliveryHistory.id -> {
                if (Const.isInternetAvailable(this)) {
                    val intent = Intent(this, SerdiDeliveryHistoryActivity::class.java)
                    intent.putExtra("working_year", workingYear)
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
        val relativeContinue: RelativeLayout = dialog.findViewById(R.id.relative_continue)
        val textView: TextView = dialog.findViewById(R.id.txt_view)
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)

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

    /*private fun showUpdateDialog() {
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
    }*/

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            onBackPressedDispatcher.onBackPressed()
            finish()
        } else {
            showToast(this, getString(R.string.press_again_to_exit))
        }
        backPressedTime = System.currentTimeMillis()
    }

}