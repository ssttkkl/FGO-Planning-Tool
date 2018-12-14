package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.groupedCostItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import java.util.*

class CostItemListFragmentViewModel : ViewModel() {
    private val originData = MutableLiveData<List<CostItem>>()

    val itemClickable = MutableLiveData<Boolean>()

    val expandedItemCodename = MutableLiveData<String>().apply {
        value = NO_ITEM
    }

    val hideEnoughItems = MutableLiveData<Boolean>()

    val withEventItems = MutableLiveData<Boolean>()

    val indexedEventItems: LiveData<Map<String, Item>> = Transformations.map(Repo.EventRepo.allAsLiveData) { events ->
        val map = HashMap<String, Long>()
        events.values.forEach { event ->
            event.items.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
        }
        map.mapValues { Item(it.key, it.value) }
    }

    // item can be Header or CostItem
    val dataToShow = MediatorLiveData<List<Any>>().apply {
        val generator = {
            val items = if (hideEnoughItems.value == true)
                originData.value?.filter { it.own < it.require }
            else
                originData.value

            if (items == null)
                listOf<Any>()
            else {
                val list = ArrayList<Any>()
                items.indices.forEach { idx ->
                    // if this item and the perv item have different itemType, add a header
                    if (idx == 0 || items[idx].descriptor?.type != items[idx - 1].descriptor?.type) {
                        val type = items[idx].descriptor?.type
                        if (type != null)
                            list.add(Header(type, idx != 0)) // the first header shouldn't show a divider
                    }
                    if (withEventItems.value == true) {
                        val item = items[idx]
                        list.add(CostItem(item.codename,
                                item.require,
                                item.own + (indexedEventItems.value?.get(item.codename)?.count
                                        ?: 0L),
                                item.requirements))
                    } else
                        list.add(items[idx])
                }
                list
            }
        }
        addSource(hideEnoughItems) { value = generator() }
        addSource(withEventItems) { value = generator() }
        addSource(originData) { value = generator() }
        addSource(indexedEventItems) { value = generator() }
    }

    val itemTypes: LiveData<List<ItemType>> = Transformations.map(dataToShow) { dataToShow ->
        dataToShow?.mapNotNull { (it as? Header)?.itemType }
                ?.toSet()
                ?.sortedBy { it }
                ?: listOf()
    }

    val showEmptyHint: LiveData<Boolean> = Transformations.map(dataToShow) {
        it.isNullOrEmpty()
    }

    private fun processCostItems(costItems: Collection<CostItem>): List<CostItem> {
        return costItems.groupBy { it.descriptor?.type }
                .map { (type, items) -> Pair(type, items.sortedBy { item -> item.descriptor?.rank }) }
                .sortedBy { (type, _) -> type }
                .flatMap { (_, items) -> items }
    }

    fun setDataFromPlans(plans: Collection<Plan>) {
        originData.value = processCostItems(plans.groupedCostItems.map { (codename, requirements) ->
            CostItem(codename,
                    requirements.values.sum(),
                    Repo.ItemRepo.get(codename).count,
                    requirements.map { (servantID, cntOfReq) ->
                        RequirementListEntity(servantID, cntOfReq)
                    })
        })
        itemClickable.value = true
    }

    fun setDataFromItems(items: Collection<Item>) {
        originData.value = processCostItems(items.map { (codename, count) ->
            CostItem(codename, count, Repo.ItemRepo.get(codename).count, listOf())
        })
        itemClickable.value = false
    }

    val showServantInfoEvent = SingleLiveEvent<Int>()
    val scrollToPositionEvent = SingleLiveEvent<Int>()

    fun onClickItem(codename: String) {
        expandedItemCodename.value = if (codename == expandedItemCodename.value)
            NO_ITEM
        else
            codename
    }

    fun onClickServant(servantID: Int) {
        showServantInfoEvent.call(servantID)
    }

    fun onClickJumpItem(itemType: ItemType) {
        scrollToPositionEvent.call(dataToShow.value?.indexOfFirst { (it as? Header)?.itemType == itemType }
                ?: return)
    }

    companion object {
        private const val NO_ITEM = ""
    }
}