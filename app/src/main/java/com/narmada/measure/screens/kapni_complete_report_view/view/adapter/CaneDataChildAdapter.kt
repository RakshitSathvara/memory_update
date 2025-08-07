package com.narmada.measure.screens.kapni_complete_report_view.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.databinding.ItemKapniPuriThayaniMahitiChildBinding
import com.narmada.measure.screens.kapni_complete_report_view.model.CaneReceiptChildData
import java.text.SimpleDateFormat
import java.util.Locale

class CaneDataChildAdapter(
    private val dataList: MutableList<CaneReceiptChildData>
) :
    RecyclerView.Adapter<CaneDataChildAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKapniPuriThayaniMahitiChildBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // item_kapni_puri_thayani_mahiti_child
        val binding = ItemKapniPuriThayaniMahitiChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(dataList[position]) {

                binding.tvReceiptNo.text = docNo
                binding.tvReceiptDate.text = dateFormatDynamic(docDate, dateApi, dateDisplay)
                binding.tvLaamRopan.text = lamRopan
                binding.tvSankad.text = sankal
                binding.tvSherdiType.text = caneType
                binding.tvVehicleNumber.text = vehicleNumber
                binding.tvVehicleType.text = transportSadhan
                binding.tvDriverName.text = driverName
                binding.tvMukadamNumber.text = mukadamCode
                binding.tvKhetarInTime.text = dateFormatDynamic(farmIn, dateTimeApi, dateTimeDisplay)
                binding.tvKhetarOutTime.text = dateFormatDynamic(farmOut, dateTimeApi, dateTimeDisplay)
                binding.tvCuttingCompleteDate.text = dateFormatDynamic(cuttingCloseDate, dateApi, dateDisplay)

                if(dataList.size == position + 1) {
                    binding.seperator.visibility = View.GONE
                } else {
                    binding.seperator.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private val dateDisplay = "dd/MM/yy"
    private val dateApi = "dd-MM-yyyy"

    private val dateTimeDisplay = "dd/MM/yy - HH:mm"
    private val dateTimeApi = "dd-MM-yyyy HH:mm"

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

