package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.editplan.container.EditPlanContainerFragment
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment

class EditPlanActivityPresenter(val view: EditPlanActivity) {
    private val mode: EditPlanActivity.Companion.Mode = view.intent.extras[EditPlanActivity.ARG_MODE] as EditPlanActivity.Companion.Mode

    private val viewModel = ViewModelProviders.of(view).get(EditPlanViewModel::class.java).apply {
        // when this view is created the first time,
        // set all the values of the Plan to the ViewModel.
        if (firstCreate) {
            if (mode == EditPlanActivity.Companion.Mode.Edit) {
                oldPlan = (view.intent.extras[EditPlanActivity.ARG_PLAN] as Plan).also {
                    servantId.value = it.servantId
                    nowStage.value = it.nowStage
                    planStage.value = it.planStage
                    nowSkillI.value = it.nowSkill1
                    planSkillI.value = it.planSkill1
                    nowSkillII.value = it.nowSkill2
                    planSkillII.value = it.planSkill2
                    nowSkillIII.value = it.nowSkill3
                    planSkillIII.value = it.planSkill3
                }
            }
            if (servantId.value == 0)
                gotoServantListUi()
            else
                gotoEditPlanUi()
            firstCreate = false
        }
    }

    // reactions
    fun onServantSelected(servantId: Int) {
        viewModel.servantId.value = servantId
        gotoEditPlanUi()
    }

    fun onSaveAction() {
        when (mode) {
            EditPlanActivity.Companion.Mode.New -> {
                Repo.planRepo.insert(viewModel.plan, HowToPerform.Launch)
                view.finish()
            }
            EditPlanActivity.Companion.Mode.Edit -> {
                if (viewModel.plan != viewModel.oldPlan)
                    ChangePlanWarningDialogFragment.newInstanceForEdit(viewModel.oldPlan!!, viewModel.plan)
                            .show(view.supportFragmentManager, ChangePlanWarningDialogFragment.tag)
                else
                    view.finish()
            }
        }
    }

    fun onRemoveAction() {
        when (mode) {
            EditPlanActivity.Companion.Mode.New -> view.finish()
            EditPlanActivity.Companion.Mode.Edit ->
                ChangePlanWarningDialogFragment.newInstanceForRemove(viewModel.oldPlan!!)
                        .show(view.supportFragmentManager, ChangePlanWarningDialogFragment.tag)
        }
    }

    fun onWarningFragmentAction(mode: ChangePlanWarningDialogFragment.Companion.Mode, plan: Plan, deductItems: Boolean) {
        when (mode) {
            ChangePlanWarningDialogFragment.Companion.Mode.Edit -> Repo.planRepo.insert(viewModel.plan, HowToPerform.Launch)
            ChangePlanWarningDialogFragment.Companion.Mode.Remove -> Repo.planRepo.remove(viewModel.plan.servantId, HowToPerform.Launch)
        }
        if (deductItems)
            Repo.itemRepo.deductItems(listOf(plan).costItems, HowToPerform.Launch)
        view.finish()
    }

    private fun switchToFragment(fragment: Fragment, tag: String?) {
        view.supportFragmentManager.apply {
            beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frameLayout, fragment, tag)
                    .commit()
            executePendingTransactions()
        }
    }

    private fun gotoServantListUi() {
        switchToFragment(ServantListFragment(), ServantListFragment.tag)
    }

    private fun gotoEditPlanUi() {
        if (view.supportFragmentManager.findFragmentByTag(EditPlanContainerFragment.tag) == null) {
            switchToFragment(EditPlanContainerFragment.newInstance(mode), EditPlanContainerFragment.tag)
        }
    }
}