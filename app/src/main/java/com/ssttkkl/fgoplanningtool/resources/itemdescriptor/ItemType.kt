package com.ssttkkl.fgoplanningtool.resources.itemdescriptor

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class ItemType {
    General, Piece, Gem, Copper, Sliver, Golden, Event;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.itemType)[ordinal]
}