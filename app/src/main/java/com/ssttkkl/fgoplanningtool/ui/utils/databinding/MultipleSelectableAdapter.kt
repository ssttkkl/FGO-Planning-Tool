package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView

interface MultipleSelectableAdapter {
    var selection: Set<Int>

    fun setOnSelectionChangedListener(newListener: ((selection: Set<Int>) -> Unit)?)
}

object MultipleSelectableAdapterBindingAdapter {
    @BindingAdapter("onSelectionChanged")
    @JvmStatic
    fun setOnSelectionChanged(recView: androidx.recyclerview.widget.RecyclerView, listener: InverseBindingListener) {
        (recView.adapter as? MultipleSelectableAdapter)?.setOnSelectionChangedListener {
            listener.onChange()
        }
    }

    @BindingAdapter("selection")
    @JvmStatic
    fun setSelection(recView: androidx.recyclerview.widget.RecyclerView, selection: Set<Int>) {
        (recView.adapter as? MultipleSelectableAdapter)?.selection = selection
    }

    @InverseBindingAdapter(attribute = "selection", event = "onSelectionChanged")
    @JvmStatic
    fun getSelection(recView: androidx.recyclerview.widget.RecyclerView): Set<Int> {
        return (recView.adapter as? MultipleSelectableAdapter)?.selection ?: setOf()
    }
}