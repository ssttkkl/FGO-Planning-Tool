package com.ssttkkl.fgoplanningtool.ui.planlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.utils.RecViewAdapterDataSetChanger
import kotlinx.android.synthetic.main.item_planlist.view.*
import java.util.concurrent.ConcurrentHashMap

class PlanListRecViewAdapter(val context: Context) : RecyclerView.Adapter<PlanListRecViewAdapter.ViewHolder>() {
    val data: List<Plan> = ArrayList()

    fun setNewData(newData: List<Plan>) {
        synchronized(data) {
            isInSelectMode = false
            RecViewAdapterDataSetChanger.perform(this, data as ArrayList<Plan>, newData) { it.servantId }
        }
    }

    // callback start
    interface Callback {
        fun onItemClickedInNormalMode(pos: Int)
        fun onSelectModeEnabled()
        fun onSelectModeDisabled()
        fun onSelectStateChanged(pos: Int, selected: Boolean)
    }

    private var callback: Callback? = null

    fun setCallback(newCallback: Callback?) {
        callback = newCallback
    }

    // selection start
    private val selection = ConcurrentHashMap<Int, Boolean>()

    val selectedPositions: Set<Int>
        get() = selection.filterValues { it }.keys

    fun isSelected(pos: Int) = isInSelectMode && (selection[pos] ?: false)

    fun select(pos: Int) {
        if (!isInSelectMode)
            isInSelectMode = true
        if (!isSelected(pos)) {
            selection[pos] = true
            notifyItemChanged(pos)
            callback?.onSelectStateChanged(pos, true)
        }
    }

    fun deselect(pos: Int) {
        if (!isInSelectMode)
            isInSelectMode = true
        if (isSelected(pos)) {
            selection[pos] = false
            notifyItemChanged(pos)
            callback?.onSelectStateChanged(pos, false)
        }
    }

    val isAllSelected
        get() = (0 until itemCount).all { isSelected(it) }

    val isAnySelected
        get() = (0 until itemCount).any { isSelected(it) }

    fun selectAll() {
        for (pos in 0 until itemCount) {
            if (!isSelected(pos))
                select(pos)
        }
    }

    fun deselectAll() {
        for (pos in 0 until itemCount) {
            if (isSelected(pos))
                deselect(pos)
        }
    }

    var isInSelectMode: Boolean = false
        set(value) {
            val old = field
            field = value
            if (!old && value) {
                callback?.onSelectModeEnabled()
            } else if (old && !value) {
                val oldSelectedPositions = selectedPositions
                selection.clear()
                oldSelectedPositions.forEach { notifyItemChanged(it) }
                callback?.onSelectModeDisabled()
            }
        }

    private fun onPositionClicked(pos: Int) {
        if (isInSelectMode) {
            if (isSelected(pos))
                deselect(pos)
            else
                select(pos)
        } else {
            callback?.onItemClickedInNormalMode(pos)
        }
    }

    private fun onPositionLongClicked(pos: Int): Boolean {
        if (!isInSelectMode) {
            isInSelectMode = true
            select(pos)
            return true
        }
        return false
    }

    // core start
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_planlist, parent, false)).apply {
                itemView.apply {
                    card.setOnClickListener { onPositionClicked(adapterPosition) }
                    card.setOnLongClickListener { onPositionLongClicked(adapterPosition) }
                }
            }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val plan = data[pos]
        holder.itemView.apply {
            nowLevel_textView.text = plan.nowLevel.toString()
            planLevel_textView.text = plan.planLevel.toString()
            nowSkill1_textView.text = plan.nowSkill1.toString()
            planSkill1_textView.text = plan.planSkill1.toString()
            nowSkill2_textView.text = plan.nowSkill2.toString()
            planSkill2_textView.text = plan.planSkill2.toString()
            nowSkill3_textView.text = plan.nowSkill3.toString()
            planSkill3_textView.text = plan.planSkill3.toString()

            dress_imageView.visibility = if (plan.dress.isNotEmpty())
                View.VISIBLE
            else
                View.GONE

            selectedFlag_imageView.visibility = if (isInSelectMode && isSelected(pos))
                View.VISIBLE
            else
                View.INVISIBLE

            // if servant resources doesn't exist, show its servantId and avatar_placeholder instead
            name_textView.text = plan.servant?.localizedName ?: plan.servantId.toString()
            Glide.with(context).load(plan.servant?.avatarFile).error(R.drawable.avatar_placeholder).into(avatar_imageView)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}