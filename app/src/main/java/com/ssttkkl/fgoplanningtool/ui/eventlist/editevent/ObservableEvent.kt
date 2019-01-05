package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.databinding.ObservableArrayMap
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
    val lotteryBoxCount = ObservableArrayMap<String, Int>()
    val point = ObservableArrayMap<String, Long>()

    init {
        checkedShopItems.value = _event.descriptor?.shopItems
                ?.map { item ->
                    val checkedItem = _event.checkedShopItems.firstOrNull { it.codename == item.codename }
                    CheckableItem(item,
                            checkedItem != null,
                            checkedItem?.count ?: item.count)
                }
        rerunAndParticipated.value = _event.rerunAndParticipated
        lotteryBoxCount.putAll(_event.lotteryBoxCount)
        point.putAll(_event.point)
    }

    val originEvent = object : LiveData<Event>() {
        val generator = {
            Event(codename,
                    rerunAndParticipated.value == true,
                    checkedShopItems.value
                            ?.filter { it.checked && it.count > 0 }
                            ?.map { Item(it.item.codename, it.count) }
                            ?: listOf(),
                    lotteryBoxCount.toMap(),
                    point.toMap())
        }

        init {
            checkedShopItems.observeForever { value = generator() }
            rerunAndParticipated.observeForever { value = generator() }
            lotteryBoxCount.addOnMapChangedCallback(object : ObservableMap.OnMapChangedCallback<ObservableArrayMap<String, Int>, String, Int>() {
                override fun onMapChanged(sender: ObservableArrayMap<String, Int>?, key: String?) {
                    value = generator()
                }
            })
            point.addOnMapChangedCallback(object : ObservableMap.OnMapChangedCallback<ObservableArrayMap<String, Long>, String, Long>() {
                override fun onMapChanged(sender: ObservableArrayMap<String, Long>?, key: String?) {
                    value = generator()
                }
            })
        }
    }

    val descriptor: EventDescriptor?
        get() = ResourcesProvider.instance.eventDescriptors[codename]

    val pointItems: LiveData<Map<String, List<PointItem>>> = Transformations.map(originEvent) { event ->
        event.descriptor?.pointPools?.mapValues { (codename, pool) ->
            pool.items.flatMap { (requestPoint, items) ->
                items.map {
                    PointItem(it, requestPoint, (event.point[codename] ?: 0) >= requestPoint)
                }.sortedBy { it.item.descriptor?.rank }
            }
        } ?: mapOf()
    }
}