package com.narmada.measure.screens.khetar_mapni_report.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityKhetarMapniReportBinding
import com.narmada.measure.multi_select_spinner.MultiSelectSpinnerDialog
import com.narmada.measure.network.Constants
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.khetar_mapni_report.model.KhetarMapniReport
import com.narmada.measure.screens.khetar_mapni_report.model.KhetarMapniReportRequest
import com.narmada.measure.screens.khetar_mapni_report.view.adapter.KhetarMapniReportAdapter
import com.narmada.measure.screens.khetar_mapni_report.viewmodel.KhetarMapniReportRepository
import com.narmada.measure.screens.khetar_mapni_report.viewmodel.KhetarMapniReportViewModel
import com.narmada.measure.screens.khetar_mapni_report.viewmodel.KhetarMapniReportViewModelFactory
import com.narmada.measure.screens.khetarmapni.model.VillageItem
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerItem
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class KhetarMapniReportActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityKhetarMapniReportBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: KhetarMapniReportViewModel

    private lateinit var workingYear: String

    var zoneOfficerList: ArrayList<ZoneOfficerItem> = ArrayList()
    var zoneList: ArrayList<SupervisorZoneItem> = ArrayList()
    var villageList: ArrayList<VillageItem> = ArrayList()

    var zoneOfficerItems: ArrayList<String> = ArrayList()
    var zoneItems: ArrayList<String> = ArrayList()
    var villageItems: ArrayList<String> = ArrayList()

    var zoneOfficerSpinnerDialog: SpinnerDialog? = null
    var zoneSpinnerDialog: SpinnerDialog? = null
    var villageSpinnerDialog: MultiSelectSpinnerDialog<VillageItem>? = null

    var zoneOfficerPosition: Int? = null
    var zonePosition: Int? = null
    var selectedVillageIds = arrayListOf<VillageItem>()

    private var reportList: MutableList<KhetarMapniReport> = arrayListOf()
    private var reportAdapter: KhetarMapniReportAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.khetar_mapni_gaam_list)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.etFromDate.setOnClickListener(this)
        binding.etToDate.setOnClickListener(this)
        binding.etZoneOfficer.setOnClickListener(this)
        binding.etZone.setOnClickListener(this)
        binding.etVillage.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerReport.layoutManager = layoutManager
        reportAdapter = KhetarMapniReportAdapter(this, reportList)
        binding.recyclerReport.adapter = reportAdapter

        setupViewModel()

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
            binding.etZoneOfficer.setText(SharedPreferenceUtil.getString(Constants.OFFICER_NAME, ""))
            viewModel.supervisorZoneList(SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")!!)
        } else {
            viewModel.zoneOfficerList()
        }
    }

    private fun resetSupervisorSelection() {
        binding.etZoneOfficer.setText("")
        zoneOfficerList.clear()
        zoneOfficerItems.clear()
        zoneOfficerPosition = null
    }

    private fun resetZonesSelection() {
        binding.etZone.setText("")
        zoneList.clear()
        zoneItems.clear()
        zonePosition = null
    }

    private fun resetVillageSelection() {
        binding.etVillage.setText("")
        selectedVillageIds.clear()
        villageList.clear()
        villageItems.clear()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, KhetarMapniReportViewModelFactory(KhetarMapniReportRepository(retrofitService!!)))[KhetarMapniReportViewModel::class.java]

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

        viewModel.zoneOfficerListResponse.observe(this) {
            try {
                resetSupervisorSelection()
                resetZonesSelection()
                resetVillageSelection()

                zoneOfficerList.addAll(it.data!! as List<ZoneOfficerItem>)
                for (item in zoneOfficerList) {
                    zoneOfficerItems.add(item.zoneOfficerName.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.zoneListResponse.observe(this) {
            try {
                resetZonesSelection()
                resetVillageSelection()

                zoneList.addAll(it.data!! as List<SupervisorZoneItem>)
                for (item in zoneList) {
                    zoneItems.add(item.zoneName.toString())
                }
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("1")) {
                    if (zoneItems.isNotEmpty()) {
                        binding.etZone.setText(zoneItems[0])
                        zonePosition = 0
                        viewModel.villageList(VillageListRequest(zoneList[zonePosition!!].zoneId.toString()))

                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.villageListResponse.observe(this) {
            try {
                resetVillageSelection()

                villageList.addAll(it.data!! as List<VillageItem>)
                for (item in villageList) {
                    villageItems.add(item.villageName.toString())
                }

                Log.e("===>", "VillageListSize => ${villageList.size}")

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.paniBandhRegisterResponse.observe(this) {
            try {
                reportList.clear()

                if (it.data.isNullOrEmpty()) {
                    binding.tvNoHistory.visibility = View.VISIBLE
                    binding.layoutReportHeader.visibility = View.GONE
                } else {
                    binding.tvNoHistory.visibility = View.GONE
                    binding.layoutReportHeader.visibility = View.VISIBLE
                    reportList.addAll(it.data!!)
                }

                reportAdapter?.notifyDataSetChanged()

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.etFromDate.id -> {
                showDatePicker(this, null, Calendar.getInstance().timeInMillis) { date ->
                    binding.etFromDate.setText(date)
                }
            }

            binding.etToDate.id -> {
                if (binding.etFromDate.text.toString().isNotEmpty()) {
                    val fromDate = SimpleDateFormat(dateDisplay, Locale.US).parse(binding.etFromDate.text.toString())?.time
                    showDatePicker(this, fromDate, Calendar.getInstance().timeInMillis) { date ->
                        binding.etToDate.setText(date)
                    }
                } else {
                    Const.showSnackBar(this, getString(R.string.please_select_from_date))
                }
            }

            binding.etZoneOfficer.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
                    zoneOfficerSpinnerDialog = SpinnerDialog(this, zoneOfficerItems, getString(R.string.supervisor), getString(R.string.close))

                    zoneOfficerSpinnerDialog!!.setCancellable(true)
                    zoneOfficerSpinnerDialog!!.setShowKeyboard(false)
                    zoneOfficerSpinnerDialog!!.bindOnSpinerListener { item, position ->

                        resetZonesSelection()
                        resetVillageSelection()

                        binding.etZoneOfficer.setText(item)
                        zoneOfficerPosition = position

                        zoneOfficerSpinnerDialog!!.closeSpinerDialog()
                        viewModel.supervisorZoneList(zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString())
                    }
                    zoneOfficerSpinnerDialog!!.showSpinerDialog()
                }
            }

            binding.etZone.id -> {
                if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2") && zoneOfficerPosition == null) {
                    Const.showSnackBar(this, getString(R.string.please_select_supervisor))
                    return
                }

                zoneSpinnerDialog = SpinnerDialog(this, zoneItems, getString(R.string.zone), getString(R.string.close))
                zoneSpinnerDialog!!.setCancellable(true)
                zoneSpinnerDialog!!.setShowKeyboard(false)
                zoneSpinnerDialog!!.bindOnSpinerListener { item, position ->
                    resetVillageSelection()
                    binding.etZone.setText(item)
                    zonePosition = position

                    zoneSpinnerDialog!!.closeSpinerDialog()
                    viewModel.villageList(VillageListRequest(zoneList[zonePosition!!].zoneId.toString()))
                }
                zoneSpinnerDialog!!.showSpinerDialog()

            }

            binding.etVillage.id -> {
                if (zonePosition == null) {
                    Const.showSnackBar(this, getString(R.string.please_select_zone))
                    return
                }

                villageSpinnerDialog = MultiSelectSpinnerDialog<VillageItem>(
                    this,
                    villageList,
                    selectedVillageIds,
                    getString(R.string.gaam),
                    getString(R.string.close),
                    getDisplayName = { it.villageName ?: "" },
                    onSave = {
                        selectedVillageIds.clear()
                        selectedVillageIds.addAll(it)
                        villageSpinnerDialog!!.closeSpinerDialog()
                        displayMultiVillageName()
                    })
                villageSpinnerDialog!!.setCancellable(true)
                villageSpinnerDialog!!.setShowKeyboard(false)
                villageSpinnerDialog!!.showSpinerDialog()

            }

            binding.btnSubmit.id -> {
                validateAndSubmitApi()
            }
        }
    }

    private fun displayMultiVillageName() {
        if(selectedVillageIds.size > 3) {
            val villagesName = selectedVillageIds.subList(0,3).map { it.villageName ?: "" }.toList().joinToString()
            binding.etVillage.setText(Html.fromHtml("$villagesName  <b>+${selectedVillageIds.size - 3}</b>", Html.FROM_HTML_MODE_COMPACT))
        } else {
            binding.etVillage.setText(selectedVillageIds.map { it.villageName ?: "" }.toList().joinToString())
        }
    }

    private fun validateAndSubmitApi() {

        if (binding.etFromDate.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_from_date))
            return
        }

        if (binding.etToDate.text.toString().trim().isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_to_date))
            return
        }

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2") && zoneOfficerPosition == null) {
            Const.showToast(this, getString(R.string.please_select_supervisor))
            return
        }

        if (zonePosition == null) {
            Const.showToast(this, getString(R.string.please_select_zone))
            return
        }

        if (selectedVillageIds.isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_village))
            return
        }

        val reportRequest = KhetarMapniReportRequest(
            working_year = workingYear,
            from_date = dateFormatDynamic(binding.etFromDate.text.toString(), dateDisplay, dateApi),
            to_date = dateFormatDynamic(binding.etToDate.text.toString(), dateDisplay, dateApi),
            zone_id = zoneList[zonePosition!!].zoneId.toString(),
            village_id = selectedVillageIds.map { villageItem -> villageItem.villageId!!.toInt() }.toList(),
        )

        if (SharedPreferenceUtil.getString(Constants.USER_TYPE, "").equals("2")) {
            reportRequest.supervisor_id = zoneOfficerList[zoneOfficerPosition!!].zoneOfficerId.toString()
        } else {
            reportRequest.supervisor_id = SharedPreferenceUtil.getString(Constants.ZONE_SUPERVISOR_ID, "")
        }

        Log.e("TAG", "KhetarMapniReportRequest: $reportRequest")

        binding.tvNoHistory.visibility = View.GONE
        binding.layoutReportHeader.visibility = View.GONE
        reportList.clear()
        reportAdapter?.notifyDataSetChanged()

        viewModel.getKhetarMapniReportApi(reportRequest)
    }

    private fun showDatePicker(
        context: Context,
        minDate: Long?,
        maxDate: Long?,
        onDateSelected: (date: String) -> Any
    ) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { view, year, monthOfYear, dayOfMonth ->
                val dat = String.format(
                    "%02d-%02d-%d",
                    dayOfMonth,
                    (monthOfYear + 1),
                    year
                )
                onDateSelected(dat)
            }, year, month, day
        )
        maxDate?.let {
            datePickerDialog.datePicker.maxDate = it
        }
        minDate?.let {
            datePickerDialog.datePicker.minDate = it
        }
        datePickerDialog.show()
    }

    private val dateDisplay = "dd-MM-yyyy"
    private val dateApi = "yyyy-MM-dd"

    private fun dateFormatDynamic(date: String?, from: String, to: String): String {
        if (date == null) return ""
        try {
            val from = SimpleDateFormat(from, Locale.US)
            val date = from.parse(date)
            val to = SimpleDateFormat(to, Locale.US)
            val result = to.format(date)
            return result
        } catch (e: Exception) {
            return ""
        }
    }

}