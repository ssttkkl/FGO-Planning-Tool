package com.ssttkkl.fgoplanningtool.ui.editplan

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditplanBinding
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment

class EditPlanFragment : androidx.fragment.app.Fragment(),
        ChangePlanWarningDialogFragment.OnActionListener {
    private lateinit var binding: FragmentEditplanBinding
    private val viewModel
        get() = binding.viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEditplanBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[EditPlanFragmentViewModel::class.java].apply {
            mode.value = arguments!![KEY_MODE] as Mode
            oldPlan = arguments!![KEY_PLAN] as Plan
            plan = ObservablePlan(oldPlan!!)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.apply {
            // setup the Toolbar
            setHasOptionsMenu(true)
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        // setup the viewpager
        binding.viewPager.adapter = EditPlanFragmentPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        // register events
        viewModel?.apply {
            showEditWarningUIEvent.observe(this@EditPlanFragment, Observer {
                if (it != null)
                    showEditPlanWarningUI(it.first, it.second)
            })
            showRemoveWarningUIEvent.observe(this@EditPlanFragment, Observer {
                showRemovePlanWarningUI(it ?: return@Observer)
            })
            finishEvent.observe(this@EditPlanFragment, Observer {
                activity?.finish()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.editplan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.finish()
            R.id.save_action -> viewModel?.onClickSave()
            R.id.remove_action -> viewModel?.onClickRemove()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onAction(mode: ChangePlanWarningDialogFragment.Companion.Mode, plans: Collection<Plan>, deductItems: Boolean) {
        viewModel?.onChangeWarningResult(mode, plans, deductItems)
    }

    private fun showRemovePlanWarningUI(plan: Plan) {
        ChangePlanWarningDialogFragment.newInstanceForRemove(plan)
                .show(childFragmentManager, ChangePlanWarningDialogFragment::class.qualifiedName)
    }

    private fun showEditPlanWarningUI(old: Plan, new: Plan) {
        ChangePlanWarningDialogFragment.newInstanceForEdit(old, new)
                .show(childFragmentManager, ChangePlanWarningDialogFragment::class.qualifiedName)
    }

    companion object {
        private const val KEY_MODE = "mode"
        private const val KEY_PLAN = "plan"

        fun newInstance(mode: Mode, plan: Plan) = EditPlanFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_PLAN, plan)
                putSerializable(KEY_MODE, mode)
            }
        }
    }
}