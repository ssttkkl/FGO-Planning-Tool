package com.ssttkkl.fgoplanningtool.ui.planlist

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.costitemlist.CostItemListActivity
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanActivity
import kotlinx.android.synthetic.main.fragment_planlist.*

class PlanListFragmentPresenter(val view: PlanListFragment) : PlanListRecViewAdapter.Callback {
    private val adapter
        get() = view.recView.adapter as PlanListRecViewAdapter

    init {
        Repo.planListLiveData.observe(view, Observer { onDataChanged(it) })

        if (adapter.isInSelectMode)
            onSelectModeEnabled()
    }

    val inSelectMode
        get() = adapter.isInSelectMode

    val selectedPositions
        get() = adapter.selectedPositions

    // access database
    fun removePlan(plans: Collection<Plan>, deductItems: Boolean) {
        Repo.planRepo.remove(plans.map { it.servantId }, HowToPerform.Launch)
        if (deductItems)
            Repo.itemRepo.deductItems(plans.costItems, HowToPerform.Launch)
    }

    // handle ui events
    fun onBackPressed(): Boolean {
        return if (adapter.isInSelectMode) {
            adapter.isInSelectMode = false
            true
        } else false
    }

    fun onCalcResultAction() {
        gotoCalcResultUi(if (adapter.isInSelectMode) adapter.selectedPositions.map { view.data[it] }.toTypedArray()
        else view.data.toTypedArray())
    }

    fun onNewPlanAction() {
        gotoNewPlanUi()
    }

    fun onRemoveAction() {
        if (adapter.isAnyPositionSelected)
            ChangePlanWarningDialogFragment.newInstanceForRemove(adapter.selectedPositions.map { view.data[it] })
                    .show(view.childFragmentManager, ChangePlanWarningDialogFragment.tag)
        else
            adapter.isInSelectMode = false
    }

    fun onSelectAllAction() {
        adapter.isInSelectMode = true // maybe no sense
        if (!adapter.isAllPositionsSelected)
            adapter.selectAllPositions()
        else
            adapter.unselectAllPositions()
    }

    fun onGetInSelectModeAction() {
        adapter.isInSelectMode = true
    }

    fun onExitSelectModeAction() {
        adapter.isInSelectMode = false
    }

    // handle adapter events
    override fun onItemClickedInNormalMode(pos: Int) {
        gotoEditPlanUi(view.data[pos])
    }

    override fun onSelectModeEnabled() {
        view.toolbar_inSelectMode.visibility = View.VISIBLE
        view.toolbar.visibility = View.GONE
        (view.activity as? AppCompatActivity)?.apply {
            setSupportActionBar(view.toolbar_inSelectMode)
            supportActionBar?.apply {
                title = view.getString(R.string.selectedCount_planlist).format(adapter.selectedPositions.size, adapter.data.size)
                setDisplayHomeAsUpEnabled(true)
            }
            invalidateOptionsMenu()
        }
    }

    override fun onSelectModeDisabled() {
        view.toolbar_inSelectMode.visibility = View.GONE
        view.toolbar.visibility = View.VISIBLE
        (view.activity as? AppCompatActivity)?.apply {
            setSupportActionBar(view.toolbar)
            supportActionBar?.title = view.getString(R.string.title_planlist)
            invalidateOptionsMenu()
        }
        view.setupDrawerToggle()
    }

    override fun onPositionSelectStateChanged(pos: Int, selected: Boolean) {
        (view.activity as? AppCompatActivity)?.supportActionBar?.title =
                view.getString(R.string.selectedCount_planlist).format(adapter.selectedPositions.size, adapter.data.size)
    }

    // private methods
    private fun gotoNewPlanUi() {
        view.startActivity(Intent(view.activity, EditPlanActivity::class.java).apply {
            putExtra(EditPlanActivity.ARG_MODE, EditPlanActivity.Companion.Mode.New)
        })
    }

    private fun gotoEditPlanUi(plan: Plan) {
        view.startActivity(Intent(view.activity, EditPlanActivity::class.java).apply {
            putExtra(EditPlanActivity.ARG_MODE, EditPlanActivity.Companion.Mode.Edit)
            putExtra(EditPlanActivity.ARG_PLAN, plan)
        })
    }

    private fun gotoCalcResultUi(plans: Array<Plan>) {
        view.startActivity(Intent(view.activity, CostItemListActivity::class.java).apply {
            putExtra(CostItemListActivity.ARG_PLANS, plans)
        })
    }

    private fun onDataChanged(data: List<Plan>?) {
        view.data = data ?: ArrayList()
        Log.d("PlanList", "Data Changed.")
    }
}