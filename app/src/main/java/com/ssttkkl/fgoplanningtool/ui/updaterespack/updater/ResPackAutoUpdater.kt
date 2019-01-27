package com.ssttkkl.fgoplanningtool.ui.updaterespack.updater

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import com.ssttkkl.fgoplanningtool.utils.ConstantLinks
import com.ssttkkl.fgoplanningtool.utils.ResPackUpdateInfo
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.util.*

class ResPackAutoUpdater(val context: Context) : FetchListener {
    interface Callback {
        fun onStartLoadingLatestInfo()
        fun onFailOnLoadingLatestInfo(message: String)
        fun onCompleteLoadingLatestInfo(updateInfo: ResPackUpdateInfo)
        fun onDownloadProgress(progress: Int, totalBytes: Long)
        fun onCompleteDownloading()
        fun onFailOnDownloading(message: String)
        fun onCompleteUpdating()
        fun onFailOnUpdating(message: String)
    }

    private var callback: Callback? = null

    fun setCallback(newCallback: Callback) {
        callback = newCallback
    }

    private val fetch = Fetch.getInstance(FetchConfiguration.Builder(context)
            .enableHashCheck(true)
            .enableRetryOnNetworkGain(true)
            .build())

    private lateinit var updateInfo: ResPackUpdateInfo

    private lateinit var resPackFile: File

    private val work = GlobalScope.launch(Dispatchers.Default, CoroutineStart.LAZY) {
        launch(Main) { callback?.onStartLoadingLatestInfo() }
        try {
            val url = URL(ConstantLinks.urlPattern.format(ConstantLinks.resPackLatestInfoFilename))
            updateInfo = gson.fromJson<ResPackUpdateInfo>(url.readText(), ResPackUpdateInfo::class.java)
            Log.d("CheckUpdate", updateInfo.toString())
        } catch (exc: Exception) {
            launch(Main) { callback?.onFailOnLoadingLatestInfo(exc.localizedMessage) }
            return@launch
        }
        launch(Main) { callback?.onCompleteLoadingLatestInfo(updateInfo) }

        resPackFile = File(this@ResPackAutoUpdater.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
        fetch.addListener(this@ResPackAutoUpdater)
        val request = Request(updateInfo.downloadLink, resPackFile.path)
        fetch.enqueue(request, null, Func { error ->
            callback?.onFailOnDownloading(error.name)
        })
    }

    fun start(): Boolean = work.start()

    fun cancel() {
        work.cancel()
        fetch.cancelAll()
    }

    override fun onCompleted(download: Download) {
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

    override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
        callback?.onDownloadProgress(download.progress, download.total)
    }

    override fun onError(download: Download, error: com.tonyodev.fetch2.Error, throwable: Throwable?) {
        callback?.onFailOnDownloading(error.name)
    }

    override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
        callback?.onDownloadProgress(0, download.total)
    }

    override fun onAdded(download: Download) {}

    override fun onCancelled(download: Download) {}

    override fun onDeleted(download: Download) {}

    override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}

    override fun onPaused(download: Download) {}

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}

    override fun onRemoved(download: Download) {}

    override fun onResumed(download: Download) {}

    override fun onWaitingNetwork(download: Download) {}

    companion object {
        private val gson = GsonBuilder().registerTypeAdapter(ResPackUpdateInfo::class.java, ResPackUpdateInfo.GsonTypeAdapter()).create()
    }
}