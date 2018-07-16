package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter

import android.app.Activity
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import kotlinx.android.synthetic.main.item_servantlist_filter.view.*

class ServantListSingleSelectAdapter<T>(val activity: Activity, private val getText: ((item: T) -> String)) : RecyclerView.Adapter<ServantListSingleSelectAdapter<T>.ViewHolder>() {
    var data: List<T> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(activity.layoutInflater.inflate(R.layout.item_servantlist_filter, parent, false)).apply {
                itemView.setOnClickListener { setPositionSelected(adapterPosition) }
            }

    private val selectedColor = ResourcesCompat.getColor(activity.resources, R.color.colorAccent, null)
    private val unselectedColor = ResourcesCompat.getColor(activity.resources, android.R.color.tertiary_text_light, null)

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.itemView.textView.apply {
            text = getText.invoke(data[pos])
            if (selectedPosition == pos) {
                setTextColor(selectedColor)
                typeface = Typeface.DEFAULT_BOLD
            } else {
                setTextColor(unselectedColor)
                typeface = Typeface.DEFAULT
            }
        }
    }

    private var onItemSelectedListener: ((pos: Int) -> Unit)? = null

    fun setOnItemSelectedListener(listener: ((pos: Int) -> Unit)?) {
        onItemSelectedListener = listener
    }

    private var onItemUnselectedListener: ((pos: Int) -> Unit)? = null

    fun setOnItemUnselectedListener(listener: ((pos: Int) -> Unit)?) {
        onItemUnselectedListener = listener
    }

    var selectedPosition = -1
        private set

    fun setPositionSelected(pos: Int) {
        val old = selectedPosition
        selectedPosition = pos
        if (old >= 0) {
            notifyItemChanged(old)
            onItemUnselectedListener?.invoke(old)
        }
        if (pos >= 0) {
            notifyItemChanged(pos)
            onItemSelectedListener?.invoke(pos)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}