package com.ssttkkl.fgoplanningtool.ui.planlist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class PlanListFragmentViewModel : ViewModel() {
    val addPlanEvent = SingleLiveEvent<Void>()
    val editPlanEvent = SingleLiveEvent<Plan>()
    val calcResultEvent = SingleLiveEvent<Collection<Plan>>()
    val removePlansEvent = SingleLiveEvent<Collection<Plan>>()
    val changeOriginEvent = SingleLiveEvent<Collection<Servant>>()

    init {
        Repo.planListLiveData.observeForever {
            changeOriginEvent.call(it?.mapNotNull { plan -> plan.servant })
        }
    }

    val inSelectMode = object : MutableLiveData<Boolean>() {
        override fun setValue(value: Boolean?) {
            val old = this.value == true
            val new = value == true
            if (old != new)
                super.setValue(new)
        }
    }

    val data: MutableLiveData<List<Plan>> = object : MutableLiveData<List<Plan>>() {
        override fun setValue(value: List<Plan>?) {
            val old = this.value ?: listOf()
            val oldSelected = selection.value?.map { old[it] } ?: listOf()
            super.setValue(value)
            selection.value = oldSelected.filter { value?.contains(it) == true }.mapNotNull { value?.indexOf(it) }.toSet()
        }
    }

    val selection = MutableLiveData<Set<Int>>()

    fun onPlanClick(plan: Plan) {
        if (inSelectMode.value == true) {
            val idx = data.value?.indexOf(plan) ?: return
            if (selection.value?.contains(idx) == true)
                selection.value = selection.value?.minus(idx)
            else
                selection.value = selection.value?.plus(idx) ?: setOf()
        } else
            editPlanEvent.call(plan)
    }

    fun onPlanLongClick(plan: Plan): Boolean {
        return if (inSelectMode.value != true) {
            inSelectMode.value = true
            selection.value = setOf(data.value?.indexOf(plan) ?: return true)
            true
        } else false
    }

    fun onFabClick() {
        val plans = if (inSelectMode.value == true)
            selection.value?.mapNotNull { data.value?.get(it) }
        else
            data.value
        calcResultEvent.call(plans)
        inSelectMode.value = false
    }

    fun onBackPressed(): Boolean {
        return if (inSelectMode.value == true) {
            inSelectMode.value = false
            true
        } else false
    }

    fun onHomeClick() {
        inSelectMode.value = false
    }

    fun onEnterSelectModeClick() {
        inSelectMode.value = true
    }

    fun onAddClick() {
        addPlanEvent.call()
    }

    fun onSelectAllClick() {
        val all = (0 until (data.value?.size ?: 0)).toSet()
        selection.value = if (selection.value == all)
            setOf()
        else
            all
    }

    fun onRemoveClick() {
        val selected = selection.value?.mapNotNull { data.value?.get(it) }
        removePlansEvent.call(selected)
        inSelectMode.value = false
    }

    fun onRemoveWarningUIResult(plans: Collection<Plan>,
                                deductItems: Boolean) {
        remove(plans, if (deductItems)
            plans.costItems
        else
            null)
    }

    fun onFilter(filtered: List<Servant>) {
        val servantIDs = filtered.map { it.id }
        data.value = Repo.planRepo.all.asSequence()
                .sortedBy { servantIDs.indexOf(it.servantId) }
                .filter { servantIDs.contains(it.servantId) }
                .toList()
    }

    fun getPlanByServantID(servantID: Int) = Repo.planRepo[servantID]

    private fun remove(plans: Collection<Plan>, itemsToDeduct: Collection<Item>?) {
        Repo.planRepo.remove(plans.map { it.servantId }, HowToPerform.Launch)
        if (itemsToDeduct != null && itemsToDeduct.isNotEmpty())
            Repo.itemRepo.deductItems(itemsToDeduct, HowToPerform.Launch)
    }
}