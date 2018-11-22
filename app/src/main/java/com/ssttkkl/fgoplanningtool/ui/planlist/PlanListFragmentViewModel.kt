package com.ssttkkl.fgoplanningtool.ui.planlist

import androidx.lifecycle.*
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

    val data = MutableLiveData<List<CheckablePlan>>()

    val numOfSelected: LiveData<Int> = Transformations.map(data) { data ->
        data.count { it.checked }
    }

    private fun reverseChecked(servantID: Int) {
        synchronized(data) {
            val oldData = data.value ?: return
            val idx = oldData.indexOfFirst { it.plan.servantId == servantID }
            data.value = oldData.toMutableList().apply {
                this[idx] = CheckablePlan(this[idx].plan, !this[idx].checked)
            }
        }
    }

    private fun selectAll(selected: Boolean) {
        synchronized(data) {
            val oldData = data.value ?: return
            data.value = oldData.toMutableList().apply {
                for (idx in indices) {
                    if (this[idx].checked != selected)
                        this[idx] = CheckablePlan(this[idx].plan, selected)
                }
            }
        }
    }

    val inSelectMode = object : MutableLiveData<Boolean>() {
        override fun setValue(value: Boolean?) {
            val old = this.value
            if (old != value) {
                super.setValue(value)
                selectAll(false)
            }
        }
    }

    private val observer = Observer<List<Plan>> {
        changeOriginEvent.call(it?.mapNotNull { plan -> plan.servant })
    }

    init {
        Repo.planListLiveData.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        Repo.planListLiveData.removeObserver(observer)
    }

    fun onPlanClick(plan: Plan) {
        if (inSelectMode.value == true) {
            reverseChecked(plan.servantId)
        } else
            editPlanEvent.call(plan)
    }

    fun onPlanLongClick(plan: Plan): Boolean {
        return if (inSelectMode.value != true) {
            inSelectMode.value = true
            reverseChecked(plan.servantId)
            true
        } else false
    }

    fun onPlanAvatarClick(plan: Plan) {
        if (inSelectMode.value != true) {
            inSelectMode.value = true
        }
        reverseChecked(plan.servantId)
    }

    fun onFabClick() {
        val plans = if (inSelectMode.value == true)
            data.value?.filter { it.checked }?.map { it.plan }
        else
            data.value?.map { it.plan }
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

    fun onCheckAllClick() {
        selectAll(data.value?.all { it.checked } != true)
    }

    fun onRemoveClick() {
        removePlansEvent.call(data.value?.filter { it.checked }?.map { it.plan })
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
        val selectedServantIDs = data.value?.filter { it.checked }?.map { it.plan.servantId }?.toSet()
                ?: setOf<Int>()
        data.value = Repo.planRepo.all
                .filter { servantIDs.contains(it.servantId) }
                .sortedBy { servantIDs.indexOf(it.servantId) }
                .map { CheckablePlan(it, selectedServantIDs.contains(it.servantId)) }
    }

    fun getCostItems(servant: Servant) = Repo.planRepo[servant.id]?.costItems ?: listOf()

    private fun remove(plans: Collection<Plan>, itemsToDeduct: Collection<Item>?) {
        Repo.planRepo.remove(plans.map { it.servantId }, HowToPerform.Launch)
        if (itemsToDeduct != null && itemsToDeduct.isNotEmpty())
            Repo.itemRepo.deductItems(itemsToDeduct, HowToPerform.Launch)
    }
}