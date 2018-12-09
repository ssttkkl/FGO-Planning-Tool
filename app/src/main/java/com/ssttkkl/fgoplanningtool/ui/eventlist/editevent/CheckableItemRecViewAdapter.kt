package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemEditeventCheckableitemBinding

class CheckableItemRecViewAdapter(val context: Context,
                                  private val lifecycleOwner: LifecycleOwner,
                                  private val viewModel: EditEventBaseViewModel)
    : ListAdapter<CheckableItem, CheckableItemRecViewAdapter.ViewModel>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        return ViewModel(ItemEditeventCheckableitemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewModel(val binding: ItemEditeventCheckableitemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setLifecycleOwner(lifecycleOwner)
            binding.viewModel = viewModel
        }

        fun bind(item: CheckableItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<CheckableItem>() {
            override fun areItemsTheSame(oldItem: CheckableItem, newItem: CheckableItem) = oldItem.item.codename == newItem.item.codename
            override fun areContentsTheSame(oldItem: CheckableItem, newItem: CheckableItem) = oldItem == newItem
        }
    }
}