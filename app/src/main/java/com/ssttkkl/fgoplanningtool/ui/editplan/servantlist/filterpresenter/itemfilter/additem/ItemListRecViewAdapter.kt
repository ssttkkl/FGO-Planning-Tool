package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.itemfilter.additem

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.itemfilter.ItemEntity
import kotlinx.android.synthetic.main.item_servantlist_additem_itemlist.view.*

class ItemListRecViewAdapter(val context: Context) : RecyclerView.Adapter<ItemListRecViewAdapter.ViewHolder>() {
    var data: List<ItemEntity> = listOf()
        private set

    fun setNewData(newData: Collection<ItemDescriptor>) {
        data = newData.map { ItemEntity(it) }.sortedBy { it.name }
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((codename: String) -> Unit)? = null

    fun setOnItemClickListener(listener: ((codename: String) -> Unit)?) {
        onItemClickListener = listener
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_servantlist_additem_itemlist, parent, false)).apply {
                itemView.setOnClickListener { onItemClickListener?.invoke(data[adapterPosition].codename) }
            }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            Glide.with(context).load(item.imgFile).into(imageView)
            name_textView.text = item.name
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}