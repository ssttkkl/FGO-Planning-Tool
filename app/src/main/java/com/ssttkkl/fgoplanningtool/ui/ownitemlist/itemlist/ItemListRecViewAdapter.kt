package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemOwnitemlistBinding
import com.ssttkkl.fgoplanningtool.databinding.ItemOwnitemlistEditmodeBinding

class ItemListRecViewAdapter(val context: Context,
                             private val lifecycleOwner: LifecycleOwner,
                             private val viewModel: ItemListFragmentViewModel) : ListAdapter<EditableItem, ItemListRecViewAdapter.ViewHolder>(EditableItem.Differ) {
    init {
        viewModel.data.observe(lifecycleOwner, Observer { submitList(it) })
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.editing == true) EDITING else NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == 1)
        NormalViewHolder(ItemOwnitemlistBinding.inflate(LayoutInflater.from(context), parent, false))
    else
        EditingViewHolder(ItemOwnitemlistEditmodeBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    abstract inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(data: EditableItem)
    }

    inner class NormalViewHolder(val binding: ItemOwnitemlistBinding) : ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        override fun bind(data: EditableItem) {
            binding.item = data.item
            binding.executePendingBindings()
        }
    }

    inner class EditingViewHolder(val binding: ItemOwnitemlistEditmodeBinding) : ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        override fun bind(data: EditableItem) {
            binding.item = data.item
            binding.executePendingBindings()
        }
    }

    companion object {
        private const val NORMAL = 1
        private const val EDITING = 2
    }
}