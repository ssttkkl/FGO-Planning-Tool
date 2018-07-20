package com.ssttkkl.fgoplanningtool.resources.servant

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class WayToGet {
    BeginnerGift,
    Resident,
    Limited,
    LimitedInStoryPool,
    EventGift;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.wayToGet)[ordinal]
}