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

    @BindingAdapter(value = ["app:el_expanded", "enableExpansionAnimation"], requireAll = false)
    @JvmStatic
    fun setExpanded(expandableLayout: ExpandableLayout, expanded: Boolean, enableExpansionAnimation: Boolean?) {
        expandableLayout.setExpanded(expanded, enableExpansionAnimation != false)
    }
}