package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivitySerdiDeliveryBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.bareliserdi.model.MapniTypes
import com.narmada.measure.screens.bareliserdi.model.Sankad
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.AddDeliveryChalanRequest
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.CaneType
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.MukadamNumber
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.VahanNumber
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel.DeliveryChalanRepository
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel.DeliveryChalanViewModel
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel.DeliveryChalanViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.LoadingDialog
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SerdiDeliveryChalanActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivitySerdiDeliveryBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: DeliveryChalanViewModel

    private var caneTypeSpinnerDialog: SpinnerDialog? = null
    private var mapniTypeSpinnerDialog: SpinnerDialog? = null
    private var sankadSpinnerDialog: SpinnerDialog? = null

    private var caneTypeList: ArrayList<CaneType> = ArrayList()
    private var mapniTypeList: ArrayList<MapniTypes> = ArrayList()
    private var sankadList: ArrayList<Sankad> = ArrayList()

    private var caneTypeItems: ArrayList<String> = ArrayList()
    private var mapniTypeItems: ArrayList<String> = ArrayList()
    private var sankadItems: ArrayList<String> = ArrayList()

    private var caneTypePosition: Int? = null
    private var mapniTypePosition: Int? = null
    private var sankadPosition: Int? = null

    private var isKhetarCodeVerified = false
    private var accountId: String? = null

    private var vahanNumber: VahanNumber? = null
    private var vahanNumberOne: VahanNumber? = null
    private var mukadamNumber: MukadamNumber? = null

    private lateinit var workingYear: String

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data ?: return@registerForActivityResult
                // Handle the result data here

                if (intent.hasExtra("vahan_number")) {
                    vahanNumber = intent.getSerializable("vahan_number", VahanNumber::class.java)
                    binding.etVahanNumber.setText(vahanNumber?.transportName)
                }

                if (intent.hasExtra("vahan_number_one")) {
                    vahanNumberOne =
                        intent.getSerializable("vahan_number_one", VahanNumber::class.java)
                    binding.etVahanNumberOne.setText(vahanNumberOne?.transportName)
                }

                if (intent.hasExtra("mukadam_number")) {
                    mukadamNumber =
                        intent.getSerializable("mukadam_number", MukadamNumber::class.java)
                    binding.etMukadamNumber.setText("${mukadamNumber?.mukadamID} / ${mukadamNumber?.mukadamName}")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.serdi_delivery_chalan)
        toolbarBinding.ivBack.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        binding.btnKhetarCode.setOnClickListener(this)
        binding.etCaneType.setOnClickListener(this)
        binding.etLaamRopan.setOnClickListener(this)
        binding.etSankad.setOnClickListener(this)
        binding.etVahanNumber.setOnClickListener(this)
        binding.etVahanNumberOne.setOnClickListener(this)
        binding.etMukadamNumber.setOnClickListener(this)
        binding.etTarikhIn.setOnClickListener(this)
        binding.etSamayIn.setOnClickListener(this)
        binding.etTarikhOut.setOnClickListener(this)
        binding.etSamayOut.setOnClickListener(this)
        binding.etKapniPuriThayaniTarikh.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        binding.etDate.setText(
            SimpleDateFormat(
                dateDisplay,
                Locale.US
            ).format(Calendar.getInstance().time)
        )
        binding.etKapniPuriThayaniTarikh.setText(
            SimpleDateFormat(dateDisplay, Locale.US).format(
                Calendar.getInstance().time
            )
        )

        binding.etKhetarCode.addTextChangedListener { text ->
            resetPaniBandhDetail()
        }

        setupViewModel()

        viewModel.getCommonDataApi(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, DeliveryChalanViewModelFactory(
                DeliveryChalanRepository(retrofitService!!)
            )
        )[DeliveryChalanViewModel::class.java]

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

        viewModel.commonDataObservable.observe(this) {
            try {
                mapniTypeList.clear()
                mapniTypeList.addAll(it.data!!.mapniTypes)
//                mapniTypeList.add(MapniTypes(null, getString(R.string.mapni_type_all_shree_guj)))
                for (item in mapniTypeList) {
                    mapniTypeItems.add(item.name.toString())
                }

                sankadList.clear()
                sankadList.addAll(it.data.sankad)
                for (item in sankadList) {
                    sankadItems.add(item.name.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }

            viewModel.zoneOfficerList()
        }

        viewModel.caneTypeListResponse.observe(this) {
            try {
                caneTypeList.clear()
                caneTypeList.addAll(it.data)
                for (item in caneTypeList) {
                    caneTypeItems.add(item.caneType.toString())
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }

        }

        viewModel.paniBandhDetailResponse.observe(this) {
            try {

                it.data?.let { paniBandhDetail ->
                    isKhetarCodeVerified = true
                    binding.txtKhetarCode.visibility = View.VISIBLE
                    accountId = paniBandhDetail.accountId

                    binding.txtSabhasadName.text = paniBandhDetail.accountName
                    binding.txtSabhasadCode.text = paniBandhDetail.accountId
                    binding.txtSabhasadVillage.text = paniBandhDetail.villageName
                    binding.txtVillageCodeNumber.text = paniBandhDetail.villageId
                    binding.txtMojeGaam.text = paniBandhDetail.mojeVillageName
                    binding.txtMojeGaamCode.text = paniBandhDetail.mojeVillageId
                    val type =
                        mapniTypeList.find { mapniTypes: MapniTypes -> mapniTypes.id?.toString() == paniBandhDetail.lamRopan }
                    if (type != null) {
                        mapniTypePosition = mapniTypeList.indexOf(type)
                        binding.txtLaamRopan.text = mapniTypeList[mapniTypePosition!!].name
                        binding.etLaamRopan.setText(mapniTypeList[mapniTypePosition!!].name)
                    }
                    binding.txtSherdiJaat.text =
                        "${paniBandhDetail.itemId} / ${paniBandhDetail.itemName}"
                } ?: {
                    isKhetarCodeVerified = false
                    binding.txtKhetarCode.visibility = View.GONE
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }

        }

        viewModel.addDeliveryChalanResponse.observe(this) {
            try {

                val finalMessage =
                    "${it.message ?: ""}\n\n${getString(R.string.agri_receipt_number)} : ${it.data?.docNo ?: ""}"
                showSuccessDialog(finalMessage)

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }

        }

    }

    private fun resetPaniBandhDetail() {
        isKhetarCodeVerified = false
        binding.txtKhetarCode.visibility = View.GONE

        binding.txtSabhasadName.text = ""
        binding.txtSabhasadCode.text = ""
        binding.txtSabhasadVillage.text = ""
        binding.txtVillageCodeNumber.text = ""
        binding.txtMojeGaam.text = ""
        binding.txtMojeGaamCode.text = ""
        binding.txtLaamRopan.text = ""
        binding.txtSherdiJaat.text = ""
    }

    private fun showSuccessDialog(message: String) {
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
        tvClose.text = getString(R.string.cont)

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

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.btnKhetarCode.id -> {
                val khetarCode = binding.etKhetarCode.text.toString().trim()

                if (khetarCode.isEmpty()) {
                    Const.showToast(this, getString(R.string.please_enter_khetar_code))
                    return
                }

                viewModel.kapniSupervisorPaniBandhDetail(
                    workingYear = workingYear,
                    khetarCode = khetarCode
                )
            }

            binding.etCaneType.id -> {
                caneTypeSpinnerDialog =
                    SpinnerDialog(
                        this,
                        caneTypeItems,
                        getString(R.string.serdi_prakar),
                        getString(R.string.close)
                    )

                caneTypeSpinnerDialog!!.setCancellable(true)
                caneTypeSpinnerDialog!!.setShowKeyboard(false)
                caneTypeSpinnerDialog!!.bindOnSpinerListener { item, position ->

                    binding.etCaneType.setText(item)
                    caneTypePosition = position

                    caneTypeSpinnerDialog!!.closeSpinerDialog()
                }
                caneTypeSpinnerDialog!!.showSpinerDialog()
            }

            binding.etLaamRopan.id -> {
                mapniTypeSpinnerDialog =
                    SpinnerDialog(
                        this,
                        mapniTypeItems,
                        getString(R.string.laam_ropan_guj),
                        getString(R.string.close)
                    )

                mapniTypeSpinnerDialog!!.setCancellable(true)
                mapniTypeSpinnerDialog!!.setShowKeyboard(false)
                mapniTypeSpinnerDialog!!.bindOnSpinerListener { item, position ->

                    binding.etLaamRopan.setText(item)
                    mapniTypePosition = position

                    mapniTypeSpinnerDialog!!.closeSpinerDialog()
                }
                mapniTypeSpinnerDialog!!.showSpinerDialog()
            }

            binding.etSankad.id -> {
                sankadSpinnerDialog =
                    SpinnerDialog(
                        this,
                        sankadItems,
                        getString(R.string.sankad),
                        getString(R.string.close)
                    )

                sankadSpinnerDialog!!.setCancellable(true)
                sankadSpinnerDialog!!.setShowKeyboard(false)
                sankadSpinnerDialog!!.bindOnSpinerListener { item, position ->

                    binding.etSankad.setText(item)
                    sankadPosition = position

                    sankadSpinnerDialog!!.closeSpinerDialog()
                }
                sankadSpinnerDialog!!.showSpinerDialog()
            }

            binding.etVahanNumber.id -> {
                val intent = Intent(this, VahanNumberSelectionActivity::class.java)
                intent.putExtra("working_year", workingYear)
                intent.putExtra("is_vahan_one", false)
                startForResult.launch(intent)
            }

            binding.etVahanNumberOne.id -> {
                val intent = Intent(this, VahanNumberSelectionActivity::class.java)
                intent.putExtra("working_year", workingYear)
                intent.putExtra("is_vahan_one", true)
                startForResult.launch(intent)
            }

            binding.etMukadamNumber.id -> {
                val intent = Intent(this, MukadamNumberSelectionActivity::class.java)
                intent.putExtra("working_year", workingYear)
                startForResult.launch(intent)
            }

            binding.etTarikhIn.id -> {
                showDatePicker(this) { date ->
                    binding.etTarikhIn.setText(date)
                }
            }

            binding.etSamayIn.id -> {
                showTimePicker(this) { time ->
                    binding.etSamayIn.setText(time)
                }
            }

            binding.etTarikhOut.id -> {
                showDatePicker(this) { date ->
                    binding.etTarikhOut.setText(date)
                }
            }

            binding.etSamayOut.id -> {
                showTimePicker(this) { time ->
                    binding.etSamayOut.setText(time)
                }
            }

            binding.etKapniPuriThayaniTarikh.id -> {
                showDatePicker(this, minDate = Calendar.getInstance().time.time) { date ->
                    binding.etKapniPuriThayaniTarikh.setText(date)
                }
            }

            binding.btnSubmit.id -> {
                validateAndSubmit()
            }
        }
    }

    private fun validateAndSubmit() {

        val topDate = binding.etDate.text.toString()
        val inDateTime =
            "${binding.etTarikhIn.text.toString()} ${binding.etSamayIn.text.toString()}"
        val outDateTime =
            "${binding.etTarikhOut.text.toString()} ${binding.etSamayOut.text.toString()}"
        val completeDate = binding.etKapniPuriThayaniTarikh.text.toString()

        if (!isKhetarCodeVerified) {
            Const.showToast(this, getString(R.string.please_verify_khetar_code))
            return
        }

        if (caneTypePosition == null) {
            Const.showToast(this, getString(R.string.please_select_sherdi_type))
            return
        }

        if (mapniTypePosition == null) {
            Const.showToast(this, getString(R.string.please_select_laam_ropan))
            return
        }

        if (sankadPosition == null) {
            Const.showToast(this, getString(R.string.please_select_sankad))
            return
        }

        if (vahanNumber == null) {
            Const.showToast(this, getString(R.string.please_select_vehicle_number))
            return
        }

        if (mukadamNumber == null) {
            Const.showToast(this, getString(R.string.please_select_mukadam_name_number))
            return
        }

        if (binding.etTarikhIn.text.toString().isEmpty() || binding.etSamayIn.text.toString()
                .isEmpty()
        ) {
            Const.showToast(this, getString(R.string.please_select_khetar_in_time))
            return
        }

        if (binding.etTarikhOut.text.toString().isNotEmpty() && binding.etSamayOut.text.toString()
                .isEmpty()
        ) {
            Const.showToast(this, getString(R.string.please_select_khetar_out_time))
            return
        }

        if (binding.etKapniPuriThayaniTarikh.text.toString().isEmpty()) {
            Const.showToast(this, getString(R.string.please_select_kapni_complete_date))
            return
        }

        if (binding.etRasidNumber.text.toString().isEmpty()) {
            Const.showToast(this, getString(R.string.please_enter_rasid_number))
            return
        }

        val from = SimpleDateFormat(dateTimeDisplay, Locale.US)
        val inDateTimestamp = from.parse(inDateTime)
        if (outDateTime.trim().isNotEmpty()) {
            val outDateTimestamp = from.parse(outDateTime)

            if (outDateTimestamp.time <= inDateTimestamp.time) {
                Const.showToast(
                    this,
                    getString(R.string.khetar_out_time_must_be_greater_than_in_time)
                )
                return
            }
        }

        val request = AddDeliveryChalanRequest(
            working_year = workingYear,
            date = dateFormatDynamic(topDate, dateDisplay, dateApi),
            computer_code = binding.etKhetarCode.text.toString(),
            account_id = accountId.toString(),
            lam_ropan = mapniTypeList[mapniTypePosition!!].id.toString(),
            sankal = sankadList[sankadPosition!!].id.toString(),
            cane_type = caneTypeList[caneTypePosition!!].caneCode.toString(),
            transport_id = vahanNumber?.transportID.toString(),
            transport_id_one = if (vahanNumberOne != null) vahanNumberOne?.transportID.toString() else "",
            driver_name = binding.etDriverName.text.toString(),
            mukadam_id = mukadamNumber?.mukadamID.toString(),
            farm_in = dateFormatDynamic(inDateTime, dateTimeDisplay, dateTimeApi),
            farm_out = dateFormatDynamic(outDateTime.trim(), dateTimeDisplay, dateTimeApi),
            cutting_close_date = dateFormatDynamic(completeDate, dateDisplay, dateApi),
            rasid_number = binding.etRasidNumber.text.toString()
        )

        viewModel.addCaneDeliveryChalanApi(request)
    }

    private fun showDatePicker(
        context: Context,
        maxDate: Long? = null,
        minDate: Long? = null,
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
                    Locale.US,
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

    private fun showTimePicker(context: Context, onTimeSelected: (time: String) -> Any) {
        val c = Calendar.getInstance()
        val mTimePicker: TimePickerDialog
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->

                onTimeSelected(String.format(Locale.US, "%02d:%02d", hourOfDay, minute))

            }, hour, minute, true
        )
        mTimePicker.show()
    }


    private val dateApiResponse = "yyyy-MM-dd"
    private val dateDisplay = "dd-MM-yyyy"
    private val dateApi = "yyyy-MM-dd"
    private val timeDisplay = "HH:mm"
    private val timeApi = "HH:mm:ss"

    private val dateTimeDisplay = "dd-MM-yyyy HH:mm"
    private val dateTimeApi = "yyyy-MM-dd HH:mm"

    private fun dateFormatDynamic(date: String?, from: String, to: String): String {
        if (date.isNullOrEmpty()) return ""
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