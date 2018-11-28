package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.content.Context
import androidx.recyclerview.widget.*
import android.view.*
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.databinding.ItemServantinfoItemlistBinding
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter

class ServantInfoItemListRecViewAdapter(val context: Context) : ListAdapter<Item, ServantInfoItemListRecViewAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Item>() {
    override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
    override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.codename == newItem.codename
}) {
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
}