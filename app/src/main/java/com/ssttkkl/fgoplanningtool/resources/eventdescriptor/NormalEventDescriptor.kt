package com.ssttkkl.fgoplanningtool.resources.eventdescriptor

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import org.joda.time.DateTime
import java.util.*

data class NormalEventDescriptor(override val codename: String,
                                 override val jaName: String,
                                 override val zhName: String,
                                 override val enName: String,
                                 override val date: DateTime,
                                 override val isRerun: Boolean,
                                 override val storyItems: Collection<Item>,
                                 override val storyItemsIfParticipated: Collection<Item>,
                                 override val storyItemsIfNotParticipated: Collection<Item>,
                                 override val shopItems: Collection<Item>) : EventDescriptor {
    override val localizedName: String
        get() = when (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
            "ja" -> jaName
            "en" -> enName
            else -> zhName
        }
}