package com.narmada.measure.utils

import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.narmada.measure.R

/**
 * Created by Dipti Agravat on 03,August,2023
 */
object DNDUtil {

    fun isPermissionAllowed(context: Context) : Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun openDNDSettingScreen(context: Context){
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        context.startActivity(intent)
    }

    fun isDNDEnabled(context: Context) : Boolean {
        if(!isPermissionAllowed(context)){ return false }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE
    }

    fun enableDNDMode(context: Context) {
        if(!isPermissionAllowed(context)){ return }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_NONE) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            Toast.makeText(context, context.getString(R.string.dnd_started), Toast.LENGTH_SHORT).show()
        }
    }

    fun disableDNDMode(context: Context) {
        if(!isPermissionAllowed(context)){ return }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            Toast.makeText(context, context.getString(R.string.dnd_stopped), Toast.LENGTH_SHORT).show()
        }
    }

    fun showDNDPermissionDialog(context: Context, settingClicked: () -> Unit, continueClicked: () -> Unit) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_dnd_permission)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val btnGotoSettings = dialog.findViewById(R.id.btn_goto_settings) as RelativeLayout
        val btnContinue = dialog.findViewById(R.id.btn_continue_without_dnd) as TextView
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        btnGotoSettings.setOnClickListener {
            dialog.dismiss()
            settingClicked.invoke()
        }

        btnContinue.setOnClickListener {
            dialog.dismiss()
            continueClicked.invoke()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showAskDNDDialog(context: Context, enableClicked: () -> Unit, disableClicked: () -> Unit) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_ask_dnd)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val btnEnableDND = dialog.findViewById(R.id.btn_enable_dnd) as RelativeLayout
        val btnDisableDND = dialog.findViewById(R.id.btn_disable_dnd) as TextView
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        btnEnableDND.setOnClickListener {
            dialog.dismiss()
            enableClicked.invoke()
        }

        btnDisableDND.setOnClickListener {
            dialog.dismiss()
            disableClicked.invoke()
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}