package com.ssttkkl.fgoplanningtool.ui.planlist

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.data.plan.Plan

object PlanListRecViewBindingAdapter {
    @BindingAdapter("data")
    @JvmStatic
    fun setData(recView: RecyclerView, items: List<Plan>) {
        (recView.adapter as PlanListRecViewAdapter).data = items
    }
}