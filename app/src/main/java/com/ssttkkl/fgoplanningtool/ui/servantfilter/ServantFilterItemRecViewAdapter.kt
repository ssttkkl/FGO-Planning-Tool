package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemServantfilterItemBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor

class ServantFilterItemRecViewAdapter(val context: Context,
                                      private val lifecycleOwner: LifecycleOwner,
                                      private val viewModel: ServantFilterFragmentViewModel) : ListAdapter<ItemDescriptor, ServantFilterItemRecViewAdapter.ViewHolder>(ServantFilterItemRecViewAdapter.Differ) {
    companion object Differ : DiffUtil.ItemCallback<ItemDescriptor>() {
        override fun areContentsTheSame(oldItem: ItemDescriptor, newItem: ItemDescriptor) = oldItem == newItem
        override fun areItemsTheSame(oldItem: ItemDescriptor, newItem: ItemDescriptor) = oldItem.codename == newItem.codename
    }

    init {
        viewModel.items.observe(lifecycleOwner, Observer { codenames ->
            submitList(codenames?.sortedBy { it.rank } ?: listOf())
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemServantfilterItemBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ItemServantfilterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setLifecycleOwner(lifecycleOwner)
            binding.viewModel = viewModel
        }

        fun bind(descriptor: ItemDescriptor) {
            binding.itemDescriptor = descriptor
        }
    }
}