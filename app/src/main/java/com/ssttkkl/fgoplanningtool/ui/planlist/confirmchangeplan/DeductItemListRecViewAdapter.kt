package com.ssttkkl.fgoplanningtool.ui.planlist.confirmchangeplan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemConfirmChangePlanDeductItemListBinding

class DeductItemListRecViewAdapter(val context: Context,
                                   private val lifecycleOwner: LifecycleOwner,
                                   private val viewModel: ConfirmChangePlanFragmentViewModel) : ListAdapter<DeductItem, DeductItemListRecViewAdapter.ViewHolder>(object : DiffUtil.ItemCallback<DeductItem>() {
    override fun areContentsTheSame(oldItem: DeductItem, newItem: DeductItem) = oldItem == newItem
    override fun areItemsTheSame(oldItem: DeductItem, newItem: DeductItem) = oldItem.codename == newItem.codename
}) {
    init {
        viewModel.deductItems.observe(lifecycleOwner, Observer { submitList(it ?: listOf()) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ItemConfirmChangePlanDeductItemListBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemConfirmChangePlanDeductItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(deductItem: DeductItem) {
            if (adapterPosition == 0 || (getItem(adapterPosition - 1).descriptor?.type != getItem(adapterPosition).descriptor?.type)) {
                binding.showDivider = adapterPosition != 0
                binding.showHeader = true
            } else {
                binding.showDivider = false
                binding.showHeader = false
            }

            binding.deductItem = deductItem

            binding.executePendingBindings()
        }
    }
}