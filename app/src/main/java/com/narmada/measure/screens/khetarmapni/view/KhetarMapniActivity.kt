package com.narmada.measure.screens.khetarmapni.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityKhetarMapniBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.room.AppDatabase
import com.narmada.measure.screens.dashboard.model.YearIntentModel
import com.narmada.measure.screens.khetarmapni.model.AccountMemberRequest
import com.narmada.measure.screens.khetarmapni.model.BiyaransItem
import com.narmada.measure.screens.khetarmapni.model.CheckComputerCodeRequest
import com.narmada.measure.screens.khetarmapni.model.ItemsItem
import com.narmada.measure.screens.khetarmapni.model.KhetarMapniRequest
import com.narmada.measure.screens.khetarmapni.model.MapniTypesItem
import com.narmada.measure.screens.khetarmapni.model.PiyatSadhansItem
import com.narmada.measure.screens.khetarmapni.model.SabhaSadItem
import com.narmada.measure.screens.khetarmapni.model.VillageItem
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerItem
import com.narmada.measure.screens.khetarmapni.viewmodel.KhetarMapniRepository
import com.narmada.measure.screens.khetarmapni.viewmodel.KhetarMapniViewModel
import com.narmada.measure.screens.khetarmapni.viewmodel.KhetarMapniViewModelFactory
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.DNDUtil
import com.narmada.measure.utils.GujaratiUtil
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.text.SimpleDateFormat
import java.util.Calendar


class KhetarMapniActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityKhetarMapniBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private val roomService by lazy { AppDatabase.getInstance(applicationContext) }
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    lateinit var viewModel: KhetarMapniViewModel
    val c = Calendar.getInstance()

    var zoneOfficerItems: ArrayList<String> = ArrayList()
    var yearItems: ArrayList<String> = ArrayList()
    var villageItems: ArrayList<String> = ArrayList()
    var mapniTypeItems: ArrayList<String> = ArrayList()
    var itemItems: ArrayList<String> = ArrayList()
    var piyarItems: ArrayList<String> = ArrayList()
    var biyaranItems: ArrayList<String> = ArrayList()
    var sabhaSadItems: ArrayList<String> = ArrayList()
    var organicItems: ArrayList<String> = ArrayList()
    var chasDirectionItems: ArrayList<String> = ArrayList()

    var villageList: ArrayList<VillageItem> = ArrayList()
    var zoneOfficerList: ArrayList<ZoneOfficerItem> = ArrayList()
    var mapniTypeList: ArrayList<MapniTypesItem> = ArrayList()
    var itemsList: ArrayList<ItemsItem> = ArrayList()
    var piyatList: ArrayList<PiyatSadhansItem> = ArrayList()
    var biyaransList: ArrayList<BiyaransItem> = ArrayList()
    var sabhaSadList: ArrayList<SabhaSadItem> = ArrayList()

    var zoneSpinnerDialog: SpinnerDialog? = null
    var pilanSeasonSpinnerDialog: SpinnerDialog? = null
    var mojeGaamSpinnerDialog: SpinnerDialog? = null
    var nondhGaamSpinnerDialog: SpinnerDialog? = null
    var mapniTypeSpinnerDialog: SpinnerDialog? = null
    var itemsSpinnerDialog: SpinnerDialog? = null
    var piyatSpinnerDialog: SpinnerDialog? = null
    var biyaranSpinnerDialog: SpinnerDialog? = null
    var sabhaSadSpinnerDialog: SpinnerDialog? = null
    var organicSpinnerDialog: SpinnerDialog? = null
    var chasDirectionSpinnerDialog: SpinnerDialog? = null

    var zoneOfficerPosition: Int? = null
    var pilanSeasonPosition: Int? = null
    var mojeGaamPosition: Int? = null
    var nondhGaamPosition: Int? = null
    var mapniTypePosition: Int? = null
    var itemTypePosition: Int? = null
    var piyatTypePosition: Int? = null
    var biyaranTypePosition: Int? = null
    var sabhaSadPosition: Int? = null
    var organicPosition: Int? = null
    var chasDirectionPosition: Int? = null

    var isKhetarCodeVerified: Boolean? = false
    var isSabhaSadCodeVerified: Boolean? = false
    var yearIntentModel: YearIntentModel? = null
    var TAG = "KhetarMapni----"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        organicItems = arrayListOf(getString(R.string.yes_guj), getString(R.string.no_guj))
        organicPosition = 1
        binding.etOrganic.setText(organicItems[organicPosition!!])

        chasDirectionItems =
            arrayListOf(getString(R.string.east_to_west), getString(R.string.north_to_south))
        chasDirectionPosition = 0
        binding.etChasDirection.setText(chasDirectionItems[chasDirectionPosition!!])

        toolbarBinding.txtTitle.setText(R.string.farm_measure_guj)
        yearIntentModel = intent.getSerializable("yearData", YearIntentModel::class.java)
        binding.etRopanSeason.setText(yearIntentModel!!.selectedYear)

        Log.d(TAG, "yearData: " + yearIntentModel?.years.toString())

        // If selected year (Ropan season) is last working year then show error...
        val yearIndex =
            yearIntentModel!!.years!!.indexOfFirst { yearsItem -> yearsItem!!.yearId == yearIntentModel!!.selectedYear }
        if (yearIndex == -1 || yearIndex == 0) {
            showPilanSeasonErrorDialog(message = getString(R.string.pilan_season_error))
            return
        }

        val pilanSeasonYear = yearIntentModel!!.years!![yearIndex - 1]!!
        if (pilanSeasonYear.isLocked == true) {
            showPilanSeasonErrorDialog(message = getString(R.string.pilan_season_locked_error))
            return
        }

        binding.etPilanSeason.setText(pilanSeasonYear.yearId)
        pilanSeasonPosition = yearIndex - 1

        viewClickListener()
        setupViewModel()
        viewTextChangeListener()

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
            binding.etZone.setText(SharedPreferenceUtil.getString(Constants.OFFICER_NAME, ""))
            viewModel.villageList(
                VillageListRequest(
                    SharedPreferenceUtil.getString(
                        Constants.ZONE_ID,
                        ""
                    )
                )
            )
        } else {
            viewModel.zoneOfficerList()
        }

        viewModel.commonApi(yearIntentModel!!.selectedYear.toString())

