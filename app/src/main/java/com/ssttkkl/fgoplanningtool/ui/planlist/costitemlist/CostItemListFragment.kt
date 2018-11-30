package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import android.os.Bundle
import android.view.*
import android.widget.Switch
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
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
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandler
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class CostItemListFragment : Fragment(), BackHandler {
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
            adapter = CostItemListRecViewAdapter(context!!, this@CostItemListFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
        }
        binding.jumpRecView.apply {
            adapter = CostItemListJumpRecViewAdapter(context!!, this@CostItemListFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        }
        binding.viewModel?.apply {
            showServantInfoEvent.observe(this@CostItemListFragment, Observer {
                gotoServantDetailUi(it ?: return@Observer)
            })
            scrollToPositionEvent.observe(this@CostItemListFragment, Observer {
                scrollToPosition(it ?: return@Observer)
            })
        }
    }

    private lateinit var hideEnoughItemsSwitch: Switch

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.costitemlist, menu)

        val hideEnoughItemsItem = menu.findItem(R.id.hideEnoughItems)
        hideEnoughItemsSwitch = hideEnoughItemsItem.actionView.findViewById(R.id.switchWidget)
        hideEnoughItemsSwitch.text = hideEnoughItemsItem.title
        hideEnoughItemsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val alreadyChecked = binding.viewModel?.hideEnoughItems?.value == true
            if (alreadyChecked != isChecked)
                binding.viewModel?.hideEnoughItems?.value = isChecked
        }
        binding.viewModel?.hideEnoughItems?.observe(this, Observer {
            hideEnoughItemsSwitch.isChecked = it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.jump -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.END))
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                else
                    binding.drawerLayout.openDrawer(GravityCompat.END)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            true
        } else
            false
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
        findNavController().navigate(R.id.action_global_servantInfoFragment, bundleOf("servantID" to servantID))
    }

    private fun scrollToPosition(position: Int) {
        binding.recView.smoothScrollToPosition(position)
    }

    companion object {
        private const val ARG_PLANS = "plans"
        private const val ARG_ITEMS = "items"
    }
}