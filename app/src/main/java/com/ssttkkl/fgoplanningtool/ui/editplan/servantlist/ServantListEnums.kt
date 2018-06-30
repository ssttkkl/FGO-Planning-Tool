package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class OrderBy {
    ID, Class, Star;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.orderBy_servantlist)[ordinal]
}

enum class Order {
    Increase, Decrease;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.order_servantlist)[ordinal]
}

enum class ItemFilterMode {
    And, Or;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.mode_item_servantlist)[ordinal]
}