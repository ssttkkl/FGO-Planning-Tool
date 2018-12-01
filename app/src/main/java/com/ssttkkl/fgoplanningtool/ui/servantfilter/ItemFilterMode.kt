package com.ssttkkl.fgoplanningtool.ui.servantfilter

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.Localizable

enum class ItemFilterMode : Localizable {
    And, Or;

    override val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.mode_item_servantfilter)[ordinal]
}