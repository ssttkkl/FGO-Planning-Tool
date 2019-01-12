package com.ssttkkl.fgoplanningtool.resources.eventdescriptor

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.utils.Localizable
import java.util.*
import kotlin.collections.ArrayList

data class PointPool(val zhName: String,
                     val enName: String,
                     val jaName: String,
                     val items: Map<Long, Collection<Item>>) : Localizable {
    override val localizedName: String
        get() = when (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
            "ja" -> jaName
            "en" -> enName
            else -> zhName
        }

    fun getItems(point: Long): Collection<Item> {
        val map = HashMap<String, Long>()
        items.forEach { (requestPoint, items) ->
            if (point >= requestPoint) {
                items.forEach { (codename, count) ->
                    map[codename] = (map[codename] ?: 0) + count
                }
            }
        }
        return map.map { Item(it.key, it.value) }
    }

    fun getItemsWithRequestPoint(point: Long): Collection<Pair<Long, Item>> {
        val list = ArrayList<Pair<Long, Item>>()
        items.filterKeys { it <= point }.forEach { (key, value) ->
            list.addAll(value.map { Pair(key, it) })
        }
        return list.sortedBy { it.first }
    }
}