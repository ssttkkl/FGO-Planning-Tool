package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemCostitemlistBinding
import com.ssttkkl.fgoplanningtool.databinding.ItemCostitemlistHeaderBinding
import com.ssttkkl.fgoplanningtool.databinding.ItemCostitemlistRequirementBinding
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class CostItemListRecViewAdapter(val context: Context,
                                 private val lifecycleOwner: LifecycleOwner,
                                 private val viewModel: CostItemListFragmentViewModel)
    : ListAdapter<Any, RecyclerView.ViewHolder>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is CostItem -> VIEW_TYPE_COSTITEM
            is Header -> VIEW_TYPE_HEADER
            CostItemListFragmentViewModel.REQUIREMENTS_PLACE_HOLDER -> VIEW_TYPE_REQUIREMENT
            else -> throw Exception("unknown item type.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_COSTITEM -> CostItemViewHolder(ItemCostitemlistBinding.inflate(LayoutInflater.from(context), parent, false))
            VIEW_TYPE_HEADER -> HeaderViewModel(ItemCostitemlistHeaderBinding.inflate(LayoutInflater.from(context), parent, false))
            VIEW_TYPE_REQUIREMENT -> RequirementListViewModel(ItemCostitemlistRequirementBinding.inflate(LayoutInflater.from(context), parent, false))
            else -> throw Exception("unknown item type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CostItemViewHolder -> holder.bind(getItem(position) as CostItem)
            is HeaderViewModel -> holder.bind(getItem(position) as Header)
        }
    }

    inner class CostItemViewHolder(val binding: ItemCostitemlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(costItem: CostItem) {
            binding.costItem = costItem
            binding.executePendingBindings()
        }
    }

    inner class HeaderViewModel(val binding: ItemCostitemlistHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: Header) {
            binding.header = header
        }
    }

    inner class RequirementListViewModel(val binding: ItemCostitemlistRequirementBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)

            binding.recView.apply {
                adapter = RequirementListRecViewAdapter(context!!)
                layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                addItemDecoration(CommonRecViewItemDecoration(context!!))
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_COSTITEM = 1
        private const val VIEW_TYPE_HEADER = 2
        private const val VIEW_TYPE_REQUIREMENT = 3

        private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return if (oldItem is CostItem && newItem is CostItem)
                    oldItem.codename == newItem.codename
                else if (oldItem is Header && newItem is Header)
                    oldItem == newItem
                else
                    false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem
        }
    }
}