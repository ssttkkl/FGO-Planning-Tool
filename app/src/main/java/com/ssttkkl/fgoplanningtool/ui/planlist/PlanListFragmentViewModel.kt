package com.ssttkkl.fgoplanningtool.ui.planlist

import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
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

    private fun reverseChecked(servantID: Int) {
        data.value = data.value?.toMutableList()?.apply {
            val idx = indexOfFirst { it.plan.servantId == servantID }
            this[idx] = CheckablePlan(this[idx].plan, !this[idx].checked)
        }
    }

    private fun selectAll(selected: Boolean) {
        data.value = data.value?.toMutableList()?.apply {
            for (idx in indices) {
                if (this[idx].checked != selected)
                    this[idx] = CheckablePlan(this[idx].plan, selected)
            }
        }
    }

    val showEmptyHint: LiveData<Boolean> = Transformations.map(data) { data ->
        data.isNullOrEmpty()
    }

    val isDefaultState = MutableLiveData<Boolean>()

    val inSelectMode = object : MutableLiveData<Boolean>() {
        override fun setValue(value: Boolean?) {
            val old = this.value
            if (old != value) {
                super.setValue(value)
                selectAll(false)
            }
        }
    }

    val title = object : LiveData<String>() {
        init {
            val generator = {
                if (inSelectMode.value == true)
                    MyApp.context.getString(R.string.selectedCount_planlist,
                            data.value?.count { it.checked } ?: 0,
                            data.value?.size ?: 0)
                else
                    MyApp.context.getString(R.string.title_planlist)
            }
            data.observeForever { value = generator() }
            inSelectMode.observeForever { value = generator() }
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

    fun onFilter(filtered: List<Servant>, isDefaultState: Boolean) {
        this.isDefaultState.value = isDefaultState

        val servantIDs = filtered.map { it.id }
        val selectedServantIDs = data.value?.filter { it.checked }?.map { it.plan.servantId }?.toSet()
                ?: setOf()
        data.value = Repo.planRepo.all
                .filter { servantIDs.contains(it.servantId) }
                .sortedBy { servantIDs.indexOf(it.servantId) }
                .map { CheckablePlan(it, selectedServantIDs.contains(it.servantId)) }
    }

    fun getCostItems(servant: Servant) = Repo.planRepo[servant.id]?.costItems ?: listOf()
}