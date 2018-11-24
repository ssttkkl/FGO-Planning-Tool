package com.ssttkkl.fgoplanningtool.ui.confirmchangeplan

import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

data class DeductItem(val codename: String,
                      val require: Long,
                      val own: Long,
                      val checked: Boolean) {
    val descriptor
        get() = ResourcesProvider.instance.itemDescriptors[codename]

    val delta
        get() = own - require
}