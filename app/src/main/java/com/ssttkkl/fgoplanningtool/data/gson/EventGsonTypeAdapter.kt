package com.ssttkkl.fgoplanningtool.data.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.LevelValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

class EventGsonTypeAdapter : TypeAdapter<Event>() {
    private val itemCollectionAdapter = ItemCollectionGsonTypeAdapter()

    override fun write(writer: JsonWriter, it: Event) {
        writer.beginObject()

        writer.name(NAME_CODENAME)
        writer.value(it.codename)

        writer.name(NAME_RERUN_AND_PARTICIPATED)
        writer.value(it.rerunAndParticipated)

        writer.name(NAME_CHECKED_SHOP_ITEMS)
        itemCollectionAdapter.write(writer, it.checkedShopItems)

        writer.name(NAME_LOTTERY_BOX_COUNT)
        writer.beginObject()
        it.lotteryBoxCount.forEach {
            writer.name(it.key)
            writer.value(it.value)
        }
        writer.endObject()

        writer.name(NAME_POINT)
        writer.beginObject()
        it.point.forEach {
            writer.name(it.key)
            writer.value(it.value)
        }
        writer.endObject()

        writer.endObject()
    }

    override fun read(reader: JsonReader): Event {
        var codename = ""
        var rerunAndParticipated = false
        var checkedShopItems: Collection<Item> = listOf()
        var lotteryBoxCount: Map<String, Int> = mapOf()
        var point: Map<String, Long> = mapOf()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                NAME_CODENAME -> codename = reader.nextString()
                NAME_RERUN_AND_PARTICIPATED -> rerunAndParticipated = reader.nextBoolean()
                NAME_CHECKED_SHOP_ITEMS -> checkedShopItems = itemCollectionAdapter.read(reader)
                NAME_LOTTERY_BOX_COUNT -> {
                    val map = HashMap<String, Int>()
                    reader.beginObject()
                    while (reader.hasNext()) {
                        val key = reader.nextName()
                        val value = reader.nextInt()
                        map[key] = value
                    }
                    reader.endObject()
                    lotteryBoxCount = map
                }
                NAME_POINT -> {
                    val map = HashMap<String, Long>()
                    reader.beginObject()
                    while (reader.hasNext()) {
                        val key = reader.nextName()
                        val value = reader.nextLong()
                        map[key] = value
                    }
                    reader.endObject()
                    point = map
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return Event(codename, rerunAndParticipated, checkedShopItems, lotteryBoxCount, point)
    }

    companion object {
        private const val NAME_CODENAME = "codename"
        private const val NAME_RERUN_AND_PARTICIPATED = "rerunAndParticipated"
        private const val NAME_CHECKED_SHOP_ITEMS = "checkedShopItems"
        private const val NAME_LOTTERY_BOX_COUNT = "lotteryBoxCount"
        private const val NAME_POINT = "point"
    }
}