package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.FragmentCostitemlistBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class CostItemListFragment : Fragment() {
    private lateinit var binding: FragmentCostitemlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCostitemlistBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[CostItemListFragmentViewModel::class.java].apply {
            arguments?.getParcelableArray(ARG_PLANS)?.mapNotNull { it as? Plan }?.also {
                setDataFromPlans(it)
            }
            arguments?.getParcelableArray(ARG_ITEMS)?.mapNotNull { it as? Item }?.also {
                setDataFromItems(it)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
            title = getString(R.string.title_costitemlist)
        }
        setHasOptionsMenu(true)

        binding.recView.apply {
            adapter = CostItemListAdapter(context!!, this@CostItemListFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
        }
        binding.viewModel?.apply {
            showServantInfoEvent.observe(this@CostItemListFragment, Observer {
                gotoServantDetailUi(it ?: return@Observer)
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    var plans: Collection<Plan>
        get() = arguments?.getParcelableArray(ARG_PLANS)?.mapNotNull { it as? Plan } ?: listOf()
        set(value) {
            (arguments
                    ?: Bundle().also { arguments = it }).putParcelableArray(ARG_PLANS, value.toTypedArray())
            if (::binding.isInitialized)
                binding.viewModel?.setDataFromPlans(value)
        }

    var items: Collection<Item>
        get() = arguments?.getParcelableArray(ARG_ITEMS)?.mapNotNull { it as? Item } ?: listOf()
        set(value) {
            (arguments
                    ?: Bundle().also { arguments = it }).putParcelableArray(ARG_ITEMS, value.toTypedArray())
            if (::binding.isInitialized)
                binding.viewModel?.setDataFromItems(value)
        }

    private fun gotoServantDetailUi(servantID: Int) {
        findNavController().navigate(R.id.action_global_servantInfoFragment, bundleOf ("servantID" to servantID))
    }

    companion object {
        private const val ARG_PLANS = "plans"
        private const val ARG_ITEMS = "items"
    }
}