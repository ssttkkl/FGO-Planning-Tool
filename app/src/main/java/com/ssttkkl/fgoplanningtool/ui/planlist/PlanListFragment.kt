package com.ssttkkl.fgoplanningtool.ui.planlist

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.FragmentPlanlistBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.planlist.confirmchangeplan.ConfirmChangePlanFragment
import com.ssttkkl.fgoplanningtool.ui.planlist.editplan.Mode
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandler
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class PlanListFragment : Fragment(),
        BackHandler,
        LifecycleOwner,
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
        setHasOptionsMenu(true)
        onInSelectModeChanged(false)

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
            addPlanEvent.observe(this@PlanListFragment, Observer {
                showNewPlanUI()
            })
            editPlanEvent.observe(this@PlanListFragment, Observer {
                showEditPlanUI(it ?: return@Observer)
            })
            calcResultEvent.observe(this@PlanListFragment, Observer {
                showCalcResultUI(it ?: return@Observer)
            })
            removePlansEvent.observe(this@PlanListFragment, Observer {
                showRemovePlansUI(it ?: return@Observer)
            })
            changeOriginEvent.observe(this@PlanListFragment, Observer {
                changeOrigin(it ?: listOf())
            })
            inSelectMode.observe(this@PlanListFragment, Observer {
                onInSelectModeChanged(it == true)
            })
            title.observe(this@PlanListFragment, Observer {
                onTitleChanged(it ?: "")
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
            R.id.sortAndFilter_action -> {
                if (binding.drawerlayout.isDrawerOpen(GravityCompat.END))
                    binding.drawerlayout.closeDrawer(GravityCompat.END)
                else
                    binding.drawerlayout.openDrawer(GravityCompat.END)
            }
            R.id.enterSelectMode_action -> binding.viewModel?.onEnterSelectModeClick()
            R.id.add_action -> binding.viewModel?.onAddClick()
            R.id.selectAll_action -> binding.viewModel?.onCheckAllClick()
            R.id.remove_action -> binding.viewModel?.onRemoveClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return when {
            binding.viewModel?.onBackPressed() == true -> true
            binding.drawerlayout.isDrawerOpen(GravityCompat.END) -> {
                binding.drawerlayout.closeDrawer(GravityCompat.END)
                true
            }
            else -> false
        }
    }

    override fun onFilter(filtered: List<Servant>) {
        binding.viewModel?.onFilter(filtered)
    }

    override fun onRequestCostItems(servant: Servant): Collection<Item> {
        return binding.viewModel?.getCostItems(servant) ?: listOf()
    }

    private fun showNewPlanUI() {
        findNavController().navigate(PlanListFragmentDirections.actionPlanListFragmentToChooseServantFragment())
    }

    private fun showEditPlanUI(plan: Plan) {
        findNavController().navigate(PlanListFragmentDirections.actionPlanListFragmentToEditPlanDetailFragment(plan, Mode.Edit))
    }

    private fun showCalcResultUI(plans: Collection<Plan>) {
        findNavController().navigate(R.id.action_planListFragment_to_costItemListFragment, bundleOf(
                "plans" to plans.toTypedArray()
        ))
    }

    private fun showRemovePlansUI(plans: Collection<Plan>) {
        findNavController().navigate(R.id.action_planListFragment_to_confirmChangePlanFragment,
                ConfirmChangePlanFragment.buildArgumentsWithRemove(plans))
    }

    private fun changeOrigin(servants: Collection<Servant>) {
        servantFilterFragment?.originServantIDs = servants.map { it.id }.toSet()
    }

    private fun onInSelectModeChanged(inSelectMode: Boolean) {
        (activity as? MainActivity)?.apply {
            setDrawerState(!inSelectMode)
            invalidateOptionsMenu()
        }
    }

    private fun onTitleChanged(title: String) {
        (activity as? MainActivity)?.title = title
    }
}