package com.ssttkkl.fgoplanningtool.ui.eventlist.chooseevent

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemChooseeventBinding
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor

class ChooseEventListRecViewAdapter(val context: Context,
                                    private val lifecycleOwner: LifecycleOwner,
                                    private val viewModel: ChooseEventFragmentViewModel)
    : ListAdapter<EventDescriptor, ChooseEventListRecViewAdapter.ViewModel>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        return ViewModel(ItemChooseeventBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewModel(val binding: ItemChooseeventBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setLifecycleOwner(lifecycleOwner)
            binding.viewModel = viewModel
        }

        fun bind(descriptor: EventDescriptor) {
            binding.descriptor = descriptor
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<EventDescriptor>() {
            override fun areItemsTheSame(oldItem: EventDescriptor, newItem: EventDescriptor) = oldItem.codename == newItem.codename
            override fun areContentsTheSame(oldItem: EventDescriptor, newItem: EventDescriptor) = oldItem == newItem
        }
    }
}