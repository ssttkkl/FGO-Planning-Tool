package com.ssttkkl.fgoplanningtool.resources.eventdescriptor

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.utils.Localizable
import kotlin.math.min

data class Lottery(val zhName: String,
                   val enName: String,
                   val jaName: String,
                   val items: Map<IntRange, Collection<Item>>) : Localizable {
    override val localizedName: String
        get() = when (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
            "ja" -> jaName
            "en" -> enName
            else -> zhName
        }

    fun getItems(boxCount: Int): Collection<Item> {
        val map = HashMap<String, Long>()
        items.forEach { (range, items) ->
            if (range.first <= boxCount) {
                val times = min(range.endInclusive, boxCount) - range.first + 1
                items.forEach { (codename, count) ->
                    map[codename] = (map[codename] ?: 0) + count * times
                }
            }
        }
        return map.map { Item(it.key, it.value) }
    }
}