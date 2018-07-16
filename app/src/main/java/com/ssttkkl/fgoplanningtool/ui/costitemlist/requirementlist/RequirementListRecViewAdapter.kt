package com.ssttkkl.fgoplanningtool.ui.costitemlist.requirementlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter
import kotlinx.android.synthetic.main.item_requirementlist.view.*

class RequirementListRecViewAdapter(val context: Context) : RecyclerView.Adapter<RequirementListRecViewAdapter.ViewHolder>() {
    var data: List<RequirementListEntity> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_requirementlist, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            name_textView.text = item.name
            count_textView.text = item.require.toStringWithSplitter()
            Glide.with(context).load(item.avatarFile).error(R.drawable.avatar_placeholder).into(avatar_imageView)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}