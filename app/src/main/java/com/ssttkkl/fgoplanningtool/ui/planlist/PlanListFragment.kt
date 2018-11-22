package com.ssttkkl.fgoplanningtool.ui.planlist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.GravityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.FragmentPlanlistBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.costitemlist.CostItemListActivity
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanActivity
import com.ssttkkl.fgoplanningtool.ui.editplan.Mode
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class PlanListFragment : BackHandlerFragment(),
        LifecycleOwner,
        ChangePlanWarningDialogFragment.OnActionListener,
        ServantFilterFragment.Callback {
    private lateinit var binding: FragmentPlanlistBinding

    private val servantFilterFragment
        get() = childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName) as? ServantFilterFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlanlistBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[PlanListFragmentViewModel::class.java]
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // setup Toolbar
        setHasOptionsMenu(true)
        onRefreshToolbar(false)

        // setup ServantFilterFragment
        if (childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName) == null) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, ServantFilterFragment(), ServantFilterFragment::class.qualifiedName)
                    .commit()
        }

        // setup RecView
        binding.recView.apply {
            adapter = PlanListRecViewAdapter(context!!, this@PlanListFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
            hasFixedSize()
        }

        binding.viewModel?.apply {
            addPlanEvent.observe(this@PlanListFragment, Observer { onAddPlan() })
            editPlanEvent.observe(this@PlanListFragment, Observer {
                onEditPlan(it ?: return@Observer)
            })
            calcResultEvent.observe(this@PlanListFragment, Observer {
                onCalcResult(it ?: return@Observer)
            })
            removePlansEvent.observe(this@PlanListFragment, Observer {
                onRemovePlans(it ?: return@Observer)
            })
            changeOriginEvent.observe(this@PlanListFragment, Observer {
                onChangeOrigin(it ?: listOf())
            })
            inSelectMode.observe(this@PlanListFragment, Observer {
                onRefreshToolbar(it == true)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(if (binding.viewModel?.inSelectMode?.value == true)
            R.menu.planlist_inselectmode
        else
            R.menu.planlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> binding.viewModel?.onHomeClick()
            R.id.sortAndFilter_action -> binding.drawerlayout.openDrawer(GravityCompat.END)
            R.id.enterSelectMode_action -> binding.viewModel?.onEnterSelectModeClick()
            R.id.add_action -> binding.viewModel?.onAddClick()
            R.id.selectAll_action -> binding.viewModel?.onCheckAllClick()
            R.id.remove_action -> binding.viewModel?.onRemoveClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return if (binding.viewModel?.onBackPressed() == true)
            true
        else
            super.onBackPressed()
    }

    override fun onAction(mode: ChangePlanWarningDialogFragment.Companion.Mode,
                          plans: Collection<Plan>,
                          deductItems: Boolean) {
        binding.viewModel?.onRemoveWarningUIResult(plans, deductItems)
    }

    override fun onFilter(filtered: List<Servant>) {
        binding.viewModel?.onFilter(filtered)
    }

    override fun onRequestCostItems(servant: Servant): Collection<Item> {
        return binding.viewModel?.getCostItems(servant) ?: listOf()
    }

    private fun onAddPlan() {
        startActivity(Intent(activity, EditPlanActivity::class.java).apply {
            putExtra(EditPlanActivity.ARG_MODE, Mode.New)
        })
    }

    private fun onEditPlan(plan: Plan) {
        startActivity(Intent(activity, EditPlanActivity::class.java).apply {
            putExtra(EditPlanActivity.ARG_MODE, Mode.Edit)
            putExtra(EditPlanActivity.ARG_PLAN, plan)
        })
    }

    private fun onCalcResult(plans: Collection<Plan>) {
        startActivity(Intent(activity, CostItemListActivity::class.java).apply {
            putExtra(CostItemListActivity.ARG_PLANS, plans.toTypedArray())
        })
    }

    private fun onRemovePlans(plans: Collection<Plan>) {
        ChangePlanWarningDialogFragment.newInstanceForRemove(plans)
                .show(childFragmentManager, ChangePlanWarningDialogFragment.tag)
    }

    private fun onChangeOrigin(servants: Collection<Servant>) {
        servantFilterFragment?.originServantIDs = servants.map { it.id }.toSet()
    }

    private fun onRefreshToolbar(inSelectMode: Boolean) {
        (activity as? MainActivity)?.apply {
            if (inSelectMode) {
                binding.toolbarInSelectMode.visibility = View.VISIBLE
                binding.toolbar.visibility = View.GONE
                setSupportActionBar(binding.toolbarInSelectMode)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                binding.toolbarInSelectMode.visibility = View.GONE
                binding.toolbar.visibility = View.VISIBLE
                setSupportActionBar(binding.toolbar)
                setupDrawerToggle(binding.toolbar)
            }
            invalidateOptionsMenu()
        }
    }
}