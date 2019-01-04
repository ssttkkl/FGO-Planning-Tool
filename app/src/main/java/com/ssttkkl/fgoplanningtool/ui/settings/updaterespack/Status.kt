package com.ssttkkl.fgoplanningtool.ui.settings.updaterespack

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class Status {
    LoadingLatestInfo,
    CompleteLoadingLatestInfo,
    FailedOnLoadingLatestInfo,
    Downloading,
    FailedOnDownloading,
    Updating,
    CompleteUpdating,
    FailedOnUpdating;

    val summary: String
        get() = when (this) {
            LoadingLatestInfo -> MyApp.context.getString(R.string.loading)
            CompleteLoadingLatestInfo -> MyApp.context.getString(R.string.loadSuccessfully)
            FailedOnLoadingLatestInfo -> MyApp.context.getString(R.string.loadFailed)
            Downloading -> MyApp.context.getString(R.string.downloading)
            FailedOnDownloading -> MyApp.context.getString(R.string.downloadFailed)
            Updating -> MyApp.context.getString(R.string.updating)
            CompleteUpdating -> MyApp.context.getString(R.string.updateSuccessfully)
            FailedOnUpdating -> MyApp.context.getString(R.string.updateFailed)
        }
}
