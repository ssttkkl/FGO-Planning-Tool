package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class EditPlanFragmentViewModel : ViewModel() {
    val mode = MutableLiveData<Mode>()

    val tabTitles = MyApp.context.resources.getStringArray(R.array.tabTitles).toList()

    var oldPlan: Plan? = null
    lateinit var plan: ObservablePlan

    val showEditWarningUIEvent = SingleLiveEvent<Pair<Plan, Plan>>()
    val showRemoveWarningUIEvent = SingleLiveEvent<Plan>()
    val showServantInfoEvent = SingleLiveEvent<Int>()
    val showEditNowLevelUIEvent = SingleLiveEvent<Triple<Int, Int, Boolean>>()
    val showEditPlanLevelUIEvent = SingleLiveEvent<Triple<Int, Int, Boolean>>()
    val finishEvent = SingleLiveEvent<Void>()

    fun onClickSave() {
        when (mode.value) {
            Mode.New -> {
                save()
                finishEvent.call()
            }
            Mode.Edit -> {
                if (oldPlan == plan.plan.value)
                    finishEvent.call()
                else
                    showEditWarningUIEvent.call(Pair(oldPlan ?: return, plan.plan.value ?: return))
            }
        }
    }

    fun onClickRemove() {
        when (mode.value) {
            Mode.New -> {
                finishEvent.call()
            }
            Mode.Edit -> {
                showRemoveWarningUIEvent.call(oldPlan)
            }
        }
    }

    fun onClickNowLevel() {
        showEditNowLevelUIEvent.call(Triple(plan.servantId.value ?: return,
                plan.nowExp.value ?: return,
                plan.ascendedOnNowStage.value == true))
    }

    fun onClickPlanLevel() {
        showEditPlanLevelUIEvent.call(Triple(plan.servantId.value ?: return,
                plan.planExp.value ?: return,
                plan.ascendedOnPlanStage.value == true))
    }

    fun onClickShowInfo() {
        showServantInfoEvent.call(plan.servantId.value)
    }

    fun onChangeWarningResult(mode: ChangePlanWarningDialogFragment.Companion.Mode, plans: Collection<Plan>, deductItems: Boolean) {
        when (mode) {
            ChangePlanWarningDialogFragment.Companion.Mode.Remove -> {
                remove(if (deductItems) oldPlan?.costItems else null)
                finishEvent.call()
            }
            ChangePlanWarningDialogFragment.Companion.Mode.Edit -> {
                save(if (deductItems) plans.costItems else null)
                finishEvent.call()
            }
        }
    }

    fun onEditNowLevelResult(exp: Int, ascendedOnStage: Boolean) {
        plan.nowExp.value = exp
        plan.ascendedOnNowStage.value = ascendedOnStage
    }

    fun onEditPlanLevelResult(exp: Int, ascendedOnStage: Boolean) {
        plan.planExp.value = exp
        plan.ascendedOnPlanStage.value = ascendedOnStage
    }

    private fun remove(itemToDeduct: Collection<Item>? = null) {
        Repo.planRepo.remove(oldPlan!!.servantId, HowToPerform.Launch)
        if (itemToDeduct != null && itemToDeduct.isNotEmpty())
            Repo.itemRepo.deductItems(itemToDeduct, HowToPerform.Launch)
    }

    private fun save(itemToDeduct: Collection<Item>? = null) {
        Repo.planRepo.insert(plan.plan.value ?: return, HowToPerform.Launch)
        if (itemToDeduct != null && itemToDeduct.isNotEmpty())
            Repo.itemRepo.deductItems(itemToDeduct, HowToPerform.Launch)
    }
}