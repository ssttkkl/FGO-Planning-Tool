package com.ssttkkl.fgoplanningtool.ui.eventlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.databinding.ItemEventlistBinding

class EventListFragmentRecViewAdapter(val context: Context,
                                      private val lifecycleOwner: LifecycleOwner,
                                      private val viewModel: EventListFragmentViewModel)
    : ListAdapter<Event, EventListFragmentRecViewAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(ItemEventlistBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemEventlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setLifecycleOwner(lifecycleOwner)
            binding.viewModel = viewModel
        }

        fun bind(event: Event) {
            binding.event = event
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.codename == newItem.codename
            override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
        }
    }
}