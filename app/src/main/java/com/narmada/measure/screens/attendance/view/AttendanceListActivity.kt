package com.narmada.measure.screens.attendance.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityAttendanceListBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.attendance.model.AttendanceListItem
import com.narmada.measure.screens.attendance.view.adapter.AttendanceAdapter
import com.narmada.measure.screens.attendance.viewmodel.AttendanceRepository
import com.narmada.measure.screens.attendance.viewmodel.AttendanceViewModel
import com.narmada.measure.screens.attendance.viewmodel.AttendanceViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.showSnackBar
import com.narmada.measure.utils.LoadingDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class AttendanceListActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityAttendanceListBinding.inflate(layoutInflater) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    lateinit var viewModel: AttendanceViewModel
    val c = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    val format = SimpleDateFormat("yyyy-MM-dd")
    var startDate: Date? = null
    var endDate: Date? = null
    private var attendanceList: MutableList<AttendanceListItem> = arrayListOf()
    private var attendanceAdapter: AttendanceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.etStart.setOnClickListener(this)
        binding.etEnd.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.cardIn.setOnClickListener(this)
        binding.cardOut.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerAttendance.layoutManager = layoutManager
        attendanceAdapter = AttendanceAdapter(attendanceList)
        binding.recyclerAttendance.adapter = attendanceAdapter
        setupViewModel()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, AttendanceViewModelFactory(AttendanceRepository(retrofitService!!))
        )[AttendanceViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            showSnackBar(this, it)
        }

        viewModel.errorIntMessage.observe(this) {
            showSnackBar(this, getString(it))
        }

        viewModel.progressObservable.observe(this) {
            if (it == true) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

        viewModel.attendanceHistoryResponse.observe(this) {
            attendanceList.clear()
            if (it.data!!.inOutTimeStatus == 0) {
                binding.cardIn.visibility = View.VISIBLE
                binding.cardOut.visibility = View.VISIBLE
            } else if (it.data.inOutTimeStatus == 1) {
                binding.cardIn.visibility = View.GONE
                binding.cardOut.visibility = View.VISIBLE
            } else if (it.data.inOutTimeStatus == 2) {
                binding.cardIn.visibility = View.GONE
                binding.cardOut.visibility = View.GONE
            }
            if (it.data.attendanceList!!.isNotEmpty()) {
                binding.tvNoHistory.visibility = View.GONE
                attendanceList.addAll(it.data.attendanceList as List<AttendanceListItem>)
            } else {
                binding.tvNoHistory.visibility = View.VISIBLE
            }
            attendanceAdapter?.notifyDataSetChanged()
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {

            binding.ivBack.id -> {
                finish()
            }

            binding.cardIn.id -> {
                val intent = Intent(this, AttendanceInOutActivity::class.java)
                intent.putExtra("type", 1)
                startActivity(intent)
            }

            binding.cardOut.id -> {
                val intent = Intent(this, AttendanceInOutActivity::class.java)
                intent.putExtra("type", 2)
                startActivity(intent)
            }

            binding.btnSubmit.id -> {
                if (binding.etStart.text.toString().isEmpty()) {
                    showSnackBar(this, getString(R.string.please_select_from_date))
                } else if (binding.etEnd.text.toString().isEmpty()) {
                    showSnackBar(this, getString(R.string.please_select_to_date))
                } else {
                    viewModel.attendanceHistory(
                        binding.etStart.text.toString(),
                        binding.etEnd.text.toString()
                    )
                }
            }

            binding.etStart.id -> {
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    this,
                    { view, _year, _monthOfYear, _dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar[_year, _monthOfYear] = _dayOfMonth


                        val strDate: String = format.format(calendar.time)
                        startDate = format.parse(strDate)
                        binding.etStart.setText(strDate)
                        binding.etEnd.setText("")
                    }, year, month, day
                )
                datePickerDialog.datePicker.maxDate = c.timeInMillis
                datePickerDialog.show()
            }

            binding.etEnd.id -> {
                if (binding.etStart.text.toString().isNotEmpty()) {
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog = DatePickerDialog(
                        this,
                        { view, _year, _monthOfYear, _dayOfMonth ->
                            val calendar = Calendar.getInstance()
                            calendar[_year, _monthOfYear] = _dayOfMonth

                            val strDate: String = format.format(calendar.time)

                            binding.etEnd.setText(strDate)
                        }, year, month, day
                    )
                    datePickerDialog.datePicker.maxDate = c.timeInMillis
                    datePickerDialog.datePicker.minDate = startDate!!.time
                    val cal = Calendar.getInstance()
                    cal.time = startDate!!
                    datePickerDialog.datePicker.updateDate(
                        cal[Calendar.YEAR],
                        cal[Calendar.MONTH],
                        cal[Calendar.DAY_OF_MONTH]
                    )
                    datePickerDialog.show()
                } else {
                    showSnackBar(this, getString(R.string.please_select_from_date))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.attendanceHistory("","")
    }

}