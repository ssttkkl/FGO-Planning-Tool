package com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent

import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

data class AddItem(val codename: String,
                      val before: Long,
                      val after: Long) {
    val descriptor
        get() = ResourcesProvider.instance.itemDescriptors[codename]
}