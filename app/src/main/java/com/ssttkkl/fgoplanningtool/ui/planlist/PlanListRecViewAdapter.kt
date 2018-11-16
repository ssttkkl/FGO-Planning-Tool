package com.ssttkkl.fgoplanningtool.ui.planlist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.OnRebindCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemPlanlistBinding

class PlanListRecViewAdapter(val context: Context,
                             lifecycleOwner: LifecycleOwner,
                             private val viewModel: PlanListFragmentViewModel) : ListAdapter<CheckablePlan, PlanListRecViewAdapter.ViewHolder>(CheckablePlan.Differ) {
    init {
        viewModel.data.observe(lifecycleOwner, Observer { submitList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemPlanlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ItemPlanlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
        }

        fun bind(data: CheckablePlan) {
            binding.apply {
                plan = data.plan
                checked = data.checked
                executePendingBindings()
            }
        }
    }
}