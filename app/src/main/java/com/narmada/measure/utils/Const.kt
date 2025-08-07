package com.narmada.measure.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.pm.PackageInfoCompat
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.Serializable
import java.text.SimpleDateFormat

object Const {

    fun showSnackBar(activity: Activity, message: String) {
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

    fun showToast(activity: Activity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializableExtra(key, m_class)!!
        else
            this.getSerializableExtra(key) as T
    }

    @SuppressLint("SimpleDateFormat")
    fun dateValidation(startDate: String, endDate: String, dateFormat: String): Boolean {
        return try {
            val df = SimpleDateFormat(dateFormat)
            val date1 = df.parse(endDate)
            val startingDate = df.parse(startDate)

            date1!!.after(startingDate) || startDate == endDate
        } catch (e: Exception) {
            false
        }

    }

    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return result
    }

    fun noInternetConnection(context: Context) {
        try {
            val alert = AlertDialog.Builder(context)
            alert.setTitle("No Internet Connection!")
            alert.setMessage("You are offline please check your internet connection")
            alert.setPositiveButton("OK", null)
            alert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRBofText(text: String): RequestBody {
        Log.e(TAG, "getRBofText: $text")
        return text.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun closeKeyboard(activity: Activity) {
        try {
            val view = activity.currentFocus
            if (view != null) {
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
            // Ignore exceptions if any
            Log.e("KeyBoardUtil", e.toString(), e)
        }
    }

//    fun showExpireDialog(activity: Activity) {
//        val dialog = Dialog(activity, R.style.MyDialogTheme)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        @Suppress("DEPRECATION")
//        dialog.window!!.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        (dialog.window)!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.dialog_expire_session)
//
//        dialog.Tv_yes.setOnClickListener {
//            dialog.dismiss()
//            SharedPreferenceUtil.clear()
//            SharedPreferenceUtil.save()
//            activity.finish()
//            activity.startActivity(Intent(activity, LoginActivity::class.java))
//        }
//
//        dialog.show()
//    }
}

