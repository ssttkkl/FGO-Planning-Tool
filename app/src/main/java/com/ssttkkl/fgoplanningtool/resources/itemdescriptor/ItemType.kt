package com.ssttkkl.fgoplanningtool.resources.itemdescriptor

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.Localizable

enum class ItemType : Localizable {
    General, Gem, Copper, Sliver, Golden, Piece, Event;

    override val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.itemType)[ordinal]
}