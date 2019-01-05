package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.point

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemEditeventPointitemBinding
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.EditEventFragmentViewModel

class PointItemRecViewAdapter(val context: Context,
                              private val lifecycleOwner: LifecycleOwner,
                              private val viewModel: EditEventFragmentViewModel)
    : ListAdapter<PointItem, PointItemRecViewAdapter.ViewHolder>(PointItemRecViewAdapter.diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemEditeventPointitemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemEditeventPointitemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setLifecycleOwner(lifecycleOwner)
            binding.viewModel = viewModel
        }

        fun bind(item: PointItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PointItem>() {
            override fun areItemsTheSame(oldItem: PointItem, newItem: PointItem) = oldItem.item.codename == newItem.item.codename
            override fun areContentsTheSame(oldItem: PointItem, newItem: PointItem) = oldItem == newItem
        }
    }
}