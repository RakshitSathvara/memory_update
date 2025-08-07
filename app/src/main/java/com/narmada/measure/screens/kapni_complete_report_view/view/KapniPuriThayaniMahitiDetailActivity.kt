package com.narmada.measure.screens.kapni_complete_report_view.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityKapniPuriThayaniMahitiDetailBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.kapni_complete_report_view.model.CaneReceiptMainItem
import com.narmada.measure.screens.kapni_complete_report_view.model.KapniPuriThayaniMahitiIntent
import com.narmada.measure.screens.kapni_complete_report_view.view.adapter.CaneDataParentAdapter
import com.narmada.measure.screens.kapni_complete_report_view.viewmodel.KapniPuriThayaniMahitiRepository
import com.narmada.measure.screens.kapni_complete_report_view.viewmodel.KapniPuriThayaniMahitiViewModel
import com.narmada.measure.screens.kapni_complete_report_view.viewmodel.KapniPuriThayaniMahitiViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.LoadingDialog

class KapniPuriThayaniMahitiDetailActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityKapniPuriThayaniMahitiDetailBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: KapniPuriThayaniMahitiViewModel

    private lateinit var kapniPuriThayaniMahitiIntent: KapniPuriThayaniMahitiIntent

    private lateinit var caneDataParentAdapter: CaneDataParentAdapter
    private val caneDataList = ArrayList<CaneReceiptMainItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.kapni_puri_thayani_mahiti)
        toolbarBinding.ivBack.setOnClickListener(this)

        kapniPuriThayaniMahitiIntent = intent.getSerializable("intent", KapniPuriThayaniMahitiIntent::class.java)

        binding.recyclerInfo.layoutManager = LinearLayoutManager(this)
        caneDataParentAdapter = CaneDataParentAdapter(caneDataList)
        binding.recyclerInfo.adapter = caneDataParentAdapter
//
        setupViewModel()
        getPaniBandhListApi()

    }


    @SuppressLint("SetTextI18n")
    private fun setupViewModel() {
        binding.tvSupervisor.text = kapniPuriThayaniMahitiIntent.supervisorName
        binding.tvZone.text = kapniPuriThayaniMahitiIntent.zone.zoneName
        binding.tvKhetarCode.text = kapniPuriThayaniMahitiIntent.khetarCode
        binding.tvSabhasadCode.text = kapniPuriThayaniMahitiIntent.sabhasadCode

        if(kapniPuriThayaniMahitiIntent.khetarCode.isEmpty()) binding.layoutKhetarCode.visibility = View.GONE
        if(kapniPuriThayaniMahitiIntent.sabhasadCode.isEmpty()) binding.layoutSabhasadCode.visibility = View.GONE

        viewModel = ViewModelProvider(this, KapniPuriThayaniMahitiViewModelFactory(
            KapniPuriThayaniMahitiRepository(retrofitService!!)
        ))[KapniPuriThayaniMahitiViewModel::class.java]

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
            } else {
                binding.progressbar.visibility = View.GONE
                binding.scrollView.scrollTo(0, binding.scrollView.scrollY)

                if(caneDataList.isEmpty()) {
                    binding.layoutNoHistory.visibility = View.VISIBLE
                } else {
                    binding.layoutNoHistory.visibility = View.GONE
                }
            }
        }

        viewModel.kapniCompleteReportListResponse.observe(this) {
            try {

                caneDataList.addAll(it.data ?: arrayListOf())
                caneDataParentAdapter.notifyDataSetChanged()

                if(caneDataList.isEmpty()) {
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

    private fun getPaniBandhListApi() {
        viewModel.kapniCompleteReportList(kapniPuriThayaniMahitiIntent)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }
        }
    }
}