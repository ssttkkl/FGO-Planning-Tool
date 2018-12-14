package com.ssttkkl.fgoplanningtool.ui.eventlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class EventListFragmentViewModel : ViewModel() {
    private val originData get() = Repo.EventRepo.allAsLiveData

    val data: LiveData<List<Event>> = Transformations.map(originData) { originData ->
        originData?.values?.sortedBy { it.descriptor?.date }
    }

    val showEmptyHint: LiveData<Boolean> = Transformations.map(data) { data ->
        data.isNullOrEmpty()
    }

    val gotoEditNormalEventUIEvent = SingleLiveEvent<NormalEvent>()
    val gotoEditLotteryEventUIEvent = SingleLiveEvent<LotteryEvent>()
    val gotoChooseEventUIEvent = SingleLiveEvent<Unit>()

    fun onClickEvent(codename: String) {
        val event = originData.value?.get(codename)
        when (event) {
            is NormalEvent -> gotoEditNormalEventUIEvent.call(event)
            is LotteryEvent -> gotoEditLotteryEventUIEvent.call(event)
        }
    }

    fun onClickAdd() {
        gotoChooseEventUIEvent.call()
    }
}