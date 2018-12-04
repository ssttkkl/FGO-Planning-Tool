package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.databinding.ItemServantinfoItemlistBinding

class ServantInfoItemListRecViewAdapter(val context: Context)
    : ListAdapter<Item, ServantInfoItemListRecViewAdapter.ViewHolder>(diffCallback) {

    private var listener: ((Item) -> Unit)? = null

    fun setOnItemClickListener(newListener: ((Item) -> Unit)?) {
        listener = newListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemServantinfoItemlistBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ItemServantinfoItemlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.listener = View.OnClickListener {
                listener?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(item: Item) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
            override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.codename == newItem.codename
        }
    }
}