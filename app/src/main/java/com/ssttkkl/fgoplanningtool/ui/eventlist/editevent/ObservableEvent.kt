package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableArrayMap
import androidx.databinding.ObservableList
import androidx.databinding.ObservableMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.point.PointItem
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.shop.CheckableItem

class ObservableEvent(_event: Event) {
    val codename: String = _event.codename
    val rerunAndParticipated = MutableLiveData<Boolean>()
    val checkedShopItems = MutableLiveData<Collection<CheckableItem>>()
    val lotteryBoxCount = ObservableArrayList<Int?>()
    val point = ObservableArrayList<Long?>()

    init {
        checkedShopItems.value = _event.descriptor?.shopItems
                ?.map { item ->
                    val checkedItem = _event.checkedShopItems.firstOrNull { it.codename == item.codename }
                    CheckableItem(item,
                            checkedItem != null,
                            checkedItem?.count ?: item.count)
                }
        rerunAndParticipated.value = _event.rerunAndParticipated
        lotteryBoxCount.addAll(_event.lotteryBoxCount)
        point.addAll(_event.point)
    }

    val originEvent = object : LiveData<Event>() {
        val generator = {
            Event(codename,
                    rerunAndParticipated.value == true,
                    checkedShopItems.value?.filter { it.checked && it.count > 0 }
                            ?.map { Item(it.item.codename, it.count) }
                            ?: listOf(),
                    lotteryBoxCount.map { it ?: 0 },
                    point.map { it ?: 0L })
        }

        init {
            checkedShopItems.observeForever { value = generator() }
            rerunAndParticipated.observeForever { value = generator() }
            lotteryBoxCount.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<Int?>>() {
                override fun onItemRangeRemoved(sender: ObservableArrayList<Int?>?, positionStart: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onItemRangeMoved(sender: ObservableArrayList<Int?>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onItemRangeInserted(sender: ObservableArrayList<Int?>?, positionStart: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onItemRangeChanged(sender: ObservableArrayList<Int?>?, positionStart: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onChanged(sender: ObservableArrayList<Int?>?) {
                    value = generator()
                }
            })
            point.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<Long?>>() {
                override fun onItemRangeRemoved(sender: ObservableArrayList<Long?>?, positionStart: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onItemRangeMoved(sender: ObservableArrayList<Long?>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onItemRangeInserted(sender: ObservableArrayList<Long?>?, positionStart: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onItemRangeChanged(sender: ObservableArrayList<Long?>?, positionStart: Int, itemCount: Int) {
                    value = generator()
                }

                override fun onChanged(sender: ObservableArrayList<Long?>?) {
                    value = generator()
                }
            })
        }
    }

    val descriptor: EventDescriptor?
        get() = ResourcesProvider.instance.eventDescriptors[codename]

    val pointItems: LiveData<List<List<PointItem>>> = Transformations.map(originEvent) { event ->
        event.descriptor?.pointPools?.mapIndexed { idx, pool ->
            pool.items.entries.sortedBy { it.key }.flatMap { (requestPoint, items) ->
                items.map {
                    PointItem(it, requestPoint, (event.point[idx]) >= requestPoint)
                }.sortedBy { it.item.descriptor?.rank }
            }
        } ?: listOf()
    }
}