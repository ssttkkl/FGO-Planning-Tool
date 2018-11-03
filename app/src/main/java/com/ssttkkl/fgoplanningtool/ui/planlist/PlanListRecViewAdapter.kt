package com.ssttkkl.fgoplanningtool.ui.planlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.ItemPlanlistBinding
import com.ssttkkl.fgoplanningtool.ui.utils.changeDataSetSmoothly
import com.ssttkkl.fgoplanningtool.ui.utils.databinding.MultipleSelectableAdapter

class PlanListRecViewAdapter(val context: Context,
                             private val viewModel: PlanListFragmentViewModel) : RecyclerView.Adapter<PlanListRecViewAdapter.ViewHolder>(),
        MultipleSelectableAdapter {
    private val _data = ArrayList<Plan>()

    var data: List<Plan>
        get() = _data
        set(value) {
            selection = selection.filter { it < value.size }.toSet()
            changeDataSetSmoothly(_data, value) { it.servantId }
        }

    private var onSelectionChangedListener: ((selection: Set<Int>) -> Unit)? = null

    override fun setOnSelectionChangedListener(newListener: ((selection: Set<Int>) -> Unit)?) {
        onSelectionChangedListener = newListener
    }

    override var selection: Set<Int> = setOf()
        set(value) {
            if (field == value)
                return
            val old = field
            field = value
            old.forEach {
                if (!value.contains(it))
                    notifyItemChanged(it)
            }
            value.forEach {
                if (!old.contains(it))
                    notifyItemChanged(it)
            }
            onSelectionChangedListener?.invoke(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemPlanlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.binding.plan = data[pos]
        holder.binding.selected = selection.contains(pos)
        holder.binding.card.setOnClickListener { viewModel.onPlanClick(holder.binding.plan as Plan) }
        holder.binding.card.setOnLongClickListener { viewModel.onPlanLongClick(holder.binding.plan as Plan) }
    }

    inner class ViewHolder(val binding: ItemPlanlistBinding) : RecyclerView.ViewHolder(binding.root)
}