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
        writer.beginArray()
        it.lotteryBoxCount.forEach {
            writer.value(it)
        }
        writer.endArray()

        writer.name(NAME_POINT)
        writer.beginArray()
        it.point.forEach {
            writer.value(it)
        }
        writer.endArray()

        writer.endObject()
    }

    override fun read(reader: JsonReader): Event {
        var codename = ""
        var rerunAndParticipated = false
        var checkedShopItems: Collection<Item> = listOf()
        var lotteryBoxCount: List<Int> = listOf()
        var point: List<Long> = listOf()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                NAME_CODENAME -> codename = reader.nextString()
                NAME_RERUN_AND_PARTICIPATED -> rerunAndParticipated = reader.nextBoolean()
                NAME_CHECKED_SHOP_ITEMS -> checkedShopItems = itemCollectionAdapter.read(reader)
                NAME_LOTTERY_BOX_COUNT -> {
                    val list = ArrayList<Int>()
                    reader.beginArray()
                    while (reader.hasNext()) {
                        list.add(reader.nextInt())
                    }
                    reader.endArray()
                    lotteryBoxCount = list
                }
                NAME_POINT -> {
                    val list = ArrayList<Long>()
                    reader.beginArray()
                    while (reader.hasNext()) {
                        list.add(reader.nextLong())
                    }
                    reader.endArray()
                    point = list
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