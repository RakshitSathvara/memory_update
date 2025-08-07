package com.narmada.measure.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

object SharedPreferenceUtil {
    private var editor: Editor? = null
    private var sharedPreferences: SharedPreferences? = null

    val all: Map<String, *>
        get() = sharedPreferences!!.all

    @SuppressLint("CommitPrefEdits")
    fun init(mcontext: Context) {
        if (sharedPreferences == null) {
            sharedPreferences =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(mcontext)
            editor = sharedPreferences!!.edit()
            editor!!.apply()
        }
    }

    fun putValue(key: String, value: String) {
        editor!!.putString(key, value)
    }

    fun putValue(key: String, value: Int) {
        editor!!.putInt(key, value)
    }

    fun putValue(key: String, value: Long) {
        editor!!.putLong(key, value)
    }

    fun putValue(key: String, value: Boolean) {
        editor!!.putBoolean(key, value)
    }

    fun save() {
        editor!!.commit()
    }

    fun getString(key: String, defValue: String): String? {
        return sharedPreferences!!.getString(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences!!.getInt(key, defValue)
    }

    fun getLong(key: String, defValue: Long): Long {
        return sharedPreferences!!.getLong(key, defValue)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(key, defValue)
    }

    operator fun contains(key: String): Boolean {
        return sharedPreferences!!.contains(key)
    }

    fun remove(key: String) {
        editor!!.remove(key)
        editor!!.commit()
    }

    fun clear() {
        editor!!.clear()
    }
}
