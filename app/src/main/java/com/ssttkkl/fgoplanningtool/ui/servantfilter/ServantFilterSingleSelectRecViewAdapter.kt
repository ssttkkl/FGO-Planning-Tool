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

class ServantFilterSingleSelectRecViewAdapter<T>(val context: Context,
                                                 lifecycleOwner: LifecycleOwner,
                                                 val originItems: List<T>,
                                                 val selectedItem: MutableLiveData<T>,
                                                 private val textGetter: (T) -> String)
    : ListAdapter<Pair<T, Boolean>, ServantFilterSingleSelectRecViewAdapter<T>.ViewHolder>(object : DiffUtil.ItemCallback<Pair<T, Boolean>>() {
    override fun areContentsTheSame(oldItem: Pair<T, Boolean>, newItem: Pair<T, Boolean>) = oldItem == newItem
    override fun areItemsTheSame(oldItem: Pair<T, Boolean>, newItem: Pair<T, Boolean>) = oldItem.first == newItem.first
}) {
    init {
        selectedItem.observe(lifecycleOwner, Observer { selectedItem ->
            submitList(originItems.map { Pair(it, selectedItem == it) })
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemServantfilterSelectBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    fun setItemSelected(item: T) {
        selectedItem.value = item
    }

    inner class ViewHolder(val binding: ItemServantfilterSelectBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.callback = View.OnClickListener { setItemSelected(originItems[adapterPosition]) }
        }

        fun bind(item: Pair<T, Boolean>) {
            binding.text = textGetter(item.first)
            binding.selected = item.second
        }
    }
}