package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import android.os.Bundle
import android.view.*
import android.widget.Switch
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.FragmentCostitemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandler
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
            invalidateOptionsMenu()
        }

        binding.recView.apply {
            adapter = CostItemListRecViewAdapter(context!!, this@CostItemListFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
        }
        binding.viewModel?.apply {
            itemTypes.observe(this@CostItemListFragment, Observer {
                onItemTypesChanged(it ?: listOf())
            })
            scrollToPositionEvent.observe(this@CostItemListFragment, Observer {
                scrollToPosition(it ?: return@Observer)
            })
        }
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

    private fun onItemTypesChanged(itemTypes: List<ItemType>) {
        TransitionManager.beginDelayedTransition(binding.jumpChipGroup)
        binding.jumpChipGroup.removeAllViews()
        itemTypes.forEach { itemType ->
            val chip = Chip(binding.jumpChipGroup.context).apply {
                text = itemType.localizedName
                setOnClickListener {
                    binding.viewModel?.onClickJumpItem(itemType)
                }
                transitionName = itemType.name
            }
            binding.jumpChipGroup.addView(chip)
        }
        TransitionManager.endTransitions(binding.jumpChipGroup)
    }

    private fun scrollToPosition(position: Int) {
        binding.recView.smoothScrollToPosition(position)
    }

    companion object {
        private const val ARG_PLANS = "plans"
        private const val ARG_ITEMS = "items"
    }
}