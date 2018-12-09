package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import com.ssttkkl.fgoplanningtool.data.item.Item

data class CheckableItem(val item: Item,
                         val checked: Boolean,
                         val count: Long)

fun Collection<CheckableItem>.sorted(): List<CheckableItem> {
    return sortedBy { it.item.descriptor?.rank }
}