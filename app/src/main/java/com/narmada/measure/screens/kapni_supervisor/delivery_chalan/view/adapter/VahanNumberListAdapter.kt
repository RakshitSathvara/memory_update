package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.databinding.ItemSearchVehicleNumberBinding
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.VahanNumber

class VahanNumberListAdapter(
    private val dataList: MutableList<VahanNumber>,
    private val onItemClickListener: ((VahanNumber) -> Unit)
) :
    RecyclerView.Adapter<VahanNumberListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchVehicleNumberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchVehicleNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(dataList[position]) {

                binding.textAutoComplete.text = transportName

                binding.textAutoComplete.setOnClickListener {
                    onItemClickListener.invoke(this)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}

