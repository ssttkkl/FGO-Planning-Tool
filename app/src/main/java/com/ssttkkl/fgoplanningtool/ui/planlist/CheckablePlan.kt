package com.ssttkkl.fgoplanningtool.ui.planlist

import androidx.recyclerview.widget.DiffUtil
import com.ssttkkl.fgoplanningtool.data.plan.Plan

data class CheckablePlan(val plan: Plan, val checked: Boolean = false) {
    companion object Differ : DiffUtil.ItemCallback<CheckablePlan>() {
        override fun areContentsTheSame(oldItem: CheckablePlan, newItem: CheckablePlan) = oldItem == newItem
        override fun areItemsTheSame(oldItem: CheckablePlan, newItem: CheckablePlan) = oldItem.plan.servantId == newItem.plan.servantId
    }
}