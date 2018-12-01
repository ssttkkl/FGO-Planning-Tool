package com.ssttkkl.fgoplanningtool.resources.servant

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.Localizable

enum class WayToGet : Localizable {
    BeginnerGift,
    Resident,
    Limited,
    LimitedInStoryPool,
    EventGift;

    override val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.wayToGet)[ordinal]
}