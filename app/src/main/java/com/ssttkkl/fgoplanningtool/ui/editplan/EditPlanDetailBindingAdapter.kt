package com.ssttkkl.fgoplanningtool.ui.editplan

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.RecyclerView
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
    fun setData(recView: androidx.recyclerview.widget.RecyclerView, items: List<Dress>) {
        (recView.adapter as EditPlanDressListRecViewAdapter).data = items
    }

    @BindingAdapter("plan")
    @JvmStatic
    fun setPlan(pager: androidx.viewpager.widget.ViewPager, plan: Plan?) {
        (pager.adapter as EditPlanFragmentPagerAdapter).plan = plan
    }
}