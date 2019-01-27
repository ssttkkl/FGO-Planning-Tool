package com.ssttkkl.fgoplanningtool.ui.informupdate.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.utils.AppUpdateInfo
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import org.joda.time.format.DateTimeFormatterBuilder

class InformAppUpdateViewModel : ViewModel() {
    val appUpdateInfo = MutableLiveData<AppUpdateInfo>()

    val gotoDownloadPageEvent = SingleLiveEvent<String>()
    val finishEvent = SingleLiveEvent<Unit>()

    fun onClickLater() {
        finishEvent.call()
    }

    fun onClickDownload() {
        gotoDownloadPageEvent.call(appUpdateInfo.value?.downloadLink)
        finishEvent.call()
    }

    companion object {
        @JvmStatic
        val dateTimeFormatter = DateTimeFormatterBuilder().appendYear(4, 4)
                .appendLiteral('/')
                .appendMonthOfYear(2)
                .appendLiteral('/')
                .appendDayOfMonth(2)
                .toFormatter()
    }
}