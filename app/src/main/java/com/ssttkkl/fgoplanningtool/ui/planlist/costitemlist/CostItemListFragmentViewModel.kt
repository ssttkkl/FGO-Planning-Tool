package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.groupedCostItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class CostItemListFragmentViewModel : ViewModel() {
    private val originData = MutableLiveData<List<CostItem>>()

    val itemClickable = MutableLiveData<Boolean>()

    val expandedItem = MutableLiveData<String>()

    val hideEnoughItems = MutableLiveData<Boolean>()

    val dataToShow = object : LiveData<List<CostItem>>() {
        val generator = {
            if (hideEnoughItems.value == true)
                originData.value?.filter { it.own < it.require }
            else
                originData.value
        }

        init {
            hideEnoughItems.observeForever { value = generator() }
            originData.observeForever { value = generator() }
        }
    }

    val itemTypes: LiveData<List<ItemType>> = Transformations.map(dataToShow) { dataToShow ->
        dataToShow?.mapNotNull { it.descriptor?.type }?.toSet()?.sortedBy { it } ?: listOf()
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

    @Synchronized
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

    @Synchronized
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

    @Synchronized

    fun onClickItem(codename: String) {
        expandedItem.value = if (codename == expandedItem.value) null else codename
    }

    fun onClickServant(servantID: Int) {
        showServantInfoEvent.call(servantID)
    }

    fun onClickJumpItem(itemType: ItemType) {
        scrollToPositionEvent.call(dataToShow.value?.indexOfFirst { it.descriptor?.type == itemType }
                ?: return)
    }
}