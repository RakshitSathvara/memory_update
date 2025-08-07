package com.narmada.measure.screens.kapni_complete_report.view.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.databinding.ItemKapniCompleteKhetarInfoBinding
import com.narmada.measure.screens.kapni_complete_report.model.WeightListItem
import java.text.SimpleDateFormat
import java.util.Locale

class KapniKhetarInfoAdapter(
    private val dataList: MutableList<WeightListItem>
) :
    RecyclerView.Adapter<KapniKhetarInfoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKapniCompleteKhetarInfoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKapniCompleteKhetarInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(dataList[position]) {

                binding.tvReceiptNo.text = docNo
                binding.tvReceiptDate.text = dateFormatDynamic(docDate, dateApi, dateDisplay)
                binding.tvKhetarCode.text = computerCode
                binding.tvSabhasadName.text = accountName
                binding.tvSabhasadCode.text = accountId
                binding.tvLaamRopan.text = lamRopan
                binding.tvSankad.text = sankal
                binding.tvSherdiType.text = caneType
                binding.tvNetWeight.text = netWeight
                binding.tvCumulativeWeight.text = commulativeWeight
                binding.tvVehicleNumber.text = vehicleNumber
                binding.tvVehicleType.text = transportSadhan
                binding.tvDriverName.text = driverName
                binding.tvMukadamNumber.text = mukadamCode
                binding.tvKhetarInTime.text = dateFormatDynamic(farmIn, dateTimeApi, dateTimeDisplay)
                binding.tvKhetarOutTime.text = dateFormatDynamic(farmOut, dateTimeApi, dateTimeDisplay)
                binding.tvCuttingCompleteDate.text = dateFormatDynamic(cuttingCloseDate, dateApi, dateDisplay)


                if(!TextUtils.isEmpty(cuttingSupervisorId)) {
                    binding.tvKapniSupervisorNameNumber.text = "$cuttingSupervisorId / $cuttingSupervisorName"
                } else {
                    binding.tvKapniSupervisorNameNumber.text = "NA"
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

