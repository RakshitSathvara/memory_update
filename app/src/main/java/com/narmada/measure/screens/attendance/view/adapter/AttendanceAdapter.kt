package com.narmada.measure.screens.attendance.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.R
import com.narmada.measure.screens.attendance.model.AttendanceListItem

class AttendanceAdapter(
    private val dataList: MutableList<AttendanceListItem>
) :
    RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDate: TextView = itemView.findViewById(R.id.tv_date)
        var tvIn: TextView = itemView.findViewById(R.id.tv_in)
        var tvOut: TextView = itemView.findViewById(R.id.tv_out)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDate.text = dataList[position].date.toString()
        holder.tvIn.text = dataList[position].inTime.toString()
        holder.tvOut.text = dataList[position].outTime.toString()

    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}

