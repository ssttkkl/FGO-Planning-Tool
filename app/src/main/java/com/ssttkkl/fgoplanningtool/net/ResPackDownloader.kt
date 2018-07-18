package com.ssttkkl.fgoplanningtool.net

import android.content.Context
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.request.DownloadRequest
import com.google.gson.Gson
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.ui.updaterespack.UpdateResPackDialogFragment
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.util.*

class ResPackAutoUpdater(val context: Context) {
    interface Callback {
        fun onLoadLatestInfoError(message: String)
        fun onLoadLatestInfoComplete(latestInfo: LatestInfo)
        fun onDownloadProgress(progress: Int)
        fun onDownloadComplete(file: File)
        fun onDownloadError(message: String)
    }

    private var callback: Callback? = null

    fun setCallback(newCallback: Callback) {
        callback = newCallback
    }

    private lateinit var latestInfo: LatestInfo

    private lateinit var downloadRequest: DownloadRequest

    private val work = launch(Dispatchers.net, CoroutineStart.LAZY) {
        val latestInfoFile = File(this@ResPackAutoUpdater.context.cacheDir, "${UUID.randomUUID()}.json").apply { deleteOnExit() }
        try {
            FileUtils.copyURLToFile(URL(UpdateResPackDialogFragment.urlPattern.format(UpdateResPackDialogFragment.latestInfoFilename)), latestInfoFile)
            latestInfo = Gson().fromJson<LatestInfo>(latestInfoFile.readText(), LatestInfo::class.java)
        } catch (exc: Exception) {
            launch(UI) { callback?.onLoadLatestInfoError(exc.localizedMessage) }
            return@launch
        }
        launch(UI) { callback?.onLoadLatestInfoComplete(latestInfo) }

        val resPackFile = File(this@ResPackAutoUpdater.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
        downloadRequest = PRDownloader.download(latestInfo.downloadLink, resPackFile.parent, resPackFile.name).build()
                .setOnProgressListener {
                    launch(UI) { callback?.onDownloadProgress((it.currentBytes.toDouble() / it.totalBytes.toDouble() * 100).toInt()) }
                }
        downloadRequest.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                callback?.onDownloadComplete(resPackFile)
            }

            override fun onError(error: Error) {
                val sb = StringBuilder()
                if (error.isConnectionError)
                    sb.append("ConnectionError")
                if (error.isServerError)
                    sb.append("ServerError")
                callback?.onDownloadError(sb.toString())
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