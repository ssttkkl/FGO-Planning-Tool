package com.ssttkkl.fgoplanningtool.ui.eventlist.chooseevent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.LotteryEventDescriptor
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.NormalEventDescriptor
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import java.lang.Exception
import java.util.*

class ChooseEventFragmentViewModel : ViewModel() {
    // key: year
    // value: a list of events
    val events = MutableLiveData<Map<Int, List<EventDescriptor>>>().apply {
        value = ResourcesProvider.instance.eventDescriptors.values
                .sortedBy { it.date }
                .groupBy { it.date.year }
    }

    val gotoEditNormalEventUIEvent = SingleLiveEvent<NormalEvent>()
    val gotoEditLotteryEventUIEvent = SingleLiveEvent<LotteryEvent>()

    fun onClickEvent(descriptor: EventDescriptor) {
        when (descriptor) {
            is NormalEventDescriptor -> gotoEditNormalEventUIEvent.call(NormalEvent(descriptor.codename))
            is LotteryEventDescriptor -> gotoEditLotteryEventUIEvent.call(LotteryEvent(descriptor.codename))
        }
    }
}