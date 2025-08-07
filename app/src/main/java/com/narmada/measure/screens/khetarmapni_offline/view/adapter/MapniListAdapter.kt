package com.narmada.measure.screens.khetarmapni_offline.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.R
import com.narmada.measure.room.entity.OfflineMapni

class MapniListAdapter(
    private val dataList: MutableList<OfflineMapni>,
    private val listener: onClick
) :
    RecyclerView.Adapter<MapniListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvKhetarCode: TextView = itemView.findViewById(R.id.tvKhetar)
        var tvSabhaCode: TextView = itemView.findViewById(R.id.tvSabha)
        var ivDelete: LinearLayout = itemView.findViewById(R.id.linearDelete)
        var ivEdit: LinearLayout = itemView.findViewById(R.id.linearEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mapni_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvKhetarCode.text = dataList[position].khetarCode.toString()
        holder.tvSabhaCode.text = dataList[position].sabhaCode.toString()
        holder.ivDelete.setOnClickListener {
            listener.onDeleteClick(position,dataList[position])
        }
        holder.ivEdit.setOnClickListener {
            listener.onEditClick(position,dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun removeItem(index: Int) {
        dataList.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, dataList.size)
    }

    interface onClick {
        fun onDeleteClick(position: Int, offlineMapni: OfflineMapni)
        fun onEditClick(position: Int, offlineMapni: OfflineMapni)
    }

}

