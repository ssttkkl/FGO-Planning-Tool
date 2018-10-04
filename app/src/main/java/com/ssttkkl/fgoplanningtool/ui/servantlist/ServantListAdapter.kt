package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import kotlinx.android.synthetic.main.item_servantlist.view.*

class ServantListAdapter(val context: Context,
                         private val hidden: Set<Int>) : RecyclerView.Adapter<ServantListAdapter.ViewHolder>() {
    var data: List<Servant> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    private var listener: ((servantId: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: ((servantId: Int) -> Unit)?) {
        this.listener = listener
    }

    var viewType: ViewType = ViewType.List
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return viewType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResID = if (viewType == ViewType.List.ordinal)
            R.layout.item_servantlist
        else
            R.layout.item_servantlist_grid
        return ViewHolder(LayoutInflater.from(context).inflate(layoutResID, parent, false)).apply {
            itemView.setOnClickListener {
                if (!hidden.contains(data[adapterPosition].id))
                    listener?.invoke(data[adapterPosition].id)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            if (viewType == ViewType.List) {
                name_textView.text = item.localizedName
                class_textView.text = item.theClass.name
            }
            Glide.with(context).load(item.avatarFile).into(avatar_imageView)

            alpha = if (hidden.contains(item.id)) disabledAlpha else enabledAlpha
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val disabledAlpha = 0.4f
        private const val enabledAlpha = 1f
    }
}