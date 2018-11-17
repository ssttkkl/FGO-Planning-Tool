package com.ssttkkl.fgoplanningtool.ui.updaterespack

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.updaterespack.updater.ResPackAutoUpdater
import com.ssttkkl.fgoplanningtool.ui.updaterespack.updater.ResPackLatestInfo
import com.ssttkkl.fgoplanningtool.ui.updaterespack.updater.ResPackManuallyUpdater
import java.io.File

class UpdateResPackViewModel : ViewModel() {
    private lateinit var autoUpdater: ResPackAutoUpdater
    private lateinit var manuallyUpdater: ResPackManuallyUpdater

    var manually: Boolean = false

    val status = MutableLiveData<Status>()
    val latestInfo = MutableLiveData<ResPackLatestInfo>()
    val releaseDate: LiveData<String> = Transformations.map(latestInfo) { latestInfo ->
        MyApp.context.getString(R.string.releaseDate_pattern_updaterespack,
                latestInfo.releaseDate / 10000,
                latestInfo.releaseDate % 10000 / 100,
                latestInfo.releaseDate % 100)
    }
    private val size = MutableLiveData<Long>().apply { value = 0 }
    val sizeSummary: LiveData<String> = Transformations.map(size) { size ->
        MyApp.context.getString(R.string.size_pattern_updaterespack, size.toDouble() / 1024 / 1024)
    }
    val progress = MutableLiveData<Int>().apply { value = 0 }
    val showProgress: LiveData<Boolean> = Transformations.map(status) { status ->
        when (status) {
            Status.LoadingLatestInfo -> true
            Status.CompleteLoadingLatestInfo -> true
            Status.FailedOnLoadingLatestInfo -> false
            Status.Downloading -> true
            Status.FailedOnDownloading -> false
            Status.Updating -> true
            Status.CompleteUpdating -> false
            Status.FailedOnUpdating -> false
            null -> false
        }
    }
    val showProgressIndeterminately: LiveData<Boolean> = Transformations.map(status) { status ->
        when (status) {
            Status.LoadingLatestInfo -> true
            Status.CompleteLoadingLatestInfo -> true
            Status.FailedOnLoadingLatestInfo -> false
            Status.Downloading -> false
            Status.FailedOnDownloading -> false
            Status.Updating -> true
            Status.CompleteUpdating -> false
            Status.FailedOnUpdating -> false
            null -> false
        }
    }
    val errorMessage = MutableLiveData<String>()

    fun start() {
        if (!::autoUpdater.isInitialized)
            autoUpdater = ResPackAutoUpdater(MyApp.context)
        autoUpdater.setCallback(object : ResPackAutoUpdater.Callback {
            override fun onStartLoadingLatestInfo() {
                status.value = Status.LoadingLatestInfo
                Log.d("UpdateResPack", "Start loading latestInfo.")
            }

            override fun onCompleteLoadingLatestInfo(latestInfo: ResPackLatestInfo) {
                status.value = Status.CompleteLoadingLatestInfo
                this@UpdateResPackViewModel.latestInfo.value = latestInfo
                Log.d("UpdateResPack", "LatestInfo: $latestInfo")
            }

            override fun onFailOnLoadingLatestInfo(message: String) {
                status.value = Status.FailedOnLoadingLatestInfo
                errorMessage.value = message
                Log.d("UpdateResPack", "Fail on loading latestInfo. $message")
            }

            override fun onDownloadProgress(progress: Int, totalBytes: Long) {
                status.value = Status.Downloading
                this@UpdateResPackViewModel.progress.value = progress
                size.value = totalBytes
                Log.d("UpdateResPack", "Download Progress: $progress (size: ${totalBytes.toDouble() / 1024 / 1024}MB)")
            }

            override fun onCompleteDownloading() {
                status.value = Status.Updating
                Log.d("UpdateResPack", "Complete downloading.")
            }

            override fun onFailOnDownloading(message: String) {
                status.value = Status.FailedOnDownloading
                errorMessage.value = message
                Log.d("UpdateResPack", "Fail on downloading. $message")
            }

            override fun onCompleteUpdating() {
                status.value = Status.CompleteUpdating
                Log.d("UpdateResPack", "Complete updating.")
            }

            override fun onFailOnUpdating(message: String) {
                status.value = Status.FailedOnUpdating
                errorMessage.value = message
                Log.d("UpdateResPack", "Fail on updating. $message")
            }
        })
        autoUpdater.start()
    }

    fun startManually(file: File) {
        if (!::manuallyUpdater.isInitialized)
            manuallyUpdater = ResPackManuallyUpdater(file)
        manuallyUpdater.setCallback(object : ResPackManuallyUpdater.Callback {
            override fun onStartUpdating() {
                status.value = Status.Updating
                size.value = file.length()
            }

            override fun onCompleteUpdating() {
                status.value = Status.CompleteUpdating
                latestInfo.value = ResPackLatestInfo(targetVersion = ResourcesProvider.TARGET_VERSION,
                        releaseDate = ResourcesProvider.instance.resPackInfo.releaseDate,
                        content = ResourcesProvider.instance.resPackInfo.content,
                        downloadLink = "")
            }

            override fun onFailOnUpdating(message: String) {
                status.value = Status.FailedOnUpdating
                errorMessage.value = message
            }
        })
        manuallyUpdater.start()
    }

    override fun onCleared() {
        super.onCleared()
        if (::autoUpdater.isInitialized)
            autoUpdater.cancel()
        if (::manuallyUpdater.isInitialized)
            manuallyUpdater.cancel()
        Log.d("UpdateResPack", "Canceled")
    }
}