package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
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

    val expandedItem = MutableLiveData<String>()

    val hideEnoughItems = MutableLiveData<Boolean>()

    // item can be Header or CostItem
    val dataToShow = object : LiveData<List<Any>>() {
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
                    list.add(items[idx])
                }
                list
            }
        }

        init {
            hideEnoughItems.observeForever { value = generator() }
            originData.observeForever { value = generator() }
        }
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
                    Repo.itemRepo[codename].count,
                    requirements.map { (servantID, cntOfReq) ->
                        RequirementListEntity(servantID, cntOfReq)
                    })
        })
        itemClickable.value = true
    }

    fun setDataFromItems(items: Collection<Item>) {
        originData.value = processCostItems(items.map { (codename, count) ->
            CostItem(codename,
                    count,
                    Repo.itemRepo[codename].count,
                    listOf())
        })
        itemClickable.value = false
    }

    val showServantInfoEvent = SingleLiveEvent<Int>()
    val scrollToPositionEvent = SingleLiveEvent<Int>()

    fun onClickItem(codename: String) {
        expandedItem.value = if (codename == expandedItem.value) null else codename
    }

    fun onClickServant(servantID: Int) {
        showServantInfoEvent.call(servantID)
    }

    fun onClickJumpItem(itemType: ItemType) {
        scrollToPositionEvent.call(dataToShow.value?.indexOfFirst { (it as? ItemType) == itemType }
                ?: return)
    }
}