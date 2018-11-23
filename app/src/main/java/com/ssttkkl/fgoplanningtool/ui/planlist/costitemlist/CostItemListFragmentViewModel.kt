package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.groupedCostItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class CostItemListFragmentViewModel : ViewModel() {
    val data = MutableLiveData<List<CostItem>>()

    val showEmptyHint: LiveData<Boolean> = Transformations.map(data) { data ->
        data.isNullOrEmpty()
    }

    val itemClickable = MutableLiveData<Boolean>()

    val expandedItem = MutableLiveData<String>()

    private fun processCostItems(costItems: Collection<CostItem>): List<CostItem> {
        return costItems.groupBy { it.descriptor?.type }
                .map { (type, items) -> Pair(type, items.sortedBy { item -> item.descriptor?.rank }) }
                .sortedBy { (type, _) -> type }
                .flatMap { (_, items) -> items }
    }

    fun setDataFromPlans(plans: Collection<Plan>) {
        data.value = processCostItems(plans.groupedCostItems.map { (codename, requirements) ->
            CostItem(codename,
                    requirements.values.sum(),
                    Repo.itemRepo[codename].count,
                    requirements.map { (servantID, cntOfReq) ->
                        RequirementListEntity(servantID,
                                ResourcesProvider.instance.servants[servantID]?.localizedName ?: "",
                                cntOfReq,
                                ResourcesProvider.instance.servants[servantID]?.avatarFile)
                    })
        })
        itemClickable.value = true
    }

    fun setDataFromItems(items: Collection<Item>) {
        data.value = processCostItems(items.map { (codename, count) ->
            CostItem(codename,
                    count,
                    Repo.itemRepo[codename].count,
                    listOf())
        })
        itemClickable.value = false
    }

    val showServantInfoEvent = SingleLiveEvent<Int>()

    fun onClickItem(codename: String) {
        expandedItem.value = if (codename == expandedItem.value) null else codename
    }

    fun onClickServant(servantID: Int) {
        showServantInfoEvent.call(servantID)
    }
}