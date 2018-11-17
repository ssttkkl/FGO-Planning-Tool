package com.ssttkkl.fgoplanningtool.resources.itemdescriptor

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class ItemType {
    General, Gem, Copper, Sliver, Golden, Piece, Event;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.itemType)[ordinal]
}