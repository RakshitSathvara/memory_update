package com.narmada.measure.multi_select_spinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.R
import java.util.*
import kotlin.collections.ArrayList

class MultiSelectAdapter<T>(private var itemList: ArrayList<T>, val selectedItems: ArrayList<T>,val getDisplayName: (input: T) -> String, val onSelectionChange: () -> Unit) : RecyclerView.Adapter<MultiSelectAdapter<T>.CountryViewHolder>(), Filterable {

    var filterItemList = ArrayList<T>()

    init {
        filterItemList = itemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectAdapter<T>.CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.multi_select_items_view, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val item = filterItemList[position]

        holder.textView.text = getDisplayName(item)

        if(selectedItems.contains(item)) {
            holder.imageView.setImageResource(R.drawable.ic_check_selected)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_check_default)
        }

        holder.mainLayout.setOnClickListener {
            if(selectedItems.contains(item)){
                selectedItems.remove(item)
            } else {
                selectedItems.add(item)
            }
            notifyDataSetChanged()
            onSelectionChange()
        }
    }

    override fun getItemCount(): Int {
        return filterItemList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterItemList = itemList
                } else {
                    val resultList = ArrayList<T>()
                    for (row in itemList) {
                        if (row.toString().lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    filterItemList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterItemList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterItemList = results?.values as ArrayList<T>
                notifyDataSetChanged()
            }
        }
    }

    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var textView: TextView = itemView.findViewById(R.id.text1)
        var imageView: ImageView = itemView.findViewById(R.id.checkbox)
        var mainLayout: LinearLayout = itemView.findViewById(R.id.main_layout)
    }

}