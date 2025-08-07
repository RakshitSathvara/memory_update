package com.narmada.measure.multi_select_spinner

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.narmada.measure.R

/**
 * Created by Md Farhan Raja on 2/23/2017.
 */
class MultiSelectSpinnerDialog<T> {

    var context: Activity
    var dTitle: String
    var closeTitle = "Close"

    var alertDialog: AlertDialog? = null

    private var isCancellable = false
    private var isShowKeyboard = false

    var items: ArrayList<T>
    private val selectedItems = arrayListOf<T>()
    private var getDisplayName: (input: T) -> String
    private var onSave: (selectedItems: ArrayList<T>) -> Unit

    constructor(activity: Activity, items: ArrayList<T>, selectedItems: ArrayList<T>, dialogTitle: String, closeTitle: String, getDisplayName: (input: T) -> String, onSave: (selectedItems: ArrayList<T>) -> Unit) {
        context = activity

        this.items = items
        this.selectedItems.addAll(selectedItems)

        this.getDisplayName = getDisplayName
        this.onSave = onSave
        dTitle = dialogTitle
        this.closeTitle = closeTitle
    }


    fun showSpinerDialog() {
        val adb = AlertDialog.Builder(context)
        val v = context.layoutInflater.inflate(R.layout.dialog_multi_select_layout, null)
        val rippleViewClose = v.findViewById<View>(R.id.close) as TextView
        val rippleViewSave = v.findViewById<View>(R.id.save) as TextView
        val title = v.findViewById<View>(R.id.spinerTitle) as TextView
        val layoutSelectAll = v.findViewById<View>(R.id.layout_select_all) as LinearLayout
        val imgSelectAll = v.findViewById<View>(R.id.checkbox_select_all) as ImageView

        if(items.size == selectedItems.size) {
            imgSelectAll.setImageResource(R.drawable.ic_check_selected)
        } else {
            imgSelectAll.setImageResource(R.drawable.ic_check_default)
        }
        rippleViewClose.text = closeTitle
        title.text = dTitle

        val listView = v.findViewById<View>(R.id.list) as RecyclerView
        val searchBox = v.findViewById<View>(R.id.searchBox) as EditText
        if (isShowKeyboard) {
            showKeyboard(searchBox)
        }

        listView.isVerticalScrollBarEnabled = true
        listView.layoutManager = LinearLayoutManager(context)
        val adapter: MultiSelectAdapter<T> = MultiSelectAdapter(items, selectedItems, getDisplayName) {
            if(items.size == selectedItems.size) {
                imgSelectAll.setImageResource(R.drawable.ic_check_selected)
            } else {
                imgSelectAll.setImageResource(R.drawable.ic_check_default)
            }
        }
        listView.adapter = adapter
        adb.setView(v)

        alertDialog = adb.create()

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                adapter.filter.filter(searchBox.text.toString())
            }
        })

        layoutSelectAll.setOnClickListener {
            if(items.size != selectedItems.size) {
                imgSelectAll.setImageResource(R.drawable.ic_check_selected)
                selectedItems.clear()
                selectedItems.addAll(items)
            } else {
                imgSelectAll.setImageResource(R.drawable.ic_check_default)
                selectedItems.clear()
            }
            adapter.notifyDataSetChanged()
        }

        rippleViewSave.setOnClickListener {
            onSave(selectedItems)
        }

        rippleViewClose.setOnClickListener { closeSpinerDialog() }

        alertDialog?.setCancelable(isCancellable)
        alertDialog?.setCanceledOnTouchOutside(isCancellable)
        alertDialog?.show()

    }

    fun closeSpinerDialog() {
        hideKeyboard()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }

    private fun hideKeyboard() {
        try {
            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(context.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
        }
    }

    private fun showKeyboard(ettext: EditText) {
        ettext.requestFocus()
        ettext.postDelayed({
            val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.showSoftInput(ettext, 0)
        }, 200)
    }


    private fun isCancellable(): Boolean { return isCancellable }

    fun setCancellable(cancellable: Boolean) { isCancellable = cancellable }

    private fun isShowKeyboard(): Boolean { return isShowKeyboard }

    fun setShowKeyboard(showKeyboard: Boolean) { isShowKeyboard = showKeyboard }

}