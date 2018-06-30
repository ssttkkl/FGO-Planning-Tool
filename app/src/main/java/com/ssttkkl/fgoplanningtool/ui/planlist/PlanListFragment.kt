package com.ssttkkl.fgoplanningtool.ui.planlist

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_planlist.*

class PlanListFragment : BackHandlerFragment(),
        LifecycleOwner,
        ChangePlanWarningDialogFragment.OnActionListener {
    private lateinit var presenter: PlanListFragmentPresenter

    var data: List<Plan>
        get() = (recView?.adapter as? PlanListRecViewAdapter)?.data ?: listOf()
        set(value) {
            (recView?.adapter as? PlanListRecViewAdapter)?.setNewData(value)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_planlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // setup Toolbar
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        setupDrawerToggle()

        // setup Fab
        fab.setOnClickListener { presenter.onCalcResultAction() }

        // setup RecView
        recView.apply {
            adapter = PlanListRecViewAdapter(context!!)
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, true, false))
            hasFixedSize()
        }

        presenter = PlanListFragmentPresenter(this).also {
            (recView.adapter as PlanListRecViewAdapter).setCallback(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(if (presenter.inSelectMode) R.menu.planlist_inselectmode else R.menu.planlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> presenter.onExitSelectModeAction()
            R.id.enterSelectMode_action -> presenter.onGetInSelectModeAction()
            R.id.add_action -> presenter.onNewPlanAction()
            R.id.selectAll_action -> presenter.onSelectAllAction()
            R.id.remove_action -> presenter.onRemoveAction()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return if (presenter.onBackPressed()) true
        else super.onBackPressed()
    }

    override fun onAction(mode: ChangePlanWarningDialogFragment.Companion.Mode, plans: Collection<Plan>, deductItems: Boolean) {
        presenter.removePlan(plans, deductItems)
    }


    private lateinit var toggle: ActionBarDrawerToggle

    fun setupDrawerToggle() {
        if (::toggle.isInitialized)
            activity?.drawerlayout?.removeDrawerListener(toggle)
        toggle = ActionBarDrawerToggle(activity,
                activity?.drawerlayout,
                toolbar,
                R.string.openDrawer_main,
                R.string.closeDrawer_main).also {
            activity?.drawerlayout?.addDrawerListener(it)
            it.syncState()
        }
    }
}