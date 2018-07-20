package com.ssttkkl.fgoplanningtool.ui.updaterespack

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.net.ResPackLatestInfo
import com.ssttkkl.fgoplanningtool.net.ResPackDownloader

class UpdateResPackViewModel : ViewModel() {
    val updater = ResPackDownloader(MyApp.context)

    var status = MutableLiveData<Status>()

    var latestInfo = MutableLiveData<ResPackLatestInfo>()

    var size = MutableLiveData<Long>().apply {
        value = 0
    }

    var progress = MutableLiveData<Int>().apply {
        value = 0
    }

    override fun onCleared() {
        super.onCleared()
        updater.cancel()
        Log.d("UpdateResPack", "Canceled")
    }
}