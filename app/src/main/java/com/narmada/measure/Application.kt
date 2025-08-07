package com.narmada.measure

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.narmada.measure.utils.SharedPreferenceUtil

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        SharedPreferenceUtil.init(this)
        mInstance = this

        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
    }

    companion object {
        private var mInstance: Application? = null

        val instance: Application?
            @Synchronized get() {
                var appSingleton: Application?
                synchronized(Application::class.java)
                {
                    appSingleton = mInstance
                }
                return appSingleton
            }
    }

}