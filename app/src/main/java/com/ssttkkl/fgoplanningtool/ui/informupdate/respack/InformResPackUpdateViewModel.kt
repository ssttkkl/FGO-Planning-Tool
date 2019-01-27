package com.ssttkkl.fgoplanningtool.ui.informupdate.respack

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import com.ssttkkl.fgoplanningtool.utils.ResPackUpdateInfo
import org.joda.time.format.DateTimeFormatterBuilder

class InformResPackUpdateViewModel : ViewModel() {
    val resPackUpdateInfo = MutableLiveData<ResPackUpdateInfo>()

    val gotoAutoUpdateUIEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    fun onClickLater() {
        finishEvent.call()
    }

    fun onClickUpdate() {
        gotoAutoUpdateUIEvent.call()
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