package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.item.Item

class ObservableLotteryEvent(_event: LotteryEvent) : ObservableEvent {
    override val codename = MutableLiveData<String>()
    override val checkedShopItems = MutableLiveData<Collection<CheckableItem>>()
    override val rerunAndParticipated = MutableLiveData<Boolean>()
    val boxCount = MutableLiveData<Int>()

    init {
        codename.value = _event.codename
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
            val codename = codename.value
            if (codename != null)
                LotteryEvent(codename,
                        rerunAndParticipated.value == true,
                        checkedShopItems.value
                                ?.filter { it.checked && it.count > 0 }
                                ?.map { Item(it.item.codename, it.count) }
                                ?: listOf(),
                        boxCount.value ?: 0)
            else
                null
        }

        init {
            codename.observeForever { value = generator() }
            checkedShopItems.observeForever { value = generator() }
            rerunAndParticipated.observeForever { value = generator() }
            boxCount.observeForever { value = generator() }
        }
    }
}