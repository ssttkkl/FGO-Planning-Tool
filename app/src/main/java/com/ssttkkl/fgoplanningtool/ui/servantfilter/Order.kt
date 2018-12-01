package com.ssttkkl.fgoplanningtool.ui.servantfilter

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.Localizable

enum class Order : Localizable {
    Increase, Decrease;

    override val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.order_servantfilter)[ordinal]
}