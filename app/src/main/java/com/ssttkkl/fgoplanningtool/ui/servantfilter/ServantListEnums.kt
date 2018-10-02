package com.ssttkkl.fgoplanningtool.ui.servantfilter

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class OrderBy {
    ID, Class, Star;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.orderBy_servantfilter)[ordinal]
}

enum class Order {
    Increase, Decrease;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.order_servantfilter)[ordinal]
}

enum class ItemFilterMode {
    And, Or;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.mode_item_servantfilter)[ordinal]
}