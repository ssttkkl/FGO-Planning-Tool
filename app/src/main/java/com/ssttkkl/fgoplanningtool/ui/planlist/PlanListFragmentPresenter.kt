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
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.costitemlist.CostItemListActivity
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanActivity
import kotlinx.android.synthetic.main.fragment_planlist.*

class PlanListFragmentPresenter(val view: PlanListFragment) : PlanListRecViewAdapter.Callback {
    init {
        Repo.planListLiveData.observe(view, Observer { onDatabaseChanged(it ?: listOf()) })
    }

    // access database
    fun removePlan(plans: Collection<Plan>, deductItems: Boolean) {
        Repo.planRepo.remove(plans.map { it.servantId }, HowToPerform.Launch)
        if (deductItems)
            Repo.itemRepo.deductItems(plans.costItems, HowToPerform.Launch)
    }

    // handle ui events
    fun onBackPressed(): Boolean {
        return if (view.isInSelectMode) {
            view.isInSelectMode = false
            true
        } else false
    }

    fun onCalcResultAction() {
        gotoCalcResultUi(if (view.isInSelectMode) view.selectedPositions.map { view.data[it] }.toTypedArray()
        else view.data.toTypedArray())
    }

    fun onNewPlanAction() {
        gotoNewPlanUi()
    }

    fun onRemovePlanAction() {
        if (view.isAnySelected)
            ChangePlanWarningDialogFragment.newInstanceForRemove(view.selectedPositions.map { view.data[it] })
                    .show(view.childFragmentManager, ChangePlanWarningDialogFragment.tag)
        view.isInSelectMode = false
    }

    fun onSelectAllAction() {
        if (view.isAllSelected)
            view.deselectAll()
        else
            view.selectAll()
    }

    fun onGetInSelectModeAction() {
        view.isInSelectMode = true
    }

    fun onExitSelectModeAction() {
        view.isInSelectMode = false
    }

    fun onFiltered(filtered: List<Servant>) {
        val isInSelectMode = view.isInSelectMode
        val selectedServantIDs = view.selectedPositions.map { view.data[it].servantId }
        val servantIDs = filtered.map { it.id }
        view.data = Repo.planRepo.all.asSequence()
                .sortedBy { servantIDs.indexOf(it.servantId) }
                .filter { servantIDs.contains(it.servantId) }
                .toList()
        if (isInSelectMode) {
            view.isInSelectMode = true
            view.data.forEachIndexed { idx, it ->
                if (selectedServantIDs.contains(it.servantId))
                    view.select(idx)
            }
        }
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
                title = view.getString(R.string.selectedCount_planlist).format(view.selectedPositions.size, view.data.size)
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

    override fun onSelectStateChanged(pos: Int, selected: Boolean) {
        (view.activity as? AppCompatActivity)?.supportActionBar?.title =
                view.getString(R.string.selectedCount_planlist).format(view.selectedPositions.size, view.data.size)
    }

    // private methods
    private fun gotoNewPlanUi() {
        view.startActivity(Intent(view.activity, EditPlanActivity::class.java).apply {
            putExtra(EditPlanActivity.ARG_MODE, EditPlanActivity.Mode.New)
        })
    }

    private fun gotoEditPlanUi(plan: Plan) {
        view.startActivity(Intent(view.activity, EditPlanActivity::class.java).apply {
            putExtra(EditPlanActivity.ARG_MODE, EditPlanActivity.Mode.Edit)
            putExtra(EditPlanActivity.ARG_PLAN, plan)
        })
    }

    private fun gotoCalcResultUi(plans: Array<Plan>) {
        view.startActivity(Intent(view.activity, CostItemListActivity::class.java).apply {
            putExtra(CostItemListActivity.ARG_PLANS, plans)
        })
    }

    private fun onDatabaseChanged(data: List<Plan>) {
        view.origin = data.map { ResourcesProvider.instance.servants[it.servantId]!! }
        Log.d("PlanList", "Data Changed.")
    }
}