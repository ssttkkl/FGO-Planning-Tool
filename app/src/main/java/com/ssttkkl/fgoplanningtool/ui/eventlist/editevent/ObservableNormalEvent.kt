package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.data.item.Item

class ObservableNormalEvent(_event: NormalEvent) : ObservableEvent {
    override val codename = MutableLiveData<String>()
    override val checkedShopItems = MutableLiveData<Collection<CheckableItem>>()
    override val rerunAndParticipated = MutableLiveData<Boolean>()

    init {
        codename.value = _event.codename
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
            val codename = codename.value
            if (codename != null)
                NormalEvent(codename,
                        rerunAndParticipated.value == true,
                        checkedShopItems.value
                                ?.filter { it.checked && it.count > 0 }
                                ?.map { Item(it.item.codename, it.count) }
                                ?: listOf())
            else
                null
        }

        init {
            codename.observeForever { value = generator() }
            checkedShopItems.observeForever { value = generator() }
            rerunAndParticipated.observeForever { value = generator() }
        }
    }
}