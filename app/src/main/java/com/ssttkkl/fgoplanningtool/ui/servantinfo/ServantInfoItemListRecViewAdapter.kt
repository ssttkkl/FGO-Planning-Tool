package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter
import kotlinx.android.synthetic.main.item_servantinfo_itemlist.view.*

class ServantInfoItemListRecViewAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ServantInfoItemListRecViewAdapter.ViewHolder>() {
    var data: List<Item> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    private var listener: ((pos: Int, item: Item) -> Unit)? = null

    fun setOnItemClickListener(newListener: ((pos: Int, item: Item) -> Unit)?) {
        listener = newListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_servantinfo_itemlist, parent, false)).apply {
                itemView.setOnClickListener { listener?.invoke(adapterPosition, data[adapterPosition]) }
            }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            name_textView.text = item.descriptor?.localizedName ?: item.codename
            count_textView.text = item.count.toStringWithSplitter()
            Glide.with(context).load(item.descriptor?.imgFile).into(imageView)
        }
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}