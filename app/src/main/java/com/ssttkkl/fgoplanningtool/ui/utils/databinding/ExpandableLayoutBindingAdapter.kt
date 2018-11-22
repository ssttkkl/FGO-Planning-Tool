package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import net.cachapa.expandablelayout.ExpandableLayout

object ExpandableLayoutBindingAdapter {
    @BindingAdapter("attrExpandedChanged")
    @JvmStatic
    fun setAttrExpandedChanged(expandableLayout: ExpandableLayout, listener: InverseBindingListener) {
        expandableLayout.setOnExpansionUpdateListener { _, _ -> listener.onChange() }
    }

    @InverseBindingAdapter(attribute = "app:el_expanded", event = "attrExpandedChanged")
    @JvmStatic
    fun getExpanded(expandableLayout: ExpandableLayout): Boolean {
        return expandableLayout.isExpanded
    }

    @BindingAdapter("app:el_expanded")
    @JvmStatic
    fun setExpanded(expandableLayout: ExpandableLayout, value: Boolean) {
        expandableLayout.isExpanded = value
    }
}