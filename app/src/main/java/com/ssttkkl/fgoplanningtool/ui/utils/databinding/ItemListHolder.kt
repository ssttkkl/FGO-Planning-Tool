package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.data.item.Item

interface ItemListHolder {
    var data: List<Item>
}

object ItemListBindingAdapter {
    @BindingAdapter("data")
    @JvmStatic
    fun setData(recView: RecyclerView, data: List<Item>) {
        (recView.adapter as? ItemListHolder)?.data = data
    }
}