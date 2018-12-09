package com.ssttkkl.fgoplanningtool.resources.eventdescriptor

import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.utils.Localizable
import org.joda.time.DateTime
import java.util.*

interface EventDescriptor : Localizable {
    val codename: String
    val jaName: String
    val zhName: String
    val enName: String
    val date: DateTime
    val isRerun: Boolean
    val storyItems: Collection<Item>
    val storyItemsIfParticipated: Collection<Item>
    val storyItemsIfNotParticipated: Collection<Item>
    val shopItems: Collection<Item>
}