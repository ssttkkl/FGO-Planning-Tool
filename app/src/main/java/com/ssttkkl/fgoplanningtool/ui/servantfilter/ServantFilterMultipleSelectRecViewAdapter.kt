package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemServantfilterSelectBinding

class ServantFilterMultipleSelectRecViewAdapter<T>(val context: Context,
                                                   lifecycleOwner: LifecycleOwner,
                                                   val originItems: List<T>,
                                                   val selectedItems: MutableLiveData<Set<T>>,
                                                   private val textGetter: (T) -> String)
    : ListAdapter<Pair<T, Boolean>, ServantFilterMultipleSelectRecViewAdapter<T>.ViewHolder>(object : DiffUtil.ItemCallback<Pair<T, Boolean>>() {
    override fun areContentsTheSame(oldItem: Pair<T, Boolean>, newItem: Pair<T, Boolean>) = oldItem == newItem
    override fun areItemsTheSame(oldItem: Pair<T, Boolean>, newItem: Pair<T, Boolean>) = oldItem.first == newItem.first
}) {
    init {
        selectedItems.observe(lifecycleOwner, Observer { selectedItems ->
            submitList(originItems.map { Pair(it, selectedItems?.contains(it) == true) })
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ItemServantfilterSelectBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    fun isItemSelected(item: T): Boolean = selectedItems.value?.contains(item) == true

    fun setItemSelected(item: T, selected: Boolean) {
        if (selected)
            selectedItems.value = selectedItems.value?.plus(item) ?: setOf(item)
        else
            selectedItems.value = selectedItems.value?.minus(item)
    }

    inner class ViewHolder(val binding: ItemServantfilterSelectBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.callback = View.OnClickListener {
                val item = originItems[adapterPosition]
                setItemSelected(item, !isItemSelected(item))
            }
        }

        fun bind(item: Pair<T, Boolean>) {
            binding.text = textGetter(item.first)
            binding.selected = item.second
        }
    }
}