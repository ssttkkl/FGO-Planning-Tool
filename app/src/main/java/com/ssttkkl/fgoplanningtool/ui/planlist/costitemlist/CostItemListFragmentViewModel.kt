package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.groupedCostItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import java.util.*

class CostItemListFragmentViewModel : ViewModel() {
    private val originData = MutableLiveData<Map<String, CostItem>>()

    val itemClickable = MutableLiveData<Boolean>()

    private val expandedItemCodename = MutableLiveData<String>().apply {
        value = NO_ITEM
    }

    val requirements = MediatorLiveData<List<RequirementListEntity>>().apply {
        val observer = Observer<Any> { _ ->
            val newValue = originData.value?.get(expandedItemCodename.value)?.requirements?.sortedBy { it.servantID }
            if (newValue != null) // if newValue is null, remain the old value to show animations
                value = newValue
        }
        addSource(originData, observer)
        addSource(expandedItemCodename, observer)
    }

    val hideEnoughItems = MutableLiveData<Boolean>()

    val withEventItems = MutableLiveData<Boolean>()

    private val indexedEventItems: LiveData<Map<String, Item>> = Transformations.map(Repo.EventRepo.allAsLiveData) { events ->
        val map = HashMap<String, Long>()
        events.values.forEach { event ->
            event.items.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
        }
        map.mapValues { Item(it.key, it.value) }
    }

    // item can be Header or CostItem or REQUIREMENTS_PLACE_HOLDER
    val dataToShow = MediatorLiveData<List<Any>>().apply {
        val generator = {
            var items = originData.value?.values ?: listOf()
            if (withEventItems.value == true)
                items = items.map { item ->
                    CostItem(item.codename, item.require,
                            item.own + (indexedEventItems.value?.get(item.codename)?.count ?: 0L),
                            item.requirements)
                }
            if (hideEnoughItems.value == true)
                items = items.filter { it.own < it.require }
            items = items.sorted()

            val list = ArrayList<Any>()
            items.indices.forEach { idx ->
                // if this item and the perv item have different itemType, add a header
                if (idx == 0 || items[idx].descriptor?.type != items[idx - 1].descriptor?.type) {
                    val type = items[idx].descriptor?.type
                    if (type != null)
                        list.add(Header(type, idx != 0)) // the first header shouldn't show a divider
                }

                list.add(items[idx])

                if (expandedItemCodename.value == items[idx].codename)
                    list.add(REQUIREMENTS_PLACE_HOLDER)
            }

            list
        }
        addSource(expandedItemCodename) { value = generator() }
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

    val jumpExpanded = MutableLiveData<Boolean>()

    fun setDataFromPlans(plans: Collection<Plan>) {
        originData.value = plans.groupedCostItems.mapValues { (codename, requirements) ->
            CostItem(codename,
                    requirements.values.sum(),
                    Repo.ItemRepo[codename].count,
                    requirements.map { (servantID, cntOfReq) ->
                        RequirementListEntity(servantID, cntOfReq)
                    })
        }
        itemClickable.value = true
    }

    fun setDataFromItems(items: Collection<Item>) {
        originData.value = items.associate { (codename, count) ->
            codename to CostItem(codename, count, Repo.ItemRepo[codename].count, listOf())
        }
        itemClickable.value = false
    }

    val scrollToPositionEvent = SingleLiveEvent<Int>()

    fun onClickExpandJump() {
        jumpExpanded.value = jumpExpanded.value != true
    }

    fun onClickItem(codename: String) {
        expandedItemCodename.value = if (codename == expandedItemCodename.value)
            NO_ITEM
        else
            codename
    }

    fun onClickJumpItem(itemType: ItemType) {
        scrollToPositionEvent.call(dataToShow.value?.indexOfFirst { (it as? Header)?.itemType == itemType }
                ?: return)
        jumpExpanded.value = false
    }

    companion object {
        private const val NO_ITEM = ""
        const val REQUIREMENTS_PLACE_HOLDER = "requirements"
    }
}