package com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemConfirmchangeeventAdditemlistBinding

class AddItemListRecViewAdapter(val context: Context,
                                   private val lifecycleOwner: LifecycleOwner,
                                   private val viewModel: ConfirmChangeEventFragmentViewModel)
    : ListAdapter<AddItem, AddItemListRecViewAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ItemConfirmchangeeventAdditemlistBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemConfirmchangeeventAdditemlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(item: AddItem) {
            if (adapterPosition == 0 || (getItem(adapterPosition - 1).descriptor?.type != getItem(adapterPosition).descriptor?.type)) {
                binding.showDivider = adapterPosition != 0
                binding.showHeader = true
            } else {
                binding.showDivider = false
                binding.showHeader = false
            }

            binding.item = item

            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<AddItem>() {
            override fun areContentsTheSame(oldItem: AddItem, newItem: AddItem) = oldItem == newItem
            override fun areItemsTheSame(oldItem: AddItem, newItem: AddItem) = oldItem.codename == newItem.codename
        }
    }

}