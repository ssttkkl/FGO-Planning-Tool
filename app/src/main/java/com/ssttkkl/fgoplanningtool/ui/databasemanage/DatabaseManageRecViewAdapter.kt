package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.BR
import com.ssttkkl.fgoplanningtool.databinding.ItemDatabasemanageBinding
import com.ssttkkl.fgoplanningtool.databinding.ItemDatabasemanageEditmodeBinding

class DatabaseManageRecViewAdapter(val context: Context,
                                   val lifecycleOwner: LifecycleOwner,
                                   val viewModel: DatabaseManageFragmentViewModel)
    : ListAdapter<EditableDatabaseDescriptor, DatabaseManageRecViewAdapter.ViewHolder>(EditableDatabaseDescriptor.Differ) {
    init {
        viewModel.data.observe(lifecycleOwner, Observer { submitList(it) })
    }

    override fun getItemViewType(pos: Int) = if (getItem(pos).editing)
        ITEM_TYPE_EDIT
    else
        ITEM_TYPE_NORMAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ITEM_TYPE_NORMAL -> ViewHolder(ItemDatabasemanageBinding.inflate(LayoutInflater.from(context), parent, false))
        else -> ViewHolder(ItemDatabasemanageEditmodeBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setVariable(BR.viewModel, viewModel)
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(item: EditableDatabaseDescriptor) {
            binding.setVariable(BR.descriptor, item.databaseDescriptor)
        }
    }

    companion object {
        private const val ITEM_TYPE_NORMAL = 1
        private const val ITEM_TYPE_EDIT = 2
    }
}