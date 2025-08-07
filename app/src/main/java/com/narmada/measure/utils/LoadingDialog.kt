package com.narmada.measure.utils

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.narmada.measure.R


class LoadingDialog internal constructor(context: Context) : CustomDialog(context) {

    private lateinit var loadingTv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_loading)
        loadingTv = findViewById(R.id.loading_tv)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val params = window?.attributes!!
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.CENTER
        window?.attributes = params

        setCancelable(false)
    }

//    fun setLoadingMessage(loadingMSG: String) {
//        if (!this::loadingTv.isInitialized) {
//            loadingTv = findViewById(R.id.loading_tv)
//        }
//        loadingTv.text = loadingMSG
//    }

    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        super.setOnCancelListener(listener)
        loadingTv.text = context.getString(R.string.please_wait)
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        loadingTv.text = context.getString(R.string.please_wait)
    }
}