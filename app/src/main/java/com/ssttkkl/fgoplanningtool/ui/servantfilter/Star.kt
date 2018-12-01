package com.ssttkkl.fgoplanningtool.ui.servantfilter

import com.ssttkkl.fgoplanningtool.utils.Localizable

enum class Star : Localizable {
    One, Two, Three, Four, Five;

    val num
        get() = ordinal + 1

    override val localizedName: String
        get() = num.toString()
}