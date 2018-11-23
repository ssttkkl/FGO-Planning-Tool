package com.ssttkkl.fgoplanningtool.ui.costitemlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemCostitemlistBinding
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.item_costitemlist.view.*
import net.cachapa.expandablelayout.ExpandableLayout.State

class CostItemListAdapter(val context: Context,
                          private val lifecycleOwner: LifecycleOwner,
                          private val viewModel: CostItemListFragmentViewModel) : ListAdapter<CostItem, CostItemListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<CostItem>() {
    override fun areItemsTheSame(oldItem: CostItem, newItem: CostItem) = oldItem.codename == newItem.codename
    override fun areContentsTheSame(oldItem: CostItem, newItem: CostItem) = oldItem == newItem
}) {
    init {
        viewModel.data.observe(lifecycleOwner, Observer { submitList(it ?: listOf()) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemCostitemlistBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemCostitemlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)

            binding.recView.apply {
                adapter = RequirementListRecViewAdapter(context).apply {
                    setOnItemClickListener { _, item -> viewModel.onClickServant(item.servantID) }
                }
                layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                addItemDecoration(CommonRecViewItemDecoration(context!!))
            }
        }

        fun bind(costItem: CostItem) {
            binding.enableExpansionAnimation = false

            binding.costItem = costItem
            (binding.recView.adapter as? RequirementListRecViewAdapter)?.data = costItem.requirements

            if (adapterPosition == 0 || (getItem(adapterPosition - 1).descriptor?.type != getItem(adapterPosition).descriptor?.type)) {
                binding.showDivider = adapterPosition != 0
                binding.showHeader = true
            } else {
                binding.showDivider = false
                binding.showHeader = false
            }
            
            binding.executePendingBindings()
            binding.enableExpansionAnimation = true
        }
    }
}