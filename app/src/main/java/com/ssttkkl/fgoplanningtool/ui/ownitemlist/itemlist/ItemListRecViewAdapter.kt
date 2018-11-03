package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.databinding.ItemOwnitemlistBinding
import com.ssttkkl.fgoplanningtool.databinding.ItemOwnitemlistEditmodeBinding
import com.ssttkkl.fgoplanningtool.ui.utils.changeDataSetSmoothly

class ItemListRecViewAdapter(val context: Context,
                             lifecycleOwner: LifecycleOwner,
                             private val viewModel: ItemListFragmentViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data: List<Item> = ArrayList()
        set(value) {
            changeDataSetSmoothly(field as ArrayList<Item>, value) { it.codename }
        }

    private var inEditMode: Set<String> = setOf()
        set(value) {
            if (field == value)
                return
            val old = field
            field = value
            old.forEach { oldCodeName ->
                if (!value.contains(oldCodeName))
                    notifyItemChanged(data.indexOfFirst { it.codename == oldCodeName })
            }
            value.forEach { newCodeName ->
                if (!old.contains(newCodeName))
                    notifyItemChanged(data.indexOfFirst { it.codename == newCodeName })
            }
        }

    init {
        viewModel.data.observe(lifecycleOwner, Observer { data = it ?: listOf() })
        viewModel.inEditMode.observe(lifecycleOwner, Observer { inEditMode = it ?: setOf() })
    }

    override fun getItemViewType(position: Int): Int {
        return if (!inEditMode.contains(data[position].codename)) 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == 1)
        NormalViewHolder(ItemOwnitemlistBinding.inflate(LayoutInflater.from(context), parent, false))
    else
        EditModeViewHolder(ItemOwnitemlistEditmodeBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        if (holder is NormalViewHolder) {
            holder.binding.item = data[pos]
        } else if (holder is EditModeViewHolder) {
            holder.binding.item = data[pos]
        }
    }

    inner class NormalViewHolder(val binding: ItemOwnitemlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
        }
    }

    inner class EditModeViewHolder(val binding: ItemOwnitemlistEditmodeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
        }
    }
}