package com.ssttkkl.fgoplanningtool.ui.editplan

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.widget.Spinner
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.servant.Dress

object EditPlanDetailBindingAdapter {
    @BindingAdapter("selection")
    @JvmStatic
    fun setSelection(spinner: Spinner, selection: ObservablePlan.SkillLevel) {
        spinner.setSelection(selection.ordinal)
    }

    @InverseBindingAdapter(attribute = "selection", event = "onItemSelected")
    @JvmStatic
    fun getSelection(spinner: Spinner): ObservablePlan.SkillLevel {
        return ObservablePlan.SkillLevel.values()[spinner.selectedItemPosition]
    }

    @BindingAdapter("data")
    @JvmStatic
    fun setData(recView: RecyclerView, items: List<Dress>) {
        (recView.adapter as EditPlanDressListRecViewAdapter).data = items
    }

    @BindingAdapter("plan")
    @JvmStatic
    fun setPlan(pager: ViewPager, plan: Plan?) {
        (pager.adapter as EditPlanFragmentPagerAdapter).plan = plan
    }
}