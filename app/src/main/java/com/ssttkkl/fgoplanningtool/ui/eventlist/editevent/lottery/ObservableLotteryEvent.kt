package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.LotteryEventDescriptor
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.CheckableItem
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.ObservableEvent

class ObservableLotteryEvent(_event: LotteryEvent) : ObservableEvent {
    override val codename: String = _event.codename
    override val checkedShopItems = MutableLiveData<Collection<CheckableItem>>()
    override val rerunAndParticipated = MutableLiveData<Boolean>()
    val boxCount = MutableLiveData<Int>()

    init {
        rerunAndParticipated.value = _event.rerunAndParticipated
        boxCount.value = _event.boxCount
        checkedShopItems.value = _event.descriptor?.shopItems
                ?.map { item ->
                    val checkedItem = _event.checkedShopItems.firstOrNull { it.codename == item.codename }
                    CheckableItem(item,
                            checkedItem != null,
                            checkedItem?.count ?: item.count)
                }
    }

    override val originEvent = object : LiveData<LotteryEvent>() {
        val generator = {
            LotteryEvent(codename,
                    rerunAndParticipated.value == true,
                    checkedShopItems.value
                            ?.filter { it.checked && it.count > 0 }
                            ?.map { Item(it.item.codename, it.count) }
                            ?: listOf(),
                    boxCount.value ?: 0)
        }

        init {
            checkedShopItems.observeForever { value = generator() }
            rerunAndParticipated.observeForever { value = generator() }
            boxCount.observeForever { value = generator() }
        }
    }

    override val descriptor: LotteryEventDescriptor?
        get() = ResourcesProvider.instance.eventDescriptors[codename] as? LotteryEventDescriptor
}