//        if (BuildConfig.DEBUG) {
//            binding.etKhetarCode.setText("3004")
//            binding.atSabhaCode.setText("35")
//            binding.etKhetarName.setText("ખેતર")
//            binding.etNorthKhetarName.setText("ખેતર")
//            binding.etSouthKhetarName.setText("ખેતર")
//            binding.etEastKhetarName.setText("ખેતર")
//            binding.etWestKhetarName.setText("ખેતર")
//            binding.etChasNumber.setText("5")
//            binding.etSerialNo.setText("123456")
//        }
    }

    private fun showPilanSeasonErrorDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_common)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val tvMessage: TextView = dialog.findViewById(R.id.tv_message)
        val tvClose: TextView = dialog.findViewById(R.id.tv_close)
        val relativeContinue: RelativeLayout = dialog.findViewById(R.id.btn_close)
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)

        tvMessage.text = message
        tvClose.text = getString(R.string.go_back)

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun showShareZeroErrorDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_common)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val tvMessage: TextView = dialog.findViewById(R.id.tv_message)
        val tvClose: TextView = dialog.findViewById(R.id.tv_close)
        val relativeContinue: RelativeLayout = dialog.findViewById(R.id.btn_close)
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)

        tvMessage.text = message
        tvClose.text = getString(R.string.go_back)

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun viewTextChangeListener() {
        binding.etKhetarCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.txtKhetarCode.visibility = View.GONE
                isKhetarCodeVerified = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.atSabhaCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.etSabhaName.setText("")
                binding.etSabhaGaam.setText("")
                binding.etGaamCode.setText("")
                binding.etShareNo.setText("")
                isSabhaSadCodeVerified = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun viewClickListener() {
        binding.etZone.setOnClickListener(this)
        binding.etPilanSeason.setOnClickListener(this)
        binding.etMojeGaam.setOnClickListener(this)
        binding.etNondhGaam.setOnClickListener(this)
        binding.etLaamDate.setOnClickListener(this)
        binding.etLaamRopan.setOnClickListener(this)
        binding.etItemName.setOnClickListener(this)
        binding.etPiyatSadhan.setOnClickListener(this)
        binding.etBiyaranName.setOnClickListener(this)
        binding.etOrganic.setOnClickListener(this)
        binding.etChasDirection.setOnClickListener(this)
        binding.btnKhetarCode.setOnClickListener(this)
        binding.btnSabhaCode.setOnClickListener(this)
        binding.imgSearch.setOnClickListener(this)
        toolbarBinding.ivBack.setOnClickListener(this)
//        binding.relativeManual.setOnClickListener(this)
        binding.relativeAuto.setOnClickListener(this)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, KhetarMapniViewModelFactory(
                KhetarMapniRepository(retrofitService!!, roomService),
                selectedYear = yearIntentModel!!.selectedYear,
            )
        )[KhetarMapniViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            showSnackBar(this, it)
        }

        viewModel.errorIntMessage.observe(this) {
            showSnackBar(this, getString(it))
        }

        viewModel.shareCountZeroMessage.observe(this) {
            showShareZeroErrorDialog(it)
        }

        viewModel.progressObservable.observe(this) {
            if (it == true) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

        viewModel.zoneOfficerListResponse.observe(this) {
            try {
                zoneOfficerList.clear()
                zoneOfficerList.addAll(it.data!! as List<ZoneOfficerItem>)
                for (item in zoneOfficerList) {
                    zoneOfficerItems.add(item.zoneOfficerName.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.searchSabhaSadResponse.observe(this) {
            try {
                if (it.data!!.isNotEmpty()) {
                    sabhaSadList.clear()
                    sabhaSadItems.clear()

                    sabhaSadList.addAll(it.data as List<SabhaSadItem>)
                    for (item in sabhaSadList) {
                        sabhaSadItems.add(item.farmerName.toString() + " - " + (item.farmerId.toString()))
                    }

                    sabhaSadSpinnerDialog = SpinnerDialog(
                        this,
                        sabhaSadItems, getString(R.string.sabha_name_guj), getString(R.string.close)
                    ) // With No Animation

                    sabhaSadSpinnerDialog!!.setCancellable(true)
                    sabhaSadSpinnerDialog!!.setShowKeyboard(false)
                    sabhaSadSpinnerDialog!!.bindOnSpinerListener { item, position ->
                        binding.atSabhaCode.setText(sabhaSadList[position].farmerId.toString())
                        sabhaSadPosition = position
                        sabhaSadSpinnerDialog!!.closeSpinerDialog()
                        viewModel.accountMemberDetail(
                            AccountMemberRequest(
                                binding.atSabhaCode.text.toString()
                            )
                        )

                    }
                    sabhaSadSpinnerDialog!!.showSpinerDialog()
                } else {
                    showSnackBar(this, it.message.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.villageListResponse.observe(this) {
            try {
                binding.etMojeGaam.setText("")
                binding.etNondhGaam.setText("")
                mojeGaamPosition = null
                nondhGaamPosition = null
                villageList.clear()
                villageList.addAll(it.data!! as List<VillageItem>)
                for (item in villageList) {
                    villageItems.add(item.villageName.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.commonApiResponse.observe(this) {
            try {
                mapniTypeList.addAll(it.data!!.mapniTypes as List<MapniTypesItem>)
                itemsList.addAll(it.data.items as List<ItemsItem>)
                piyatList.addAll(it.data.piyatSadhans as List<PiyatSadhansItem>)
                biyaransList.addAll(it.data.biyarans as List<BiyaransItem>)

                for (item in mapniTypeList) {
                    mapniTypeItems.add(item.name.toString())
                }
                for (item in itemsList) {
                    itemItems.add(item.itemName.toString())
                }
                for (item in piyatList) {
                    piyarItems.add(item.name.toString())
                }
                for (item in biyaransList) {
                    biyaranItems.add(item.biyaranName.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.accountMemberResponse.observe(this) {
            try {
                binding.etSabhaName.setText(it.accountData!!.accountName.toString())
                binding.etSabhaGaam.setText(it.accountData.villageName.toString())
                binding.etGaamCode.setText(it.accountData.villageId.toString())
                binding.etShareNo.setText(it.accountData.balanceShare.toString())
                isSabhaSadCodeVerified = true
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.checkComputerCodeResponse.observe(this) {
            try {
                if (it.isComputerCodeExist == 0) {
                    isKhetarCodeVerified = true
                    binding.txtKhetarCode.visibility = View.VISIBLE
                } else {
                    showSnackBar(this, it.message.toString())
                    binding.txtKhetarCode.visibility = View.GONE
                    isKhetarCodeVerified = false
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.imgSearch.id -> {
                if (binding.atSabhaCode.text.isNotEmpty()) {
                    viewModel.searchSabhaSad(binding.atSabhaCode.text.toString().trim())
                } else {
                    showSnackBar(this, getString(R.string.please_enter_sabhasad_name))
                }
            }

            binding.etZone.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                    zoneSpinnerDialog = SpinnerDialog(
                        this,
                        zoneOfficerItems,
                        getString(R.string.zone_officer_guj),
                        getString(R.string.close)
                    ) // With No Animation

                    zoneSpinnerDialog!!.setCancellable(true)
                    zoneSpinnerDialog!!.setShowKeyboard(false)
                    zoneSpinnerDialog!!.bindOnSpinerListener { item, position ->
                        binding.etZone.setText(item)
                        zoneOfficerPosition = position
                        zoneSpinnerDialog!!.closeSpinerDialog()
                        viewModel.villageList(VillageListRequest(zoneOfficerList[zoneOfficerPosition!!].zoneId.toString()))
                    }
                    zoneSpinnerDialog!!.showSpinerDialog()
                }
            }

            binding.etPilanSeason.id -> {
                // --> Disabled as per condition set in onCreate()....

//                for (item in yearIntentModel!!.years!!) {
//                    yearItems.add(item!!.yearId.toString())
//                }
//
//                pilanSeasonSpinnerDialog = SpinnerDialog(
//                    this, yearItems,
//                    getString(R.string.pilan_season_guj), getString(R.string.close)
//                ) // With No Animation
//
//                pilanSeasonSpinnerDialog!!.setCancellable(true)
//                pilanSeasonSpinnerDialog!!.setShowKeyboard(false)
//                pilanSeasonSpinnerDialog!!.bindOnSpinerListener { item, position ->
//                    binding.etPilanSeason.setText(item)
//                    pilanSeasonPosition = position
//                    pilanSeasonSpinnerDialog!!.closeSpinerDialog()
//                }
//                pilanSeasonSpinnerDialog!!.showSpinerDialog()
            }

            binding.etMojeGaam.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    showSnackBar(this, getString(R.string.please_select_zone_officer))
                } else {
                    mojeGaamSpinnerDialog = SpinnerDialog(
                        this, villageItems,
                        getString(R.string.mode_gaam_guj), getString(R.string.close)
                    ) // With No Animation

                    mojeGaamSpinnerDialog!!.setCancellable(true)
                    mojeGaamSpinnerDialog!!.setShowKeyboard(false)
                    mojeGaamSpinnerDialog!!.bindOnSpinerListener { item, position ->
                        binding.etMojeGaam.setText(item)
                        mojeGaamPosition = position
                        mojeGaamSpinnerDialog!!.closeSpinerDialog()
                    }
                    mojeGaamSpinnerDialog!!.showSpinerDialog()
                }
            }

            binding.etNondhGaam.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    showSnackBar(this, getString(R.string.please_select_zone_officer))
                } else {
                    nondhGaamSpinnerDialog = SpinnerDialog(
                        this, villageItems,
                        getString(R.string.nondh_gaam_guj), getString(R.string.close)
                    ) // With No Animation

                    nondhGaamSpinnerDialog!!.setCancellable(true)
                    nondhGaamSpinnerDialog!!.setShowKeyboard(false)
                    nondhGaamSpinnerDialog!!.bindOnSpinerListener { item, position ->
                        binding.etNondhGaam.setText(item)
                        nondhGaamPosition = position
                        nondhGaamSpinnerDialog!!.closeSpinerDialog()
                    }
                    nondhGaamSpinnerDialog!!.showSpinerDialog()
                }
            }

            binding.etLaamDate.id -> {
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    this,
                    { view, _year, _monthOfYear, _dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar[_year, _monthOfYear] = _dayOfMonth

                        val format = SimpleDateFormat("yyyy-MM-dd")
                        val strDate: String = format.format(calendar.time)

                        binding.etLaamDate.setText(strDate)
                    }, year, month, day
                )
                datePickerDialog.show()
            }

            binding.etLaamRopan.id -> {
                mapniTypeSpinnerDialog = SpinnerDialog(
                    this, mapniTypeItems,
                    getString(R.string.laam_ropan_guj), getString(R.string.close)
                ) // With No Animation

                mapniTypeSpinnerDialog!!.setCancellable(true)
                mapniTypeSpinnerDialog!!.setShowKeyboard(false)
                mapniTypeSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etLaamRopan.setText(item)
                    mapniTypePosition = position
                    mapniTypeSpinnerDialog!!.closeSpinerDialog()
                }
                mapniTypeSpinnerDialog!!.showSpinerDialog()
            }

            binding.etItemName.id -> {
                itemsSpinnerDialog = SpinnerDialog(
                    this, itemItems,
                    getString(R.string.item_name_guj), getString(R.string.close)
                ) // With No Animation

                itemsSpinnerDialog!!.setCancellable(true)
                itemsSpinnerDialog!!.setShowKeyboard(false)
                itemsSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etItemName.setText(item)
                    itemTypePosition = position
                    itemsSpinnerDialog!!.closeSpinerDialog()
                }
                itemsSpinnerDialog!!.showSpinerDialog()
            }

            binding.etPiyatSadhan.id -> {
                piyatSpinnerDialog = SpinnerDialog(
                    this, piyarItems,
                    getString(R.string.piyat_sadhan_guj), getString(R.string.close)
                ) // With No Animation

                piyatSpinnerDialog!!.setCancellable(true)
                piyatSpinnerDialog!!.setShowKeyboard(false)
                piyatSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etPiyatSadhan.setText(item)
                    piyatTypePosition = position
                    piyatSpinnerDialog!!.closeSpinerDialog()
                }
                piyatSpinnerDialog!!.showSpinerDialog()
            }

            binding.etBiyaranName.id -> {
                biyaranSpinnerDialog = SpinnerDialog(
                    this, biyaranItems,
                    getString(R.string.biyaran_name_guj), getString(R.string.close)
                ) // With No Animation

                biyaranSpinnerDialog!!.setCancellable(true)
                biyaranSpinnerDialog!!.setShowKeyboard(false)
                biyaranSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etBiyaranName.setText(item)
                    biyaranTypePosition = position
                    biyaranSpinnerDialog!!.closeSpinerDialog()
                }
                biyaranSpinnerDialog!!.showSpinerDialog()
            }

            binding.etOrganic.id -> {
                organicSpinnerDialog = SpinnerDialog(
                    this, organicItems,
                    getString(R.string.organic), getString(R.string.close)
                ) // With No Animation

                organicSpinnerDialog!!.setCancellable(true)
                organicSpinnerDialog!!.setShowKeyboard(false)
                organicSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    binding.etOrganic.setText(item)
                    organicPosition = position
                    organicSpinnerDialog!!.closeSpinerDialog()
                }
                organicSpinnerDialog!!.showSpinerDialog()
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

            binding.btnKhetarCode.id -> {
                if (binding.etPilanSeason.text.toString().isEmpty()) {
                    showSnackBar(this, getString(R.string.select_pilan_season))
                } else if (binding.etKhetarCode.text.toString().isEmpty()) {
                    showSnackBar(this, getString(R.string.enter_valid_khetar_code))
                } else {
                    viewModel.checkComputerCode(
                        CheckComputerCodeRequest(
                            binding.etPilanSeason.text.toString(),
                            binding.etKhetarCode.text.toString()
                        )
                    )
                }
            }

            binding.btnSabhaCode.id -> {
                if (binding.atSabhaCode.text.toString().isEmpty()) {
                    showSnackBar(this, getString(R.string.please_enter_sabhasad_code))
                } else {
                    viewModel.accountMemberDetail(
                        AccountMemberRequest(
                            binding.atSabhaCode.text.toString()
                        )
                    )
                }
            }

            binding.relativeAuto.id -> {

                val khetarName = binding.etKhetarName.text.toString()
                val northKhetarName = binding.etNorthKhetarName.text.toString()
                val southKhetarName = binding.etSouthKhetarName.text.toString()
                val eastKhetarName = binding.etEastKhetarName.text.toString()
                val westKhetarName = binding.etWestKhetarName.text.toString()
                val khetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(khetarName)
                val northKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(northKhetarName)
                val southKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(southKhetarName)
                val eastKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(eastKhetarName)
                val westKhetarNameShreeLipi = GujaratiUtil.gujToShreeGuj(westKhetarName)
                Log.d("TAG", "1: khetarName: $khetarName")
                Log.d("TAG", "1: khetarName: $khetarNameShreeLipi")

                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    showSnackBar(this, getString(R.string.select_zone_officer))
                } else if (pilanSeasonPosition == null) {
                    showSnackBar(this, getString(R.string.select_pilan_season))
                } else if (isKhetarCodeVerified == false) {
                    showSnackBar(this, getString(R.string.enter_valid_khetar_code))
                } else if (mojeGaamPosition == null) {
                    showSnackBar(this, getString(R.string.select_moje_gaam))
                } else if (nondhGaamPosition == null) {
                    showSnackBar(this, getString(R.string.select_nondh_gaam))
                } else if (isSabhaSadCodeVerified == false) {
                    showSnackBar(this, getString(R.string.enter_valid_sabha_code))
                } else if (binding.etLaamDate.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.select_laam_date))
                } else if (binding.etKhetarName.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_khetar_code))
                } else if (!khetarNameShreeLipi.first) {
                    showSnackBar(this, getString(R.string.enter_khetar_name_only_gujarati))
                } else if (itemTypePosition == null) {
                    showSnackBar(this, getString(R.string.select_item_name))
                } else if (piyatTypePosition == null) {
                    showSnackBar(this, getString(R.string.select_piyat_sadhan))
                } else if (binding.etSerialNo.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_serial_number))
                } else if (biyaranTypePosition == null) {
                    showSnackBar(this, getString(R.string.select_biyaran))
                } else if (organicPosition == null) {
                    showSnackBar(this, getString(R.string.select_organic_type))
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
                    val khetarMapniRequest = KhetarMapniRequest()
                    if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                        khetarMapniRequest.zoneOfficerId =
                            zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
                        khetarMapniRequest.zoneOfficerName =
                            zoneOfficerList[zoneOfficerPosition!!].zoneOfficerName.toString()
                    } else {
                        khetarMapniRequest.zoneOfficerId =
                            SharedPreferenceUtil.getString(Constants.ZONE_ID, "")
                        khetarMapniRequest.zoneOfficerName =
                            SharedPreferenceUtil.getString(Constants.OFFICER_NAME, "")
                    }
                    khetarMapniRequest.accountId = binding.atSabhaCode.text.toString()
                    khetarMapniRequest.computerCode = binding.etKhetarCode.text.toString()
                    khetarMapniRequest.mojeVillageId =
                        villageList[mojeGaamPosition!!].villageId.toString()
                    khetarMapniRequest.mojeVillageName =
                        villageList[mojeGaamPosition!!].villageName.toString()
                    khetarMapniRequest.nondhVillageId =
                        villageList[nondhGaamPosition!!].villageId.toString()
                    khetarMapniRequest.nondhVillageName =
                        villageList[nondhGaamPosition!!].villageName.toString()

                    khetarMapniRequest.sabhaCode = binding.atSabhaCode.text.toString()
                    khetarMapniRequest.sabhaGaam = binding.etSabhaGaam.text.toString()
                    khetarMapniRequest.sabhaName = binding.etSabhaName.text.toString()
                    khetarMapniRequest.gaamCode = binding.etGaamCode.text.toString()
                    khetarMapniRequest.sherTotal = binding.etShareNo.text.toString()
                    khetarMapniRequest.approxRopanDate = binding.etLaamDate.text.toString()
                    khetarMapniRequest.mapniType = mapniTypeList[mapniTypePosition!!].id.toString()
                    khetarMapniRequest.mapniName =
                        mapniTypeList[mapniTypePosition!!].name.toString()
                    khetarMapniRequest.itemId = itemsList[itemTypePosition!!].itemID.toString()
                    khetarMapniRequest.itemName = itemsList[itemTypePosition!!].itemName.toString()
                    khetarMapniRequest.piyatSadhan = piyatList[piyatTypePosition!!].id.toString()
                    khetarMapniRequest.piyatSadhanName =
                        piyatList[piyatTypePosition!!].name.toString()
                    khetarMapniRequest.biyaranId =
                        biyaransList[biyaranTypePosition!!].biyaranId.toString()
                    khetarMapniRequest.biyaranName =
                        biyaransList[biyaranTypePosition!!].biyaranName.toString()
                    khetarMapniRequest.notOrganic = if (organicPosition!! == 0) "0" else "1"
                    khetarMapniRequest.notOrganicName = organicItems[organicPosition!!]
                    khetarMapniRequest.serialNumber = binding.etSerialNo.text.toString()
                    khetarMapniRequest.workingYear = binding.etRopanSeason.text.toString()
                    khetarMapniRequest.pilanSeason = binding.etPilanSeason.text.toString()
                    khetarMapniRequest.khetarName = khetarNameShreeLipi.second
//                    07-05-2025 new fields are added in khetar mapni
                    khetarMapniRequest.northKhetarName = northKhetarNameShreeLipi.second
                    khetarMapniRequest.southKhetarName = southKhetarNameShreeLipi.second
                    khetarMapniRequest.eastKhetarName = eastKhetarNameShreeLipi.second
                    khetarMapniRequest.westKhetarName = westKhetarNameShreeLipi.second
                    khetarMapniRequest.chasNumber = binding.etChasNumber.text.toString()
                    khetarMapniRequest.chasDirection =
                        if (chasDirectionPosition!! == 0) "1" else "2"
                    khetarMapniRequest.chasDirectionName =
                        chasDirectionItems[chasDirectionPosition!!]

                    // Check for DND Mode...
                    if (!DNDUtil.isPermissionAllowed(this)) {
                        DNDUtil.showDNDPermissionDialog(
                            this,
                            settingClicked = {
                                DNDUtil.openDNDSettingScreen(this)
                            },
                            continueClicked = {
                                gotoNextScreen(khetarMapniRequest)
                            })
                        return
                    }

                    DNDUtil.showAskDNDDialog(
                        this,
                        enableClicked = {
                            DNDUtil.enableDNDMode(this)
                            gotoNextScreen(khetarMapniRequest)
                        },
                        disableClicked = {
                            DNDUtil.disableDNDMode(this)
                            gotoNextScreen(khetarMapniRequest)
                        })
                }
            }

            /*binding.relativeManual.id -> {
                if (SharedPreferenceUtil.getString(ApiParams.USER_TYPE, "")
                        .equals("2") && zoneOfficerPosition == null
                ) {
                    showSnackBar(this, getString(R.string.select_zone_officer))
                } else if (pilanSeasonPosition == null) {
                    showSnackBar(this, getString(R.string.select_pilan_season))
                } else if (isKhetarCodeVerified == false) {
                    showSnackBar(this, getString(R.string.enter_valid_khetar_code))
                } else if (mojeGaamPosition == null) {
                    showSnackBar(this, getString(R.string.select_moje_gaam))
                } else if (nondhGaamPosition == null) {
                    showSnackBar(this, getString(R.string.select_nondh_gaam))
                } else if (isSabhaSadCodeVerified == false) {
                    showSnackBar(this, getString(R.string.enter_valid_sabha_code))
                } else if (binding.etLaamDate.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.select_laam_date))
                } else if (binding.etKhetarName.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_khetar_code))
                } else if (itemTypePosition == null) {
                    showSnackBar(this, getString(R.string.select_item_name))
                } else if (piyatTypePosition == null) {
                    showSnackBar(this, getString(R.string.select_piyat_sadhan))
                } else if (binding.etSerialNo.text.isNullOrEmpty()) {
                    showSnackBar(this, getString(R.string.enter_serial_number))
                } else if (biyaranTypePosition == null) {
                    showSnackBar(this, getString(R.string.select_biyaran))
                } else {
                    val khetarMapniRequest = KhetarMapniRequest()
                    if (SharedPreferenceUtil.getString(ApiParams.USER_TYPE, "").equals("2")) {
                        khetarMapniRequest.zoneOfficerId =
                            zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
                        khetarMapniRequest.zoneOfficerName =
                            zoneOfficerList[zoneOfficerPosition!!].zoneOfficerName.toString()
                    } else {
                        khetarMapniRequest.zoneOfficerId =
                            SharedPreferenceUtil.getString(ApiParams.ZONE_SUPERVISOR_ID, "")
                        khetarMapniRequest.zoneOfficerName =
                            SharedPreferenceUtil.getString(ApiParams.OFFICER_NAME, "")
                    }
                    khetarMapniRequest.accountId = binding.etSabhaCode.text.toString()
                    khetarMapniRequest.computerCode = binding.etKhetarCode.text.toString()
                    khetarMapniRequest.mojeVillageId =
                        villageList[mojeGaamPosition!!].villageId.toString()
                    khetarMapniRequest.mojeVillageName =
                        villageList[mojeGaamPosition!!].villageName.toString()
                    khetarMapniRequest.nondhVillageId =
                        villageList[nondhGaamPosition!!].villageId.toString()
                    khetarMapniRequest.nondhVillageName =
                        villageList[nondhGaamPosition!!].villageName.toString()

                    khetarMapniRequest.sabhaCode = binding.etSabhaCode.text.toString()
                    khetarMapniRequest.sabhaGaam = binding.etSabhaGaam.text.toString()
                    khetarMapniRequest.sabhaName = binding.etSabhaName.text.toString()
                    khetarMapniRequest.gaamCode = binding.etGaamCode.text.toString()
                    khetarMapniRequest.sherTotal = binding.etShareNo.text.toString()

                    khetarMapniRequest.approxRopanDate = binding.etLaamDate.text.toString()
                    khetarMapniRequest.mapniType = mapniTypeList[mapniTypePosition!!].id.toString()
                    khetarMapniRequest.mapniName =
                        mapniTypeList[mapniTypePosition!!].name.toString()
                    khetarMapniRequest.itemId = itemsList[itemTypePosition!!].itemID.toString()
                    khetarMapniRequest.itemName = itemsList[itemTypePosition!!].itemName.toString()
                    khetarMapniRequest.piyatSadhan = piyatList[piyatTypePosition!!].id.toString()
                    khetarMapniRequest.piyatSadhanName =
                        piyatList[piyatTypePosition!!].name.toString()
                    khetarMapniRequest.biyaranId =
                        biyaransList[biyaranTypePosition!!].biyaranId.toString()
                    khetarMapniRequest.biyaranName =
                        biyaransList[biyaranTypePosition!!].biyaranName.toString()
                    khetarMapniRequest.serialNumber = binding.etSerialNo.text.toString()
                    khetarMapniRequest.workingYear = binding.etRopanSeason.text.toString()
                    khetarMapniRequest.pilanSeason = binding.etPilanSeason.text.toString()
                    khetarMapniRequest.khetarName = binding.etKhetarName.text.toString()

                    val intent = Intent(this, MapKhetarMapniManualActivity::class.java)
                    intent.putExtra("data", khetarMapniRequest)
                    startActivity(intent)

                }
            }*/

        }
    }

    private fun gotoNextScreen(khetarMapniRequest: KhetarMapniRequest) {
        val intent = Intent(this, MapKhetarMapniAutoActivity::class.java)
        intent.putExtra("data", khetarMapniRequest)
        startActivity(intent)
    }

}