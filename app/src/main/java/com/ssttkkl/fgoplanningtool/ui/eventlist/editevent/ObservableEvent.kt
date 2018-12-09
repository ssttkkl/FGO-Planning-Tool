package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor

interface ObservableEvent {
    val codename: MutableLiveData<String>
    val checkedShopItems: MutableLiveData<Collection<CheckableItem>>
    val rerunAndParticipated: MutableLiveData<Boolean>

    val originEvent: LiveData<out Event>
}