package com.ssttkkl.fgoplanningtool.ui.eventlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class EventListFragmentViewModel : ViewModel() {
    private val originData get() = Repo.EventRepo.allAsLiveData

    val data: LiveData<List<Event>> = Transformations.map(originData) { originData ->
        originData?.values?.sortedBy { it.descriptor?.date }
    }

    val showEmptyHint: LiveData<Boolean> = Transformations.map(data) { data ->
        data.isNullOrEmpty()
    }

    val gotoEditEventUIEvent = SingleLiveEvent<Event>()
    val gotoChooseEventUIEvent = SingleLiveEvent<Unit>()

    fun onClickEvent(codename: String) {
        gotoEditEventUIEvent.call(originData.value?.get(codename))
    }

    fun onClickAdd() {
        gotoChooseEventUIEvent.call()
    }
}