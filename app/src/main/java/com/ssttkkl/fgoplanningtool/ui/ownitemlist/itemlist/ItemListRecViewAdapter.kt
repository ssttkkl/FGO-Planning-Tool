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
    val data: List<Item> = ArrayList()

    fun setNewData(newData: List<Item>) {
        if (newData.size == data.size && newData.indices.all { newData[it].codename == data[it].codename }) {
            newData.indices.forEach {
                if (newData[it] != data[it]) {
                    (data as MutableList<Item>)[it] = newData[it]
                    notifyItemChanged(it)
                }
            }
        } else {
            (data as MutableList<Item>).apply {
                clear()
                addAll(newData)
            }
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
                name_textView.text = item.descriptor?.localizedName ?: item.codename
                own_textView.text = item.count.toStringWithSplitter()
                Glide.with(context).load(item.descriptor?.imgFile).error(R.drawable.item_placeholder).into(avatar_imageView)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}