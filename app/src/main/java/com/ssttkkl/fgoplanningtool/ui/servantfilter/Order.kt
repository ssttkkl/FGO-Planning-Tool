package com.ssttkkl.fgoplanningtool.ui.servantfilter

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class Order {
    Increase, Decrease;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.order_servantfilter)[ordinal]
}