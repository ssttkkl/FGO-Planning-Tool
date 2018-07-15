package com.ssttkkl.fgoplanningtool.ui.costitemlist.requireservantlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter
import kotlinx.android.synthetic.main.item_costitemlist_requireservant.view.*

class RequireServantsListRecViewAdapter(val context: Context) : RecyclerView.Adapter<RequireServantsListRecViewAdapter.ViewHolder>() {
    var data: List<RequireServantsListEntity> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_costitemlist_requireservant, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            require_textView.text = item.require.toStringWithSplitter()
            Glide.with(context).load(item.avatarFile).error(R.drawable.avatar_placeholder).into(avatar_imageView)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}