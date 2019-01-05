package com.ssttkkl.fgoplanningtool.resources.eventdescriptor

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.utils.Localizable
import org.joda.time.DateTime

data class EventDescriptor(val codename: String,
                           val jaName: String,
                           val zhName: String,
                           val enName: String,
                           val date: DateTime,
                           val isRerun: Boolean,
                           val storyItems: Collection<Item>,
                           val storyItemsIfParticipated: Collection<Item>,
                           val storyItemsIfNotParticipated: Collection<Item>,
                           val shopItems: Collection<Item>,
                           val lotteries: Map<String, Lottery>,
                           val pointPools: Map<String, PointPool>) : Localizable {
    override val localizedName: String
        get() = when (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
            "ja" -> jaName
            "en" -> enName
            else -> zhName
        }
}