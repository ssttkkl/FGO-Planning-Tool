package com.ssttkkl.fgoplanningtool.ui.servantfilter.additem

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemServantfilterAdditemItemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor

class ItemListRecViewAdapter(val context: Context,
                             private val lifecycleOwner: LifecycleOwner,
                             private val viewModel: ItemListFragmentViewModel) : ListAdapter<ItemDescriptor, ItemListRecViewAdapter.ViewHolder>(ItemListRecViewAdapter.Differ) {
    companion object Differ : DiffUtil.ItemCallback<ItemDescriptor>() {
        override fun areContentsTheSame(oldItem: ItemDescriptor, newItem: ItemDescriptor) = oldItem == newItem
        override fun areItemsTheSame(oldItem: ItemDescriptor, newItem: ItemDescriptor) = oldItem.codename == newItem.codename
    }

    init {
        viewModel.data.observe(lifecycleOwner, Observer { submitList(it ?: listOf()) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemServantfilterAdditemItemlistBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ItemServantfilterAdditemItemlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(itemDescriptor: ItemDescriptor) {
            binding.itemDescriptor = itemDescriptor
        }
    }
}