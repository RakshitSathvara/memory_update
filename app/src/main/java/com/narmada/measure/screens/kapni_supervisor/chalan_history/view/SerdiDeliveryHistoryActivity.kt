package com.narmada.measure.screens.kapni_supervisor.chalan_history.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivitySerdiDeliveryHistoryBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.kapni_supervisor.chalan_history.model.DeliveryChalan
import com.narmada.measure.screens.kapni_supervisor.chalan_history.view.adapter.DeliveryChalanListAdapter
import com.narmada.measure.screens.kapni_supervisor.chalan_history.viewmodel.DeliveryChalanHistoryRepository
import com.narmada.measure.screens.kapni_supervisor.chalan_history.viewmodel.DeliveryChalanHistoryViewModel
import com.narmada.measure.screens.kapni_supervisor.chalan_history.viewmodel.DeliveryChalanHistoryViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SerdiDeliveryHistoryActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivitySerdiDeliveryHistoryBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: DeliveryChalanHistoryViewModel

    lateinit var chalanListAdapter: DeliveryChalanListAdapter
    private val yaadiList = ArrayList<DeliveryChalan>()

    private lateinit var workingYear: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.serdi_delivery_history)
        toolbarBinding.ivBack.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        binding.etFromDate.setOnClickListener(this)
        binding.etToDate.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        binding.recyclerInfo.layoutManager = LinearLayoutManager(this)
        chalanListAdapter = DeliveryChalanListAdapter(yaadiList)
        binding.recyclerInfo.adapter = chalanListAdapter

        setupViewModel()

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->

            if (scrollY >= v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if(!viewModel.isLoading && !viewModel.isLastPage) {
                    viewModel.currentPage++
                    getDeliveryChalanList()
                }

            }
        })
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, DeliveryChalanHistoryViewModelFactory(
            DeliveryChalanHistoryRepository(retrofitService!!)
        ))[DeliveryChalanHistoryViewModel::class.java]

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

        viewModel.paginationProgressObservable.observe(this) {
            if (it == true) {
                binding.progressbar.visibility = View.VISIBLE
//                binding.scrollView.scrollTo(0, binding.scrollView.bottom)
            } else {
                binding.progressbar.visibility = View.GONE
                binding.scrollView.scrollTo(0, binding.scrollView.scrollY)
            }
        }

        viewModel.deliveryChalanListResponse.observe(this) {
            try {
                yaadiList.addAll(it.data?.deliveryChallan ?: arrayListOf())
                chalanListAdapter.notifyDataSetChanged()

                if(yaadiList.isEmpty()) {
                    binding.layoutNoHistory.visibility = View.VISIBLE
                } else {
                    binding.layoutNoHistory.visibility = View.GONE
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

    }

    private fun getDeliveryChalanList() {

        viewModel.getDeliveryChalanListApi(
            workingYear = workingYear,
            fromDate = dateFormatDynamic(binding.etFromDate.text.toString(), dateDisplay, dateApi),
            toDate = dateFormatDynamic(binding.etToDate.text.toString(), dateDisplay, dateApi),
            khetarCode = binding.etKhetarCode.text.toString(),
        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.etFromDate.id -> {
                showDatePicker(this, null, Calendar.getInstance().timeInMillis) { date ->
                    binding.etFromDate.setText(date)
                    binding.etToDate.setText("")
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

            binding.btnSubmit.id -> {

                val khetarCode = binding.etKhetarCode.text.toString().trim()
                val fromDate = binding.etFromDate.text.toString().trim()
                val toDate = binding.etToDate.text.toString().trim()

//                if (khetarCode.isEmpty()) {
//                    Const.showToast(this, getString(R.string.please_enter_khetar_code))
//                    return
//                }

                if (fromDate.isEmpty()) {
                    Const.showToast(this, getString(R.string.please_select_from_date))
                    return
                }

                if (toDate.isEmpty()) {
                    Const.showToast(this, getString(R.string.please_select_to_date))
                    return
                }

                viewModel.currentPage = 1
                viewModel.isLastPage = false

                yaadiList.clear()
                chalanListAdapter.notifyDataSetChanged()

                binding.layoutNoHistory.visibility = View.GONE

                getDeliveryChalanList()
            }

        }
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
                val dat = String.format(Locale.US,
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

    private fun dateFormatDynamic(dateString: String?, fromFormat: String, toFormat: String): String {
        if (dateString == null) return ""
        try {
            val from = SimpleDateFormat(fromFormat, Locale.US)
            val date = from.parse(dateString)
            val to = SimpleDateFormat(toFormat, Locale.US)
            val result = to.format(date)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

}