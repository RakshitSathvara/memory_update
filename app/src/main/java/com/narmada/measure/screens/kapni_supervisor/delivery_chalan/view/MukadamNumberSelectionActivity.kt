package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityVahanNumberSelectionBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.MukadamNumber
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.view.adapter.MukadamNumberListAdapter
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel.DeliveryChalanRepository
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel.DeliveryChalanViewModelFactory
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel.MukadamNumberListViewModel
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog


class MukadamNumberSelectionActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityVahanNumberSelectionBinding.inflate(layoutInflater) }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: MukadamNumberListViewModel

    private lateinit var workingYear: String

    private lateinit var mukadamNumberListAdapter: MukadamNumberListAdapter
    private val mukadamList = ArrayList<MukadamNumber>()

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.txtTitle.text = getString(R.string.mukadam_number)
        binding.toolbar.ivBack.setOnClickListener(this)

        binding.etSearch.setHint(getString(R.string.mukadam_number))
        binding.btnSearch.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        binding.recyclerInfo.layoutManager = LinearLayoutManager(this)
        mukadamNumberListAdapter = MukadamNumberListAdapter(
            mukadamList
        ) { mukadamNumber ->
            val resultIntent = Intent()
            resultIntent.putExtra("mukadam_number", mukadamNumber)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        binding.recyclerInfo.adapter = mukadamNumberListAdapter

        setupViewModel()

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->

            if (scrollY >= v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if(!viewModel.isLoading && !viewModel.isLastPage) {
                    viewModel.currentPage++
                    getMukadamNumberList()
                }

            }
        })

    }

    private fun getMukadamNumberList() {
        viewModel.getMukadamNumberListApi(workingYear, binding.etSearch.text.toString().trim())
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, DeliveryChalanViewModelFactory(DeliveryChalanRepository(retrofitService!!)))[MukadamNumberListViewModel::class.java]

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

        viewModel.mukadamNumberListResponse.observe(this) {
            try {
                mukadamList.addAll(it.data)
                mukadamNumberListAdapter.notifyDataSetChanged()

                if(mukadamList.isEmpty()) {
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.toolbar.ivBack.id -> {
                finish()
            }


            binding.btnSearch.id -> {

                if(binding.etSearch.text.toString().trim().isEmpty()) {
                    Const.showToast(this, getString(R.string.please_enter_mukadam_name_number))
                    return
                }

                viewModel.currentPage = 1
                viewModel.isLastPage = false

                mukadamList.clear()
                mukadamNumberListAdapter.notifyDataSetChanged()

                getMukadamNumberList()
            }

        }
    }

}