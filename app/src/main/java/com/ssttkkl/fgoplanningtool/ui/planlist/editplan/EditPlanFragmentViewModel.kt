package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.servant.Dress
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class EditPlanFragmentViewModel : ViewModel() {
    val mode = MutableLiveData<Mode>()

    private lateinit var oldPlan: Plan
    lateinit var plan: ObservablePlan

    private var firstCreate = true

    @Synchronized
    fun start(mode: Mode, plan: Plan) {
        if (firstCreate) {
            this.mode.value = mode
            this.oldPlan = plan
            this.plan = ObservablePlan(plan)

            firstCreate = false
        }
    }

    val showEditWarningUIEvent = SingleLiveEvent<Pair<Plan, Plan>>()
    val showRemoveWarningUIEvent = SingleLiveEvent<Plan>()
    val showServantInfoUIEvent = SingleLiveEvent<Int>()
    val showCostItemUIEvent = SingleLiveEvent<Collection<Item>>()
    val showEditNowLevelUIEvent = SingleLiveEvent<Triple<Int, Int, Boolean>>()
    val showEditPlanLevelUIEvent = SingleLiveEvent<Triple<Int, Int, Boolean>>()
    val finishEvent = SingleLiveEvent<Void>()

    fun onClickFab() {
        showCostItemUIEvent.call(plan.plan?.costItems ?: listOf())
    }

    fun onClickSave() {
        when (mode.value) {
            Mode.New -> {
                Repo.PlanRepo.insert(plan.plan ?: return)
                finishEvent.call()
            }
            Mode.Edit -> {
                if (oldPlan == plan.plan)
                    finishEvent.call()
                else
                    showEditWarningUIEvent.call(Pair(oldPlan, plan.plan ?: return))
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
        showServantInfoUIEvent.call(plan.servantId.value)
    }

    fun onEditNowLevelResult(exp: Int, ascendedOnStage: Boolean) {
        plan.nowExp.value = exp
        plan.ascendedOnNowStage.value = ascendedOnStage
    }

    fun onEditPlanLevelResult(exp: Int, ascendedOnStage: Boolean) {
        plan.planExp.value = exp
        plan.ascendedOnPlanStage.value = ascendedOnStage
    }

    fun onClickDress(dress: Dress) {
        plan.dress.value = plan.dress.value?.toMutableSet()?.apply {
            if (contains(dress))
                remove(dress)
            else
                add(dress)
        }
    }
}