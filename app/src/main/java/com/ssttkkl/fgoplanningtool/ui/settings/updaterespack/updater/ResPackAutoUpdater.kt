package com.ssttkkl.fgoplanningtool.ui.settings.updaterespack.updater

import android.content.Context
import com.google.gson.Gson
import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.util.*

class ResPackAutoUpdater(val context: Context) : FetchListener {
    interface Callback {
        fun onStartLoadingLatestInfo()
        fun onFailOnLoadingLatestInfo(message: String)
        fun onCompleteLoadingLatestInfo(latestInfo: ResPackLatestInfo)
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

    private lateinit var latestInfo: ResPackLatestInfo

    private lateinit var resPackFile: File

    private val work = GlobalScope.launch(Dispatchers.Default, CoroutineStart.LAZY) {
        launch(Main) { callback?.onStartLoadingLatestInfo() }
        val latestInfoFile = File(this@ResPackAutoUpdater.context.cacheDir, "${UUID.randomUUID()}.json").apply { deleteOnExit() }
        try {
            FileUtils.copyURLToFile(URL(ConstantLinks.urlPattern.format(ConstantLinks.resPackLatestInfoFilename)), latestInfoFile)
            latestInfo = Gson().fromJson<ResPackLatestInfo>(latestInfoFile.readText(), ResPackLatestInfo::class.java)
        } catch (exc: Exception) {
            launch(Main) { callback?.onFailOnLoadingLatestInfo(exc.localizedMessage) }
            return@launch
        }
        launch(Main) { callback?.onCompleteLoadingLatestInfo(latestInfo) }

        resPackFile = File(this@ResPackAutoUpdater.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
        fetch.addListener(this@ResPackAutoUpdater)
        val request = Request(latestInfo.downloadLink, resPackFile.path)
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
}