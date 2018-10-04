package com.ssttkkl.fgoplanningtool.ui.planlist

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.fragment_planlist.*

class PlanListFragment : BackHandlerFragment(),
        LifecycleOwner,
        ChangePlanWarningDialogFragment.OnActionListener,
        ServantFilterFragment.OnFilterListener {
    private lateinit var presenter: PlanListFragmentPresenter

    private val adapter
        get() = recView?.adapter as? PlanListRecViewAdapter

    private val servantFilterFragment
        get() = childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName) as? ServantFilterFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_planlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // setup Toolbar
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? MainActivity)?.setupDrawerToggle(toolbar)

        // setup Fab
        fab.setOnClickListener { presenter.onCalcResultAction() }

        // setup presenter
        presenter = PlanListFragmentPresenter(this)

        // setup ServantFilterFragment
        if (childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName) == null) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, ServantFilterFragment().apply {
                        planGetter = { Repo.planRepo[it] }
                    }, ServantFilterFragment::class.qualifiedName)
                    .commit()
        }

        // setup RecView
        recView.apply {
            adapter = PlanListRecViewAdapter(context!!).apply {
                setCallback(presenter)
            }
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, true, false))
            hasFixedSize()
        }


        // restore select state
        if (savedInstanceState != null) {
            data = (savedInstanceState.getParcelableArray(KEY_DATA).map { it as Plan })
            if (savedInstanceState.getBoolean(KEY_IN_SELECT_MODE, false)) {
                val selectedServantIDs = savedInstanceState.getIntArray(KEY_SELECTED_SERVANT_IDS)
                data.forEachIndexed { idx, it ->
                    if (selectedServantIDs.contains(it.servantId))
                        adapter?.select(idx)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArray(KEY_DATA, data.toTypedArray())
        if (isInSelectMode) {
            outState.putBoolean(KEY_IN_SELECT_MODE, true)
            outState.putIntArray(KEY_SELECTED_SERVANT_IDS, selectedPositions.map { data[it].servantId }.toIntArray())
        } else {
            outState.putBoolean(KEY_IN_SELECT_MODE, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(if (isInSelectMode) R.menu.planlist_inselectmode else R.menu.planlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> presenter.onExitSelectModeAction()
            R.id.sortAndFilter_action -> drawerlayout.openDrawer(Gravity.END)
            R.id.enterSelectMode_action -> presenter.onGetInSelectModeAction()
            R.id.add_action -> presenter.onNewPlanAction()
            R.id.selectAll_action -> presenter.onSelectAllAction()
            R.id.remove_action -> presenter.onRemovePlanAction()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return if (presenter.onBackPressed()) true
        else super.onBackPressed()
    }

    override fun onAction(mode: ChangePlanWarningDialogFragment.Companion.Mode,
                          plans: Collection<Plan>,
                          deductItems: Boolean) {
        presenter.removePlan(plans, deductItems)
    }

    override fun onFilter(filtered: List<Servant>) {
        presenter.onFiltered(filtered)
    }

    // interface for presenter to control ui
    var data: List<Plan>
        get() = adapter?.data ?: listOf()
        set(value) {
            adapter?.setNewData(value)
            recView?.invalidateItemDecorations()
        }

    var origin
        get() = servantFilterFragment?.origin ?: listOf()
        set(value) {
            servantFilterFragment?.origin = value
        }

    var isInSelectMode
        get() = adapter?.isInSelectMode == true
        set(value) {
            adapter?.isInSelectMode = value
        }

    val selectedPositions
        get() = adapter?.selectedPositions ?: setOf()

    val isAllSelected
        get() = adapter?.isAllSelected == true

    val isAnySelected
        get() = adapter?.isAnySelected == true

    fun selectAll() {
        adapter?.selectAll()
    }

    fun deselectAll() {
        adapter?.deselectAll()
    }

    fun select(pos: Int) {
        adapter?.select(pos)
    }

    fun setupDrawerToggle() {
        (activity as? MainActivity)?.setupDrawerToggle(toolbar)
    }

    companion object {
        private const val KEY_DATA = "data"
        private const val KEY_IN_SELECT_MODE = "inSelectMode"
        private const val KEY_SELECTED_SERVANT_IDS = "selectedServantIDs"
    }
}