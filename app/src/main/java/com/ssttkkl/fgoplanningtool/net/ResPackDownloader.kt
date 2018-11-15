package com.ssttkkl.fgoplanningtool.net

import android.content.Context
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.request.DownloadRequest
import com.google.gson.Gson
import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.util.*

class ResPackDownloader(val context: Context) {
    interface Callback {
        fun onStartLoadingLatestInfo()
        fun onFailOnLoadingLatestInfo(message: String)
        fun onCompleteLoadingLatestInfo(latestInfo: ResPackLatestInfo)
        fun onDownloadProgress(progress: Int, downloadRequest: DownloadRequest)
        fun onCompleteDownloading()
        fun onFailOnDownloading(message: String)
        fun onCompleteUpdating()
        fun onFailOnUpdating(message: String)
    }

    private var callback: Callback? = null

    fun setCallback(newCallback: Callback) {
        callback = newCallback
    }

    private lateinit var latestInfo: ResPackLatestInfo

    private lateinit var downloadRequest: DownloadRequest

    private val work = GlobalScope.launch(Dispatchers.Default, CoroutineStart.LAZY) {
        GlobalScope.launch(Main) { callback?.onStartLoadingLatestInfo() }
        val latestInfoFile = File(this@ResPackDownloader.context.cacheDir, "${UUID.randomUUID()}.json").apply { deleteOnExit() }
        try {
            FileUtils.copyURLToFile(URL(ConstantLinks.urlPattern.format(ConstantLinks.resPackLatestInfoFilename)), latestInfoFile)
            latestInfo = Gson().fromJson<ResPackLatestInfo>(latestInfoFile.readText(), ResPackLatestInfo::class.java)
        } catch (exc: Exception) {
            GlobalScope.launch(Main) { callback?.onFailOnLoadingLatestInfo(exc.localizedMessage) }
            return@launch
        }
        GlobalScope.launch(Main) { callback?.onCompleteLoadingLatestInfo(latestInfo) }

        val resPackFile = File(this@ResPackDownloader.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
        downloadRequest = PRDownloader.download(latestInfo.downloadLink, resPackFile.parent, resPackFile.name).build()

        var lastProgress = -1
        downloadRequest.setOnProgressListener {
            val newProgress = (it.currentBytes.toDouble() / it.totalBytes.toDouble() * 100).toInt()
            if (lastProgress != newProgress)
                GlobalScope.launch(Main) { callback?.onDownloadProgress(newProgress, downloadRequest) }
            lastProgress = newProgress
        }

        downloadRequest.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                callback?.onCompleteDownloading()
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        ResourcesUpdater.update(resPackFile)
                        launch(Main) { callback?.onCompleteUpdating() }
                    } catch (exc: Exception) {
                        launch(Main) { callback?.onFailOnUpdating(exc.toString()) }
                    }
                }
            }

            override fun onError(error: Error) {
                val sb = StringBuilder()
                if (error.isConnectionError)
                    sb.append("ConnectionError")
                if (error.isServerError)
                    sb.append("ServerError")
                callback?.onFailOnDownloading(sb.toString())
            }
        })
    }

    fun start(): Boolean = work.start()

    fun cancel() {
        work.cancel()
        if (::downloadRequest.isInitialized)
            downloadRequest.cancel()
    }
}