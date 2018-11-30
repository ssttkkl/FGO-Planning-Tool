package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter

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
}