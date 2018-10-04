package com.ssttkkl.fgoplanningtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.downloader.PRDownloader

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        PRDownloader.initialize(context)

        cacheDir.listFiles().forEach { it.deleteRecursively() }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var context: Context
            private set

        val versionName: String
            get() = context.packageManager.getPackageInfo(context.packageName, 0).versionName

        val versionCode
            get() = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    }
}