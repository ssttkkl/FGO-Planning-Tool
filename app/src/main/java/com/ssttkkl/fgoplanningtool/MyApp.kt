package com.ssttkkl.fgoplanningtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var context: Context
            private set

        val versionName
            get() = context.packageManager.getPackageInfo(context.packageName, 0).versionName

        val versionCode
            get() = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    }
}