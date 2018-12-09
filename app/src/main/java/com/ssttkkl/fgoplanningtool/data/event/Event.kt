package com.ssttkkl.fgoplanningtool.data.event

import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor

interface Event : Parcelable {
    val codename: String
    val checkedShopItems: Collection<Item>
    val rerunAndParticipated: Boolean
    val descriptor: EventDescriptor?
    val storyItems: Collection<Item>
    val items: Collection<Item>
}
