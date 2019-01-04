package com.ssttkkl.fgoplanningtool.ui.planlist.confirmchangeplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class ConfirmChangePlanFragmentViewModel : ViewModel() {
    val plans = MutableLiveData<Collection<ConfirmablePlan>>()
    val mode: LiveData<Mode> = Transformations.map(plans) { plans ->
        if (plans.all { it.mode == Mode.Remove })
            Mode.Remove
        else
            Mode.Change
    }
    val title: LiveData<String> = Transformations.map(mode) { mode ->
        if (mode == Mode.Remove)
            MyApp.context.getString(R.string.confirmRemovePlan)
        else
            MyApp.context.getString(R.string.confirmChangePlan)
    }

    private val indexedDeductItems = MutableLiveData<Map<String, DeductItem>>().apply {
        val generator = {
            plans.value?.map { it.delta }?.costItems?.associate { item ->
                item.codename to
                        DeductItem(item.codename, item.count,
                                Repo.ItemRepo.allAsLiveData.value?.get(item.codename)?.count ?: 0,
                                value?.get(item.codename)?.checked == true)
            } ?: mapOf()
        }
        plans.observeForever { value = generator() }
        Repo.ItemRepo.allAsLiveData.observeForever { value = generator() }
    }

    val deductItems: LiveData<List<DeductItem>> = Transformations.map(indexedDeductItems) { indexedDeductItems ->
        indexedDeductItems.values
                .groupBy { it.descriptor?.type }
                .map { (type, items) -> Pair(type, items.sortedBy { it.descriptor?.rank }) }
                .sortedBy { (type, _) -> type }
                .flatMap { (_, items) -> items }
    }

    val showEmptyHint: LiveData<Boolean> = Transformations.map(deductItems) { deductItems ->
        deductItems.isNullOrEmpty()
    }

    val finishEvent = SingleLiveEvent<Void>()

    fun onClickItem(codename: String) {
        synchronized(indexedDeductItems) {
            indexedDeductItems.value = indexedDeductItems.value?.toMutableMap()?.apply {
                val oldItem = this[codename] ?: return
                this[codename] = DeductItem(codename, oldItem.require, oldItem.own, !oldItem.checked)
            }
        }
    }

    fun onClickSelectAll() {
        synchronized(indexedDeductItems) {
            val alreadySelectAll = deductItems.value?.filter { it.delta >= 0 }?.all { it.checked } == true
            indexedDeductItems.value = indexedDeductItems.value?.mapValues { (_, it) ->
                DeductItem(it.codename, it.require, it.own, it.delta >= 0 && !alreadySelectAll)
            }
        }
    }

    fun onClickYes() {
        plans.value?.forEach {
            when (it.mode) {
                Mode.Remove -> Repo.PlanRepo.remove(it.old.servantId)
                Mode.Change -> Repo.PlanRepo.insert(it.new!!)
            }
        }
        Repo.ItemRepo.deduct(deductItems.value?.filter { it.checked }?.map { Item(it.codename, it.require) }
                ?: listOf())
        finishEvent.call()
    }
}