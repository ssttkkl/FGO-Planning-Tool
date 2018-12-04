package com.ssttkkl.fgoplanningtool.ui.planlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemPlanlistBinding

class PlanListRecViewAdapter(val context: Context,
                             val lifecycleOwner: LifecycleOwner,
                             private val viewModel: PlanListFragmentViewModel)
    : ListAdapter<CheckablePlan, PlanListRecViewAdapter.ViewHolder>(CheckablePlan.ItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemPlanlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ItemPlanlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(data: CheckablePlan) {
            binding.plan = data.plan
            binding.checked = data.checked
            binding.executePendingBindings()
        }
    }
}