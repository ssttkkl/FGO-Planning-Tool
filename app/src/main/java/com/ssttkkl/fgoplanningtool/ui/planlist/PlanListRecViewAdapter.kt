package com.ssttkkl.fgoplanningtool.ui.planlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import kotlinx.android.synthetic.main.item_planlist.view.*

class PlanListRecViewAdapter(val context: Context) : RecyclerView.Adapter<PlanListRecViewAdapter.ViewHolder>() {
    val data: List<Plan> = ArrayList()

    fun setNewData(newData: List<Plan>) {
        isInSelectMode = false
        PlanListRecViewAdapterDataSetChanger.perform(this, data as ArrayList<Plan>, newData)
    }

    // callback start
    interface Callback {
        fun onItemClickedInNormalMode(pos: Int)
        fun onSelectModeEnabled()
        fun onSelectModeDisabled()
        fun onPositionSelectStateChanged(pos: Int, selected: Boolean)
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    // selection start
    private val selection = HashMap<Int, Boolean>()

    val selectedPositions: Set<Int>
        get() = selection.filterValues { it }.keys

    fun isPositionSelected(pos: Int) = isInSelectMode && (selection[pos] ?: false)

    fun setPositionSelected(pos: Int) {
        if (!isInSelectMode)
            throw Exception("You must be in select mode before you set a position selected/unselected.")
        if (!isPositionSelected(pos)) {
            selection[pos] = true
            notifyItemChanged(pos)
            callback?.onPositionSelectStateChanged(pos, true)
        }
    }

    fun setPositionUnselected(pos: Int) {
        if (!isInSelectMode)
            throw Exception("You must be in select mode before set position selected/unselected.")
        if (isPositionSelected(pos)) {
            selection[pos] = false
            notifyItemChanged(pos)
            callback?.onPositionSelectStateChanged(pos, false)
        }
    }

    val isAllPositionsSelected
        get() = (0 until itemCount).all { isPositionSelected(it) }

    val isAnyPositionSelected
        get() = (0 until itemCount).any { isPositionSelected(it) }

    fun selectAllPositions() {
        for (pos in 0 until itemCount) {
            if (!isPositionSelected(pos))
                setPositionSelected(pos)
        }
    }

    fun unselectAllPositions() {
        for (pos in 0 until itemCount) {
            if (isPositionSelected(pos))
                setPositionUnselected(pos)
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
            if (isPositionSelected(pos))
                setPositionUnselected(pos)
            else
                setPositionSelected(pos)
        } else {
            callback?.onItemClickedInNormalMode(pos)
        }
    }

    private fun onPositionLongClicked(pos: Int): Boolean {
        if (!isInSelectMode) {
            isInSelectMode = true
            setPositionSelected(pos)
            return true
        }
        return false
    }

    // core start
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_planlist, parent, false)).apply {
                itemView.card.apply {
                    setOnClickListener { onPositionClicked(adapterPosition) }
                    setOnLongClickListener { onPositionLongClicked(adapterPosition) }
                }
            }

    override fun getItemCount() = data.size

    private val levelPattern = context.getString(R.string.levelPattern_item_planlist)

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.itemView.apply {
            val plan = data[pos]
            name_textView.text = plan.servant.localizedName

            stage_textView.text = levelPattern.format(plan.nowStage, plan.planStage)
            skill1_textView.text = levelPattern.format(plan.nowSkill1, plan.planSkill1)
            skill2_textView.text = levelPattern.format(plan.nowSkill2, plan.planSkill2)
            skill3_textView.text = levelPattern.format(plan.nowSkill3, plan.planSkill3)

            Glide.with(context).load(plan.servant.avatarUri).into(avatar_imageView)
            selectedFlag_imageView.visibility = if (isInSelectMode && isPositionSelected(pos)) View.VISIBLE else View.INVISIBLE
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}