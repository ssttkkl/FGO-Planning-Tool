package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.itemfilter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.android.synthetic.main.item_servantlist_item.view.*

class ItemFilterRecViewAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ItemFilterRecViewAdapter.ViewHolder>() {
    var data: List<ItemEntity> = arrayListOf()
        private set

    fun setNewData(newData: Collection<String>) {
        data = newData.map { ItemEntity(ResourcesProvider.instance.itemDescriptors[it]!!) }
        notifyDataSetChanged()
    }

    fun addItem(codename: String) {
        if (data.any { it.codename == codename })
            return
        (data as MutableList<ItemEntity>).add(ItemEntity(ResourcesProvider.instance.itemDescriptors[codename]!!))
        notifyItemInserted(data.size - 1)
    }

    fun removeItem(codename: String) {
        data.forEachIndexed { idx, it ->
            if (it.codename == codename) {
                (data as MutableList<ItemEntity>).removeAt(idx)
                notifyItemRemoved(idx)
                return
            }
        }
        throw Exception("item not found")
    }

    private var onRemoveActionListener: ((codename: String) -> Unit)? = null

    fun setOnRemoveActionListener(listener: ((codename: String) -> Unit)?) {
        onRemoveActionListener = listener
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_servantlist_item, parent, false)).apply {
                itemView.remove_button.setOnClickListener {
                    onRemoveActionListener?.invoke(data[adapterPosition].codename)
                }
            }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            name_textView.text = item.name
            Glide.with(context).load(item.imgFile).into(imageView)
        }
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}