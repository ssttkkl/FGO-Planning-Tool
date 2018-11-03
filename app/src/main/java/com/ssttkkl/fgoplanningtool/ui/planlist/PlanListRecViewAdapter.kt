package com.ssttkkl.fgoplanningtool.ui.planlist

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.ItemPlanlistBinding
import com.ssttkkl.fgoplanningtool.ui.utils.changeDataSetSmoothly

class PlanListRecViewAdapter(val context: Context,
                             lifecycleOwner: LifecycleOwner,
                             private val viewModel: PlanListFragmentViewModel) : RecyclerView.Adapter<PlanListRecViewAdapter.ViewHolder>() {
    private var data: List<Plan> = ArrayList()
        set(value) {
            selection = selection.filter { it < value.size }.toSet()
            changeDataSetSmoothly(field as ArrayList<Plan>, value) { it.servantId }
        }

    private var selection: Set<Int> = setOf()
        set(value) {
            if (field == value)
                return
            val old = field
            field = value
            old.forEach { oldServantID ->
                if (!value.contains(oldServantID))
                    notifyItemChanged(data.indexOfFirst { it.servantId == oldServantID })
            }
            value.forEach { newServantID ->
                if (!old.contains(newServantID))
                    notifyItemChanged(data.indexOfFirst { it.servantId == newServantID })
            }
        }

    init {
        viewModel.data.observe(lifecycleOwner, Observer { data = it ?: listOf() })
        viewModel.selectedServantIDs.observe(lifecycleOwner, Observer { selection = it ?: setOf() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemPlanlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.binding.plan = data[pos]
    }

    inner class ViewHolder(val binding: ItemPlanlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
        }
    }
}