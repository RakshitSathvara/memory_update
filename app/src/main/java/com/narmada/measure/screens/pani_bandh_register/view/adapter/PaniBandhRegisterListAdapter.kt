package com.narmada.measure.screens.pani_bandh_register.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.databinding.ItemPaniBandhReportListBinding
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhData
import java.text.SimpleDateFormat
import java.util.Locale

/* Gemini: Add callback listener to below adapter. */

class PaniBandhRegisterListAdapter(
    private val villageId: String,
    private val village: String,
    private val dataList: MutableList<PaniBandhData>,
    private val onItemClickListener: ((PaniBandhData) -> Unit)
) :
    RecyclerView.Adapter<PaniBandhRegisterListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPaniBandhReportListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaniBandhReportListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(dataList[position]) {

                binding.tvComputerCode.text = computerCode
                binding.tvSabhasadCode.text = accountId
                binding.tvSabhasadName.text = accountName
                binding.tvVillage.text = "$villageId / $village"
                binding.tvType.text = itemName
                binding.tvLaamRopanType.text = lamRopan
                binding.tvLaamRopanDate.text = dateFormatDynamic(approxRopanDate, dateApi, dateDisplay)
                binding.tvArea.text = ropanArea
                binding.tvMobileNo.text = phoneNo

                binding.btnView.setOnClickListener {
                    onItemClickListener.invoke(this)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private val dateDisplay = "dd/MM/yy"
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

