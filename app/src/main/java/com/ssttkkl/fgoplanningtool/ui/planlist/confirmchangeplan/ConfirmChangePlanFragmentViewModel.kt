package com.ssttkkl.fgoplanningtool.ui.planlist.confirmchangeplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.HowToPerform
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
            MyApp.context.getString(R.string.title_confirm_remove_plan)
        else
            MyApp.context.getString(R.string.title_confirm_change_plan)
    }

    private val indexedDeductItems = MutableLiveData<Map<String, DeductItem>>().apply {
        plans.observeForever { plans ->
            value = plans?.map { it.delta }?.costItems?.associate {
                Pair(it.codename, DeductItem(it.codename, it.count,
                        Repo.itemRepo[it.codename].count,
                        value?.get(it.codename)?.checked == true))
            } ?: mapOf()
        }
    }

    val deductItems: LiveData<List<DeductItem>> = Transformations.map(indexedDeductItems) { indexedDeductItems ->
        indexedDeductItems.values
                .groupBy { it.descriptor?.type }
                .map { (type, items) -> Pair(type, items.sortedBy { it.descriptor?.rank }) }
                .sortedBy { (type, _) -> type }
                .flatMap { (_, items) -> items }
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
                Mode.Remove -> Repo.planRepo.remove(it.old.servantId, HowToPerform.Launch)
                Mode.Change -> Repo.planRepo.insert(it.new!!, HowToPerform.Launch)
            }
        }

        deductItems.value?.forEach {
            if (it.checked)
                Repo.itemRepo.update(Item(it.codename, it.delta), HowToPerform.Launch)
        }

        finishEvent.call()
    }
}