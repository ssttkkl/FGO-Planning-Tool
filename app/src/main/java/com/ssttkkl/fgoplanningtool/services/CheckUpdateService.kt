package com.ssttkkl.fgoplanningtool.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.gson.Gson
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.net.AppLatestInfo
import com.ssttkkl.fgoplanningtool.net.ConstantLinks
import com.ssttkkl.fgoplanningtool.net.ResPackLatestInfo
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.preferences.PreferencesActivity
import kotlinx.coroutines.experimental.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class CheckUpdateService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        launch(Dispatchers.net) {
            try {
                val latestInfoFile = File(cacheDir, "${UUID.randomUUID()}.json")

                URL(ConstantLinks.urlPattern.format(ConstantLinks.resPackLatestInfoFilename)).openConnection()
                FileUtils.copyURLToFile(URL(ConstantLinks.urlPattern.format(ConstantLinks.resPackLatestInfoFilename)), latestInfoFile)
                val resPackLatestInfo = Gson().fromJson<ResPackLatestInfo>(latestInfoFile.readText(), ResPackLatestInfo::class.java)
                Log.d("CheckUpdate", resPackLatestInfo.toString())
                if (resPackLatestInfo.releaseDate > ResourcesProvider.instance.resPackInfo.releaseDate)
                    notifyResPackUpdate(resPackLatestInfo)

                FileUtils.copyURLToFile(URL(ConstantLinks.urlPattern.format(ConstantLinks.appLatestInfoFilename)), latestInfoFile)
                val appLatestInfo = Gson().fromJson<AppLatestInfo>(latestInfoFile.readText(), AppLatestInfo::class.java)
                Log.d("CheckUpdate", appLatestInfo.toString())
                if (appLatestInfo.versionCode > MyApp.versionCode)
                    notifyAppUpdate(appLatestInfo)

                latestInfoFile.delete()
                stopSelf()
            } catch (exc: Exception) {
            }
        }
        return START_NOT_STICKY
    }

    private fun notifyResPackUpdate(latestInfo: ResPackLatestInfo) {
        val intent = Intent(this, PreferencesActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.resPackUpdateAvailable_checkupdate))
                .setContentText("${latestInfo.content}(${latestInfo.releaseDate})")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)
    }

    private fun notifyAppUpdate(latestInfo: AppLatestInfo) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(latestInfo.downloadLink)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.appUpdateAvailable_checkupdate))
                .setContentText("${latestInfo.versionName}(${latestInfo.versionCode})")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)
    }
}