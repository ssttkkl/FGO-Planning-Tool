package com.ssttkkl.fgoplanningtool.ui.eventlist.chooseevent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class ChooseEventFragmentViewModel : ViewModel() {
    // key: year
    // value: a list of events
    val events = MutableLiveData<Map<Int, List<EventDescriptor>>>().apply {
        value = ResourcesProvider.instance.eventDescriptors.values
                .sortedBy { it.date }
                .groupBy { it.date.year }
    }

    val hiddenEventCodenames: LiveData<Set<String>> = Transformations.map(Repo.EventRepo.allAsLiveData) { events ->
        events.keys
    }

    val gotoEditEventUIEvent = SingleLiveEvent<Event>()

    fun onClickEvent(descriptor: EventDescriptor) {
        gotoEditEventUIEvent.call(Event(descriptor.codename))
    }
}