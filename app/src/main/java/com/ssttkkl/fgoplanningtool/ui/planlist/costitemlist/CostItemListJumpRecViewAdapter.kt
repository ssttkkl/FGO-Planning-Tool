package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemCostitemlistJumpBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType

class CostItemListJumpRecViewAdapter(val context: Context,
                                     private val lifecycleOwner: LifecycleOwner,
                                     private val viewModel: CostItemListFragmentViewModel) : ListAdapter<ItemType, CostItemListJumpRecViewAdapter.ViewHolder>(object : DiffUtil.ItemCallback<ItemType>() {
    override fun areContentsTheSame(oldItem: ItemType, newItem: ItemType) = oldItem == newItem
    override fun areItemsTheSame(oldItem: ItemType, newItem: ItemType) = oldItem == newItem
}) {
    init {
        viewModel.itemTypes.observe(lifecycleOwner, Observer { submitList(it ?: listOf()) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemCostitemlistJumpBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemCostitemlistJumpBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(itemType: ItemType) {
            binding.itemType = itemType
            binding.executePendingBindings()
        }
    }
}