package com.narmada.measure.screens.kapni_supervisor.chalan_history.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.databinding.ItemSerdiDeliveryHistoryBinding
import com.narmada.measure.screens.kapni_supervisor.chalan_history.model.DeliveryChalan
import java.text.SimpleDateFormat
import java.util.Locale

class DeliveryChalanListAdapter(
    private val dataList: MutableList<DeliveryChalan>,
) :
    RecyclerView.Adapter<DeliveryChalanListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSerdiDeliveryHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSerdiDeliveryHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(dataList[position]) {

                binding.tvReceiptNo.text = docNo
                binding.tvRasidNo.text = rasidNumber
                binding.tvReceiptDate.text = dateFormatDynamic(docDate, dateApi, dateDisplay)
                binding.tvKhetarCode.text = computerCode
                binding.tvSabhasadName.text = accountName
                binding.tvSabhasadCode.text = accountId
                binding.tvLaamRopan.text = lamRopan
                binding.tvSankad.text = sankal
                binding.tvSherdiType.text = caneType
                binding.tvVehicleNumber.text = transportName
                binding.tvVehicleType.text = transportSadhan
                binding.tvVehicleNumberOne.text = if (transportNameOne!!.isEmpty()) {
                    "-"
                } else {
                    transportNameOne
                }

                binding.tvVehicleTypeOne.text = if (transportSadhanOne!!.isEmpty()) {
                    "-"
                } else {
                    transportSadhanOne
                }
                binding.tvDriverName.text = driverName
                binding.tvMukadamNumber.text = "$mukadamId / $mukadamName"
                binding.tvKhetarInTime.text =
                    dateFormatDynamic(farmIn, dateTimeApi, dateTimeDisplay)
                binding.tvKhetarOutTime.text =
                    dateFormatDynamic(farmOut, dateTimeApi, dateTimeDisplay)
                binding.tvCuttingCompleteDate.text =
                    dateFormatDynamic(cuttingCloseDate, dateApi, dateDisplay)

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

