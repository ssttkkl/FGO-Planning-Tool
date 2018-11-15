package com.ssttkkl.fgoplanningtool.ui.preferences.updaterespack

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.net.ResPackDownloader
import com.ssttkkl.fgoplanningtool.net.ResPackLatestInfo

class UpdateResPackViewModel : ViewModel() {
    val updater = ResPackDownloader(MyApp.context)

    var status = MutableLiveData<Status>()

    var latestInfo = MutableLiveData<ResPackLatestInfo>()

    var releaseDate = MutableLiveData<String>()

    var content = MutableLiveData<String>()

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