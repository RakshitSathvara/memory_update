package com.narmada.measure.screens.sabhasad_mahiti_update.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.R
import com.narmada.measure.databinding.ItemUpdateSabhasadRowBinding
import com.narmada.measure.screens.sabhasad_mahiti_update.model.LinkedMember

class SabhasadListAdapter(
    private val dataList: MutableList<LinkedMember>
) :
    RecyclerView.Adapter<SabhasadListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUpdateSabhasadRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpdateSabhasadRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            binding.tvSabhasadCode.text = dataList[position].farmerId
            binding.tvSabhasadName.text = dataList[position].farmerName

            if (dataList[position].isSelected) {
                binding.checkbox.setImageResource(R.drawable.checkbox_selected)
            } else {
                binding.checkbox.setImageResource(R.drawable.checkbox_default)
            }

            binding.checkbox.setOnClickListener {
                dataList[position].isSelected = !dataList[position].isSelected
                notifyItemChanged(position)
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}

