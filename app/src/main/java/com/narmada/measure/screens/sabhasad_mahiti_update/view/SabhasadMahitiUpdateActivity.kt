package com.narmada.measure.screens.sabhasad_mahiti_update.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivitySabhasadMahitiUpdateBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.sabhasad_mahiti_update.model.LinkedMember
import com.narmada.measure.screens.sabhasad_mahiti_update.model.MemberDetailByCodeRequest
import com.narmada.measure.screens.sabhasad_mahiti_update.model.SabhasadMahitiUpdateRequest
import com.narmada.measure.screens.sabhasad_mahiti_update.view.adapter.SabhasadListAdapter
import com.narmada.measure.screens.sabhasad_mahiti_update.viewmodel.SabhasadMahitiUpdateRepository
import com.narmada.measure.screens.sabhasad_mahiti_update.viewmodel.SabhasadMahitiUpdateViewModel
import com.narmada.measure.screens.sabhasad_mahiti_update.viewmodel.SabhasadMahitiUpdateViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.LoadingDialog


class SabhasadMahitiUpdateActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivitySabhasadMahitiUpdateBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: SabhasadMahitiUpdateViewModel

    private lateinit var workingYear: String

    private var sabhasadCode: String? = null

    lateinit var sabhasadListAdapter: SabhasadListAdapter
    private val linkedMemberList: ArrayList<LinkedMember> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.sabhasad_mahiti)
        toolbarBinding.ivBack.setOnClickListener(this)

        binding.linearSubmit.setOnClickListener(this)
        binding.linearUpdate.setOnClickListener(this)

        workingYear = intent.getStringExtra("working_year").orEmpty()

        binding.recyclerSabhasad.layoutManager = LinearLayoutManager(this)
        sabhasadListAdapter = SabhasadListAdapter(linkedMemberList)
        binding.recyclerSabhasad.adapter = sabhasadListAdapter

        setupViewModel()

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, SabhasadMahitiUpdateViewModelFactory(SabhasadMahitiUpdateRepository(retrofitService!!)))[SabhasadMahitiUpdateViewModel::class.java]

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

        viewModel.memberDetailByCodeResponse.observe(this) {
            try {

                binding.layoutMahitiUpdate.visibility = View.VISIBLE

                sabhasadCode = it.data?.farmerId
                binding.etSabhaName.setText(it.data?.farmerName)
                binding.etMobileNumber.setText(it.data?.mobileNumber)

                linkedMemberList.clear()

                if(it.data?.members.isNullOrEmpty()) {
                    binding.layoutOtherMembers.visibility = View.GONE
                    binding.txtNote.visibility = View.GONE
                    binding.txtNote.text = getString(R.string.note_sabhasad_mahiti_update_members_not_linked)
                } else {
                    linkedMemberList.addAll(it.data?.members!!)
                    binding.layoutOtherMembers.visibility = View.VISIBLE
                    binding.txtNote.visibility = View.VISIBLE
                    binding.txtNote.text = getString(R.string.note_sabhasad_mahiti_update)
                }
                sabhasadListAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.updateMemberDetailByCodeResponse.observe(this) {
            try {

                Const.showSnackBar(this, it!!.message!!)

                binding.layoutMahitiUpdate.visibility = View.GONE
                binding.etSabhaName.setText("")
                binding.etMobileNumber.setText("")
                sabhasadCode = null

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

            binding.linearSubmit.id -> {
                validateAndGetDetailApi()
            }

            binding.linearUpdate.id -> {
                validateAndUpdateDetailApi()
            }
        }
    }

    private fun validateAndGetDetailApi() {

        if (binding.etSabhaCode.text.toString().trim().isEmpty()) {
            Const.showSnackBar(this, getString(R.string.enter_valid_sabha_code))
            return
        }

        val reportRequest = MemberDetailByCodeRequest(
            account_id = binding.etSabhaCode.text.toString().trim(),
        )

        Log.e("TAG", "MemberDetailByCodeRequest: $reportRequest")

        viewModel.getMemberDetailByCode(reportRequest)

        binding.layoutMahitiUpdate.visibility = View.GONE
        binding.etSabhaName.setText("")
        binding.etMobileNumber.setText("")
        sabhasadCode = null
    }

    private fun validateAndUpdateDetailApi() {

        if (sabhasadCode.isNullOrEmpty()) {
            Const.showSnackBar(this, getString(R.string.enter_valid_sabha_code))
            return
        }

        if (binding.etMobileNumber.text.toString().trim().length != 10) {
            Const.showSnackBar(this, getString(R.string.valid_mobile_number))
            return
        }

        val selectedMemberIdList = arrayListOf<String>()

        linkedMemberList.filter { it.isSelected }
            .map { it.farmerId ?: "" }
            .forEach { selectedMemberIdList.add(it) }

        val reportRequest = SabhasadMahitiUpdateRequest(
            farmerId = sabhasadCode,
            mobileNumber = binding.etMobileNumber.text.toString().trim(),
            members = selectedMemberIdList,
        )

        Log.e("TAG", "MemberDetailByCodeRequest: $reportRequest")

        viewModel.updateMemberDetailByCode(reportRequest)

    }

}