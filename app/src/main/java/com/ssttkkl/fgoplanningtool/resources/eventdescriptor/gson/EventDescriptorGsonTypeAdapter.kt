package com.ssttkkl.fgoplanningtool.resources.eventdescriptor.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.Lottery
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.PointPool
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

class EventDescriptorGsonTypeAdapter : TypeAdapter<EventDescriptor>() {
    private val itemCollectionTypeAdapter = ItemCollectionGsonTypeAdapter()

    override fun write(writer: JsonWriter, value: EventDescriptor) {

    }

    override fun read(reader: JsonReader): EventDescriptor {
        var codename = ""
        var zhName = ""
        var jaName = ""
        var enName = ""
        var date: DateTime = DateTime.parse("1900/01/01", DATE_TIME_FORMATTER_BUILDER)
        var isRerun = false
        var storyItems: Collection<Item> = listOf()
        var storyItemsIfParticipated: Collection<Item> = listOf()
        var storyItemsIfNotParticipated: Collection<Item> = listOf()
        var shopItems: Collection<Item> = listOf()
        val lotteries = HashMap<String, Lottery>()
        val pointPools = HashMap<String, PointPool>()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                KEY_CODENAME -> codename = reader.nextString()
                KEY_ZH_NAME -> zhName = reader.nextString()
                KEY_JA_NAME -> jaName = reader.nextString()
                KEY_EN_NAME -> enName = reader.nextString()
                KEY_DATE -> date = DateTime.parse(reader.nextString(), DATE_TIME_FORMATTER_BUILDER)
                KEY_IS_RERUN -> isRerun = reader.nextBoolean()
                KEY_STORY_ITEMS -> storyItems = itemCollectionTypeAdapter.read(reader)
                KEY_STORY_ITEMS_IF_PARTICIPATED -> storyItemsIfParticipated = itemCollectionTypeAdapter.read(reader)
                KEY_STORY_ITEMS_IF_NOT_PARTICIPATED -> storyItemsIfNotParticipated = itemCollectionTypeAdapter.read(reader)
                KEY_SHOP_ITEMS -> shopItems = itemCollectionTypeAdapter.read(reader)
                KEY_LOTTERIES -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val lottery = readLottery(reader)
                        lotteries[lottery.codename] = lottery
                    }
                    reader.endArray()
                }
                KEY_POINT_POOLS -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val pointPool = readPointPool(reader)
                        pointPools[pointPool.codename] = pointPool
                    }
                    reader.endArray()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return EventDescriptor(codename, jaName, zhName, enName, date, isRerun,
                storyItems, storyItemsIfParticipated, storyItemsIfNotParticipated, shopItems,
                lotteries, pointPools)
    }

    private fun readLottery(reader: JsonReader): Lottery {
        var codename = ""
        var zhName = ""
        var enName = ""
        var jaName = ""
        val lotteryItems = HashMap<IntRange, Collection<Item>>()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                KEY_CODENAME -> codename = reader.nextString()
                KEY_ZH_NAME -> zhName = reader.nextString()
                KEY_EN_NAME -> enName = reader.nextString()
                KEY_JA_NAME -> jaName = reader.nextString()
                KEY_LOTTERY_ITEMS -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        var start = 0
                        var to = Int.MAX_VALUE
                        var items: Collection<Item> = listOf()

                        reader.beginObject()
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                KEY_START -> start = reader.nextInt()
                                KEY_TO -> to = reader.nextInt()
                                KEY_ITEMS -> items = itemCollectionTypeAdapter.read(reader)
                                else -> reader.skipValue()
                            }
                        }
                        lotteryItems[start..to] = items
                        reader.endObject()
                    }
                    reader.endArray()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return Lottery(codename, zhName, enName, jaName, lotteryItems)
    }

    private fun readPointPool(reader: JsonReader): PointPool {
        var codename = ""
        var zhName = ""
        var enName = ""
        var jaName = ""
        val pointItems = HashMap<Long, Collection<Item>>()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                KEY_CODENAME -> codename = reader.nextString()
                KEY_ZH_NAME -> zhName = reader.nextString()
                KEY_EN_NAME -> enName = reader.nextString()
                KEY_JA_NAME -> jaName = reader.nextString()
                KEY_POINT_ITEMS -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        var requestPoint = 0L
                        var items: Collection<Item> = listOf()

                        reader.beginObject()
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                KEY_REQUEST_POINT -> requestPoint = reader.nextLong()
                                KEY_ITEMS -> items = itemCollectionTypeAdapter.read(reader)
                            }
                        }
                        pointItems[requestPoint] = items
                        reader.endObject()
                    }
                    reader.endArray()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return PointPool(codename, zhName, enName, jaName, pointItems)
    }

    companion object {
        private const val KEY_CODENAME = "codename"
        private const val KEY_ZH_NAME = "zhName"
        private const val KEY_JA_NAME = "jaName"
        private const val KEY_EN_NAME = "enName"
        private const val KEY_DATE = "date"
        private const val KEY_IS_RERUN = "isRerun"
        private const val KEY_STORY_ITEMS = "storyItems"
        private const val KEY_STORY_ITEMS_IF_PARTICIPATED = "storyItemsIfParticipated"
        private const val KEY_STORY_ITEMS_IF_NOT_PARTICIPATED = "storyItemsIfNotParticipated"
        private const val KEY_SHOP_ITEMS = "shopItems"
        private const val KEY_LOTTERIES = "lotteries"
        private const val KEY_LOTTERY_ITEMS = "lotteryItems"
        private const val KEY_POINT_POOLS = "pointPools"
        private const val KEY_POINT_ITEMS = "pointItems"
        private const val KEY_REQUEST_POINT = "requestPoint"
        private const val KEY_START = "start"
        private const val KEY_TO = "to"
        private const val KEY_ITEMS = "items"

        private val DATE_TIME_FORMATTER_BUILDER = DateTimeFormatterBuilder()
                .appendYear(4, 4)
                .appendLiteral('/')
                .appendMonthOfYear(2)
                .appendLiteral('/')
                .appendDayOfMonth(2)
                .toFormatter()
    }
}