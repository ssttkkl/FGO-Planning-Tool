package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.support.v7.widget.RecyclerView

interface MultipleSelectableAdapter {
    var selection: Set<Int>

    fun setOnSelectionChangedListener(newListener: ((selection: Set<Int>) -> Unit)?)
}

object MultipleSelectableAdapterBindingAdapter {
    @BindingAdapter("onSelectionChanged")
    @JvmStatic
    fun setOnSelectionChanged(recView: RecyclerView, listener: InverseBindingListener) {
        (recView.adapter as? MultipleSelectableAdapter)?.setOnSelectionChangedListener {
            listener.onChange()
        }
    }

    @BindingAdapter("selection")
    @JvmStatic
    fun setSelection(recView: RecyclerView, selection: Set<Int>) {
        (recView.adapter as? MultipleSelectableAdapter)?.selection = selection
    }

    @InverseBindingAdapter(attribute = "selection", event = "onSelectionChanged")
    @JvmStatic
    fun getSelection(recView: RecyclerView): Set<Int> {
        return (recView.adapter as? MultipleSelectableAdapter)?.selection ?: setOf()
    }
}