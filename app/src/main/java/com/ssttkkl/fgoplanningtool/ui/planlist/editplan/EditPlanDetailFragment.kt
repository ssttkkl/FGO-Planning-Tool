package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment

class EditPlanDetailFragment : Fragment(),
        LifecycleOwner,
        EditLevelDialogFragment.OnSaveListener,
        ChangePlanWarningDialogFragment.OnActionListener {
    private lateinit var binding: FragmentEditplanDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEditplanDetailBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[EditPlanFragmentViewModel::class.java].apply {
            mode.value = arguments!!.getParcelable("mode") as Mode
            oldPlan = arguments!!.getParcelable("plan") as Plan
            plan = ObservablePlan(oldPlan!!)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        binding.dressRecView.apply {
            adapter = EditPlanDressListRecViewAdapter(context!!)
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
                findNavController().navigate(R.id.action_global_planListFragment)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.editplan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.save_action -> binding.viewModel?.onClickSave()
            R.id.remove_action -> binding.viewModel?.onClickRemove()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onAction(mode: ChangePlanWarningDialogFragment.Companion.Mode, plans: Collection<Plan>, deductItems: Boolean) {
        binding.viewModel?.onChangeWarningResult(mode, plans, deductItems)
    }

    override fun onSave(exp: Int, ascendedOnStage: Boolean, extraTag: String?) {
        when (extraTag) {
            "now" -> binding.viewModel?.onEditNowLevelResult(exp, ascendedOnStage)
            "plan" -> binding.viewModel?.onEditPlanLevelResult(exp, ascendedOnStage)
        }
    }

    private fun showRemovePlanWarningUI(plan: Plan) {
        ChangePlanWarningDialogFragment.newInstanceForRemove(plan)
                .show(childFragmentManager, ChangePlanWarningDialogFragment::class.qualifiedName)
    }

    private fun showEditPlanWarningUI(old: Plan, new: Plan) {
        ChangePlanWarningDialogFragment.newInstanceForEdit(old, new)
                .show(childFragmentManager, ChangePlanWarningDialogFragment::class.qualifiedName)
    }

    private fun showEditLevelUI(servantID: Int, exp: Int, ascendedOnStage: Boolean, tag: String) {
        EditLevelDialogFragment.newInstance(servantID, exp, ascendedOnStage, tag)
                .show(childFragmentManager, EditLevelDialogFragment::class.qualifiedName)
    }

    private fun showServantInfo(servantID: Int) {
        if (servantID > 0)
            ServantInfoDialogFragment.newInstance(servantID)
                    .show(childFragmentManager, ServantInfoDialogFragment::class.qualifiedName)
    }

    private fun showCostItems(costItems: Collection<Item>) {
        findNavController().navigate(R.id.action_editPlanDetailFragment_to_costItemListFragment, Bundle().apply {
            putParcelableArray("items", costItems.toTypedArray())
        })
    }
}
