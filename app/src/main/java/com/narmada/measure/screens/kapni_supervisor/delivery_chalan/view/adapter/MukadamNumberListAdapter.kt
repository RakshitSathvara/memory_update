package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.databinding.ItemSearchMukadamNumberBinding
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.MukadamNumber

class MukadamNumberListAdapter(
    private val dataList: MutableList<MukadamNumber>,
    private val onItemClickListener: ((MukadamNumber) -> Unit)
) :
    RecyclerView.Adapter<MukadamNumberListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchMukadamNumberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchMukadamNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(dataList[position]) {

                binding.textAutoComplete.text = "$mukadamID / $mukadamName"

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

