package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
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
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditplanDetailBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.planlist.confirmchangeplan.ConfirmChangePlanFragment

class EditPlanDetailFragment : Fragment(),
        LifecycleOwner,
        EditLevelDialogFragment.OnSaveListener {
    private lateinit var binding: FragmentEditplanDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEditplanDetailBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[EditPlanFragmentViewModel::class.java].apply {
            start(arguments!!.getParcelable("mode") as Mode,
                    arguments!!.getParcelable("plan") as Plan)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            title = getString(R.string.editPlan)
            drawerState = false
        }
        setHasOptionsMenu(true)

        binding.dressRecView.apply {
            adapter = EditPlanDressListRecViewAdapter(context!!, this@EditPlanDetailFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            isNestedScrollingEnabled = false
        }

        binding.viewModel?.apply {
            showServantInfoUIEvent.observe(this@EditPlanDetailFragment, Observer {
                showServantInfo(it ?: return@Observer)
            })
            showEditNowLevelUIEvent.observe(this@EditPlanDetailFragment, Observer {
                if (it != null)
                    showEditLevelUI(it.first, it.second, it.third, "now")
            })
            showEditPlanLevelUIEvent.observe(this@EditPlanDetailFragment, Observer {
                if (it != null)
                    showEditLevelUI(it.first, it.second, it.third, "plan")
            })
            showEditWarningUIEvent.observe(this@EditPlanDetailFragment, Observer {
                if (it != null)
                    showEditPlanWarningUI(it.first, it.second)
            })
            showCostItemUIEvent.observe(this@EditPlanDetailFragment, Observer {
                showCostItems(it ?: listOf())
            })
            showRemoveWarningUIEvent.observe(this@EditPlanDetailFragment, Observer {
                showRemovePlanWarningUI(it ?: return@Observer)
            })
            finishEvent.observe(this@EditPlanDetailFragment, Observer {
                finish()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.editplan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> binding.viewModel?.onClickSave()
            R.id.remove -> binding.viewModel?.onClickRemove()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSave(exp: Int, ascendedOnStage: Boolean, extraTag: String?) {
        when (extraTag) {
            "now" -> binding.viewModel?.onEditNowLevelResult(exp, ascendedOnStage)
            "plan" -> binding.viewModel?.onEditPlanLevelResult(exp, ascendedOnStage)
        }
    }

    private fun showRemovePlanWarningUI(plan: Plan) {
        findNavController().navigate(R.id.action_editPlanDetailFragment_to_confirmChangePlanFragment,
                ConfirmChangePlanFragment.buildArgumentsWithRemove(plan))
    }

    private fun showEditPlanWarningUI(old: Plan, new: Plan) {
        findNavController().navigate(R.id.action_editPlanDetailFragment_to_confirmChangePlanFragment,
                ConfirmChangePlanFragment.buildArgumentsWithChange(old, new))
    }

    private fun showEditLevelUI(servantID: Int, exp: Int, ascendedOnStage: Boolean, tag: String) {
        EditLevelDialogFragment.newInstance(servantID, exp, ascendedOnStage, tag)
                .show(childFragmentManager, EditLevelDialogFragment::class.qualifiedName)
    }

    private fun showServantInfo(servantID: Int) {
        findNavController().navigate(EditPlanDetailFragmentDirections.actionEditPlanDetailFragmentToServantInfoFragment(servantID))
    }

    private fun showCostItems(costItems: Collection<Item>) {
        findNavController().navigate(R.id.action_editPlanDetailFragment_to_costItemListFragment, bundleOf("items" to costItems.toTypedArray()))
    }

    private fun finish() {
        findNavController().navigate(EditPlanDetailFragmentDirections.actionGlobalPlanListFragment())
    }
}
