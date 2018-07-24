package com.ssttkkl.fgoplanningtool.ui.editplan.container

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.servant.Dress
import kotlinx.android.synthetic.main.item_editplan_dress.view.*
import java.util.concurrent.ConcurrentHashMap

class EditPlanDressListRecViewAdapter(val context: Context) : RecyclerView.Adapter<EditPlanDressListRecViewAdapter.ViewHolder>() {
    var data: List<Dress> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    private val isPositionChecked = ConcurrentHashMap<Int, Boolean>()

    val checkedPositions: Set<Int>
        get() = isPositionChecked.filterValues { it }.keys

    fun setChecked(checked: Set<Int>) {
        if (checked == checkedPositions)
            return
        isPositionChecked.clear()
        checked.forEach {
            isPositionChecked[it] = true
        }
        notifyDataSetChanged()
    }

    private var listener: ((pos: Int, isChecked: Boolean) -> Unit)? = null

    fun setOnCheckedChangeListener(newListener: ((pos: Int, isChecked: Boolean) -> Unit)?) {
        listener = newListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_editplan_dress, parent, false)).apply {
            itemView.checkBox.setOnCheckedChangeListener { _, isChecked ->
                isPositionChecked[adapterPosition] = isChecked
                listener?.invoke(adapterPosition, isChecked)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.itemView.checkBox.apply {
            text = data[pos].localizedName
            isChecked = isPositionChecked[pos] ?: false
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}