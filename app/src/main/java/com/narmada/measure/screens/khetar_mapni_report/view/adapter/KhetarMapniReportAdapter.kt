package com.narmada.measure.screens.khetar_mapni_report.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.R
import com.narmada.measure.screens.khetar_mapni_report.model.KhetarMapniReport


class KhetarMapniReportAdapter(
    private val context: Context,
    private val dataList: MutableList<KhetarMapniReport>
) :
    RecyclerView.Adapter<KhetarMapniReportAdapter.ViewHolder>() {

    var typefaceGuj: Typeface? = null
    var typefaceEng: Typeface? = null
    init {
        typefaceGuj = ResourcesCompat.getFont(context, R.font.shree_guj)
        typefaceEng = ResourcesCompat.getFont(context, R.font.rasa_regular)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvVillage: TextView = itemView.findViewById(R.id.tv_village)
        var tvTotalArea: TextView = itemView.findViewById(R.id.tv_total_area)
        var tvCount: TextView = itemView.findViewById(R.id.tv_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_khetar_mapni_report, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = dataList[position]

        if(data.villageName.toString().equals("Total", ignoreCase = true)) {
            holder.tvVillage.typeface = typefaceEng
        } else {
            holder.tvVillage.typeface = typefaceGuj
        }

        holder.tvVillage.text = data.villageName.toString()
        holder.tvTotalArea.text = data.ropanArea.toString()
        holder.tvCount.text = data.count.toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}

