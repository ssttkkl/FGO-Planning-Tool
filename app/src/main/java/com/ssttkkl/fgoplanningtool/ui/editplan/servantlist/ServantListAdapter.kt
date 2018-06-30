package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.PlansRepository
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import kotlinx.android.synthetic.main.item_servantlist.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking

class ServantListAdapter(val context: Context) : RecyclerView.Adapter<ServantListAdapter.ViewHolder>() {
    private val exist = runBlocking(CommonPool) {
        PlansRepository.getAll().mapTo(HashSet()) { it.servantId }
    }

    var data: List<Servant> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_servantlist, parent, false)).apply {
                itemView.setOnClickListener {
                    if (!exist.contains(data[adapterPosition].id))
                        onItemClickListener?.invoke(data[adapterPosition].id)
                }
            }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            name_textView.text = item.localizedName
            class_textView.text = item.theClass.name
            Glide.with(context).load(item.avatarUri).into(avatar_imageView)

            alpha = if (exist.contains(item.id)) disabledAlpha else enabledAlpha
        }
    }

    private var onItemClickListener: ((servantId: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: ((servantId: Int) -> Unit)?) {
        onItemClickListener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val disabledAlpha = 0.4f
        private const val enabledAlpha = 1f
    }
}