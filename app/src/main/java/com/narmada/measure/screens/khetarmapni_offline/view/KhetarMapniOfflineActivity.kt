package com.narmada.measure.screens.khetarmapni_offline.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityKhetarMapniOfflineBinding
import com.narmada.measure.screens.khetarmapni_offline.model.OfflineMapniModel
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.DNDUtil
import com.narmada.measure.utils.GujaratiUtil
import com.narmada.measure.utils.LoadingDialog
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog


class KhetarMapniOfflineActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityKhetarMapniOfflineBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    var TAG = "KhetarMapniOffline----"
    var chasDirectionItems: ArrayList<String> = ArrayList()
    var chasDirectionSpinnerDialog: SpinnerDialog? = null
    var chasDirectionPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.offline_khetar_mapni)

        chasDirectionItems =
            arrayListOf(getString(R.string.east_to_west), getString(R.string.north_to_south))
        chasDirectionPosition = 0
        binding.etChasDirection.setText(chasDirectionItems[chasDirectionPosition!!])

        viewClickListener()
    }

    private fun viewClickListener() {
        toolbarBinding.ivBack.setOnClickListener(this)
        binding.relativeStart.setOnClickListener(this)
        binding.etChasDirection.setOnClickListener(this)

        binding.etSabhaCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length == 1 && s.toString().startsWith("0")) {
                    s.clear()
                }
            }
        })

        binding.etKhetarCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length == 1 && s.toString().startsWith("0")) {
                    s.clear()
                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.etChasDirection.id -> {
                chasDirectionSpinnerDialog = SpinnerDialog(
                    this, chasDirectionItems,
                    getString(R.string.chas_direction), getString(R.string.close)
                ) // With No Animation

                chasDirectionSpinnerDialog!!.setCancellable(true)
                chasDirectionSpinnerDialog!!.setShowKeyboard(false)
                chasDirectionSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etChasDirection.setText(item)
                    chasDirectionPosition = position
                    chasDirectionSpinnerDialog!!.closeSpinerDialog()
                }
                chasDirectionSpinnerDialog!!.showSpinerDialog()
            }

            binding.relativeStart.id -> {
                val northKhetarName = binding.etNorthKhetarName.text.toString()
                val southKhetarName = binding.etSouthKhetarName.text.toString()
                val eastKhetarName = binding.etEastKhetarName.text.toString()
                val westKhetarName = binding.etWestKhetarName.text.toString()

                val northKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(northKhetarName)
                val southKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(southKhetarName)
                val eastKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(eastKhetarName)
                val westKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(westKhetarName)

                if (binding.etKhetarCode.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_valid_khetar_code))
                } else if (binding.etSabhaCode.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_valid_sabha_code))
                } else if (binding.etNorthKhetarName.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_north_direction_khetar_name))
                } else if (!northKhetarNameShreeLipi.first) {
                    showSnackBar(this, getString(R.string.validate_north_direction_khetar_name))
                } else if (binding.etSouthKhetarName.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_south_direction_khetar_name))
                } else if (!southKhetarNameShreeLipi.first) {
                    showSnackBar(this, getString(R.string.validate_south_direction_khetar_name))
                } else if (binding.etEastKhetarName.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_east_direction_khetar_name))
                } else if (!eastKhetarNameShreeLipi.first) {
                    showSnackBar(this, getString(R.string.validate_east_direction_khetar_name))
                } else if (binding.etWestKhetarName.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_west_direction_khetar_name))
                } else if (!westKhetarNameShreeLipi.first) {
                    showSnackBar(this, getString(R.string.validate_west_direction_khetar_name))
                } else if (binding.etChasNumber.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_chas_number))
                } else if (chasDirectionPosition == null) {
                    showSnackBar(this, getString(R.string.select_chas_direction))
                } else {
                    val offlineMapniModel = OfflineMapniModel()
                    offlineMapniModel.khetarCode = binding.etKhetarCode.text.toString()
                    offlineMapniModel.sabhasadCode = binding.etSabhaCode.text.toString()
                    //                    08-05-2025 new fields are added in khetar mapni
                    offlineMapniModel.northKhetarName = northKhetarName
                    offlineMapniModel.southKhetarName = southKhetarName
                    offlineMapniModel.eastKhetarName = eastKhetarName
                    offlineMapniModel.westKhetarName = westKhetarName
                    offlineMapniModel.chasNumber = binding.etChasNumber.text.toString()
                    offlineMapniModel.chasDirection = if (chasDirectionPosition!! == 0) "1" else "2"
                    offlineMapniModel.chasDirectionName =
                        chasDirectionItems[chasDirectionPosition!!]

                    if (!DNDUtil.isPermissionAllowed(this)) {
                        DNDUtil.showDNDPermissionDialog(
                            this,
                            settingClicked = {
                                DNDUtil.openDNDSettingScreen(this)
                            },
                            continueClicked = {
                                gotoNextScreen(offlineMapniModel)
                            })
                        return
                    }

                    DNDUtil.showAskDNDDialog(
                        this,
                        enableClicked = {
                            DNDUtil.enableDNDMode(this)
                            gotoNextScreen(offlineMapniModel)
                        },
                        disableClicked = {
                            DNDUtil.disableDNDMode(this)
                            gotoNextScreen(offlineMapniModel)
                        })
                }
            }

        }
    }

    private fun gotoNextScreen(offlineMapniModel: OfflineMapniModel) {
        val intent = Intent(this, MapKhetarMapniOfflineActivity::class.java)
        intent.putExtra("data", offlineMapniModel)
        startActivity(intent)
    }

}