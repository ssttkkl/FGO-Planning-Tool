package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter

import android.app.Activity
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import kotlinx.android.synthetic.main.item_servantlist_filter.view.*

class ServantListMultipleSelectAdapter<T>(val activity: Activity, private val getText: ((item: T) -> String)) : RecyclerView.Adapter<ServantListMultipleSelectAdapter<T>.ViewHolder>() {
    var data: List<T> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(activity.layoutInflater.inflate(R.layout.item_servantlist_filter, parent, false)).apply {
                itemView.setOnClickListener {
                    if (isPositionSelected(adapterPosition))
                        setPositionUnselected(adapterPosition)
                    else
                        setPositionSelected(adapterPosition)
                }
            }

    private val selectedColor = ResourcesCompat.getColor(activity.resources, R.color.colorAccent, null)
    private val unselectedColor = ResourcesCompat.getColor(activity.resources, android.R.color.tertiary_text_light, null)

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.itemView.textView.apply {
            text = getText.invoke(data[pos])
            if (isPositionSelected(pos)) {
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

    private val selection = HashMap<Int, Boolean>()

    val selectedPosition
        get() = selection.filterValues { it }.keys

    fun isPositionSelected(pos: Int) = selection[pos] ?: false

    fun setPositionSelected(pos: Int) {
        if (!isPositionSelected(pos)) {
            selection[pos] = true
            notifyItemChanged(pos)
            onItemSelectedListener?.invoke(pos)
        }
    }

    fun setPositionUnselected(pos: Int) {
        if (isPositionSelected(pos)) {
            selection[pos] = false
            notifyItemChanged(pos)
            onItemUnselectedListener?.invoke(pos)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}