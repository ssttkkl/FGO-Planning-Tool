package com.ssttkkl.fgoplanningtool.ui.requirementlist

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter
import kotlinx.android.synthetic.main.item_requirementlist.view.*

class RequirementListRecViewAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<RequirementListRecViewAdapter.ViewHolder>() {
    var data: List<RequirementListEntity> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    private var listener: ((pos: Int, item: RequirementListEntity) -> Unit)? = null

    fun setOnItemClickListener(newListener: ((pos: Int, item: RequirementListEntity) -> Unit)?) {
        listener = newListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_requirementlist, parent, false)).apply {
                itemView.setOnClickListener { listener?.invoke(adapterPosition, data[adapterPosition]) }
            }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            name_textView.text = item.name
            count_textView.text = item.requirement.toStringWithSplitter()
            Glide.with(context).load(item.avatarFile).error(R.drawable.avatar_placeholder).into(avatar_imageView)
        }
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}