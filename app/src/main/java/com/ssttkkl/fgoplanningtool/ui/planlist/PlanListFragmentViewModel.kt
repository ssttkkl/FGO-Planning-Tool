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

    private val originData get() = Repo.PlanRepo.allAsLiveData

    private val observer = Observer<Map<Int, Plan>> {
        changeOriginEvent.call(it?.mapNotNull { (_, plan) -> plan.servant })
    }

    init {
        originData.observeForever(observer)
    }

    override fun onCleared() {
        originData.removeObserver(observer)
    }

    val data = MutableLiveData<List<CheckablePlan>>()

    val showEmptyHint: LiveData<Boolean> = Transformations.map(data) { data ->
        data.isNullOrEmpty()
    }

    val isDefaultState = MutableLiveData<Boolean>()

    val inSelectMode = MutableLiveData<Boolean>().apply {
        observeForever {
            selectAll(false)
        }
    }

    val title = MediatorLiveData<String>().apply {
        val generator = {
            if (inSelectMode.value == true)
                MyApp.context.getString(R.string.selectedCount_planlist,
                        data.value?.count { it.checked } ?: 0,
                        data.value?.size ?: 0)
            else
                MyApp.context.getString(R.string.title_planlist)
        }
        addSource(data) { value = generator() }
        addSource(inSelectMode) { value = generator() }
    }

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
        val data = if (inSelectMode.value == true)
            data.value?.filter { it.checked }?.map { it.plan }
        else
            data.value?.map { it.plan }
        calcResultEvent.call(data)
        inSelectMode.value = false
    }

    fun onBackPressed(): Boolean {
        return if (inSelectMode.value == true) {
            inSelectMode.value = false
            true
        } else false
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
        data.value = originData.value?.values
                ?.filter { servantIDs.contains(it.servantId) }
                ?.sortedBy { servantIDs.indexOf(it.servantId) }
                ?.map { CheckablePlan(it, selectedServantIDs.contains(it.servantId)) }
    }

    fun getCostItems(servant: Servant) = originData.value?.get(servant.id)?.costItems ?: listOf()
}