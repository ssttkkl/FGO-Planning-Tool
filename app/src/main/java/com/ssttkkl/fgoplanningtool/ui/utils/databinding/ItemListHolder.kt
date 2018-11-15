package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.data.item.Item

interface ItemListHolder {
    var data: List<Item>
}

object ItemListBindingAdapter {
    @BindingAdapter("data")
    @JvmStatic
    fun setData(recView: androidx.recyclerview.widget.RecyclerView, data: List<Item>) {
        (recView.adapter as? ItemListHolder)?.data = data
    }
}