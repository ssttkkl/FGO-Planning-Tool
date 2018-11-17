package com.ssttkkl.fgoplanningtool.ui.updaterespack

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
            LoadingLatestInfo -> MyApp.context.getString(R.string.status_loading_updaterespack)
            CompleteLoadingLatestInfo -> MyApp.context.getString(R.string.status_loadSuccessful_updaterespack)
            FailedOnLoadingLatestInfo -> MyApp.context.getString(R.string.status_loadFailed_updaterespack)
            Downloading -> MyApp.context.getString(R.string.status_downloading_updaterespack)
            FailedOnDownloading -> MyApp.context.getString(R.string.status_downloadFailed_updaterespack)
            Updating -> MyApp.context.getString(R.string.status_updating_updaterespack)
            CompleteUpdating -> MyApp.context.getString(R.string.status_updateSuccessful_updaterespack)
            FailedOnUpdating -> MyApp.context.getString(R.string.status_updateFailed_updaterespack)
        }
}
