package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter
import kotlinx.android.synthetic.main.item_ownitemlist.view.*

class ItemListRecViewAdapter(val context: Context) : RecyclerView.Adapter<ItemListRecViewAdapter.ViewHolder>() {
    var data: List<ItemListEntity> = ArrayList()
        private set

    fun setNewData(newData: List<Item>) {
        val newEntities = newData.sortedBy { it.descriptor?.localizedName }.map {
            ItemListEntity(it.codename,
                    it.descriptor?.localizedName ?: it.codename,
                    it.count,
                    it.descriptor?.imgUri ?: "")
        }
        if (data is MutableList<ItemListEntity> &&
                newEntities.size == data.size &&
                newEntities.indices.all { newEntities[it].codename == data[it].codename }) {
            newEntities.indices.forEach {
                if (newEntities[it] != data[it]) {
                    (data as MutableList<ItemListEntity>)[it] = newEntities[it]
                    notifyItemChanged(it)
                }
            }
        } else {
            data = ArrayList(newEntities)
            notifyDataSetChanged()
        }
    }

    private var onItemClickListener: ((codename: String) -> Unit)? = null

    fun setOnButtonClickListener(listener: ((codename: String) -> Unit)?) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ownitemlist, parent, false)).apply {
                itemView.setOnClickListener {
                    onItemClickListener?.invoke(data[adapterPosition].codename)
                }
            }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.apply {
            itemView.apply {
                name_textView.text = item.name
                own_textView.text = item.own.toStringWithSplitter()
                Glide.with(context).load(item.imgUri).into(avatar_imageView)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}