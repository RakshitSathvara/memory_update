package com.narmada.measure.screens.kapni_complete_report_view.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.databinding.ItemKapniPuriThayaniMahitiParentBinding
import com.narmada.measure.screens.kapni_complete_report_view.model.CaneReceiptMainItem
import java.text.SimpleDateFormat
import java.util.Locale

class CaneDataParentAdapter(
    private val dataList: MutableList<CaneReceiptMainItem>
) :
    RecyclerView.Adapter<CaneDataParentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKapniPuriThayaniMahitiParentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKapniPuriThayaniMahitiParentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(dataList[position]) {

                binding.tvKapniReportNumber.text = reportNo
                binding.tvDate.text = dateFormatDynamic(reportDate, dateApi, dateDisplay)
                binding.tvKhetarCode.text = computerCode
                binding.tvSabhasadName.text = accountName
                binding.tvSabhasadCode.text = accountId

                binding.recyclerInfo.layoutManager = LinearLayoutManager(binding.tvDate.context)
                val caneDataChildAdapter = CaneDataChildAdapter(caneReceiptData ?: arrayListOf())
                binding.recyclerInfo.adapter = caneDataChildAdapter
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private val dateDisplay = "dd/MM/yy"
    private val dateApi = "dd-MM-yyyy"

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

