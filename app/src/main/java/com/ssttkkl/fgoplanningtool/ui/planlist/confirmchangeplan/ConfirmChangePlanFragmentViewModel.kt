package com.ssttkkl.fgoplanningtool.ui.planlist.confirmchangeplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
            MyApp.context.getString(R.string.confirmRemove)
        else
            MyApp.context.getString(R.string.confirmChange)
    }
    val hint = MediatorLiveData<String>().apply {
        val generator = {
            when (mode.value) {
                Mode.Remove -> MyApp.context.getString(R.string.hintOfConfirmRemovePlan, plans.value?.size)
                Mode.Change -> MyApp.context.getString(R.string.hintOfConfirmChangePlan, plans.value?.size)
                else -> ""
            }
        }
        addSource(mode) { value = generator() }
        addSource(plans) { value = generator() }
    }

    private val indexedItems = MutableLiveData<Map<String, DeductItem>>().apply {
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

    val itemsToDeduct: LiveData<List<DeductItem>> = Transformations.map(indexedItems) { indexedItems ->
        indexedItems.values
                .groupBy { it.descriptor?.type }
                .map { (type, items) -> Pair(type, items.sortedBy { it.descriptor?.rank }) }
                .sortedBy { (type, _) -> type }
                .flatMap { (_, items) -> items }
    }

    val deductItems = MutableLiveData<Boolean>().apply {
        observeForever {
            if (it == true)
                indexedItems.value = indexedItems.value?.mapValues { (_, item) ->
                    DeductItem(item.codename, item.require, item.own, item.require <= item.own)
                }
        }
    }

    val showDeductItems: LiveData<Boolean> = Transformations.map(itemsToDeduct) {
        !it.isNullOrEmpty()
    }

    val finishEvent = SingleLiveEvent<Void>()

    fun onClickItem(codename: String) {
        synchronized(indexedItems) {
            indexedItems.value = indexedItems.value?.toMutableMap()?.apply {
                val oldItem = this[codename] ?: return
                this[codename] = DeductItem(codename, oldItem.require, oldItem.own, !oldItem.checked)
            }
        }
    }

    fun onClickSelectAll() {
        synchronized(indexedItems) {
            val alreadySelectAll = itemsToDeduct.value?.filter { it.delta >= 0 }?.all { it.checked } == true
            indexedItems.value = indexedItems.value?.mapValues { (_, it) ->
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

        if (deductItems.value == true)
            Repo.ItemRepo.deduct(itemsToDeduct.value?.filter { it.checked }?.map { Item(it.codename, it.require) }
                    ?: listOf())
        finishEvent.call()
    }
}