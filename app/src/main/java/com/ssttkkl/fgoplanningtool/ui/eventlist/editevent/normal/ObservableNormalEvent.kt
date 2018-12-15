package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.normal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.NormalEventDescriptor
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.CheckableItem
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.ObservableEvent

class ObservableNormalEvent(_event: NormalEvent) : ObservableEvent {
    override val codename: String = _event.codename
    override val checkedShopItems = MutableLiveData<Collection<CheckableItem>>()
    override val rerunAndParticipated = MutableLiveData<Boolean>()

    init {
        rerunAndParticipated.value = _event.rerunAndParticipated
        checkedShopItems.value = _event.descriptor?.shopItems
                ?.map { item ->
                    val checkedItem = _event.checkedShopItems.firstOrNull { it.codename == item.codename }
                    CheckableItem(item,
                            checkedItem != null,
                            checkedItem?.count ?: item.count)
                }
    }

    override val originEvent = object : LiveData<NormalEvent>() {
        val generator = {
            NormalEvent(codename,
                    rerunAndParticipated.value == true,
                    checkedShopItems.value
                            ?.filter { it.checked && it.count > 0 }
                            ?.map { Item(it.item.codename, it.count) }
                            ?: listOf())
        }

        init {
            checkedShopItems.observeForever { value = generator() }
            rerunAndParticipated.observeForever { value = generator() }
        }
    }

    override val descriptor: NormalEventDescriptor?
        get() = ResourcesProvider.instance.eventDescriptors[codename] as? NormalEventDescriptor
}