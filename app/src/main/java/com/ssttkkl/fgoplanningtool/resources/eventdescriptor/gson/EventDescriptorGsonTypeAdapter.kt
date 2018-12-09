package com.ssttkkl.fgoplanningtool.resources.eventdescriptor.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.LotteryEventDescriptor
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.NormalEventDescriptor
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

class EventDescriptorGsonTypeAdapter : TypeAdapter<EventDescriptor>() {
    private val itemCollectionTypeAdapter = ItemCollectionGsonTypeAdapter()

    override fun write(writer: JsonWriter, value: EventDescriptor) {

    }

    override fun read(reader: JsonReader): EventDescriptor {
        var type = ""
        var codename = ""
        var zhName = ""
        var jaName = ""
        var enName = ""
        var date: DateTime = DateTime.parse("1900/01/01", DATE_TIME_FORMATTER_BUILDER)
        var isRerun = false
        var storyItems: Collection<Item>? = null
        var storyItemsIfParticipated: Collection<Item>? = null
        var storyItemsIfNotParticipated: Collection<Item>? = null
        var shopItems: Collection<Item>? = null
        val lotteryItems = HashMap<IntRange, Collection<Item>>()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                KEY_TYPE -> type = reader.nextString()
                KEY_CODENAME -> codename = reader.nextString()
                KEY_ZH_NAME -> zhName = reader.nextString()
                KEY_JA_NAME -> jaName = reader.nextString()
                KEY_EH_NAME -> enName = reader.nextString()
                KEY_DATE -> date = DateTime.parse(reader.nextString(), DATE_TIME_FORMATTER_BUILDER)
                KEY_IS_RERUN -> isRerun = reader.nextBoolean()
                KEY_STORY_ITEMS -> storyItems = itemCollectionTypeAdapter.read(reader)
                KEY_STORY_ITEMS_IF_PARTICIPATED -> storyItemsIfParticipated = itemCollectionTypeAdapter.read(reader)
                KEY_STORY_ITEMS_IF_NOT_PARTICIPATED -> storyItemsIfNotParticipated = itemCollectionTypeAdapter.read(reader)
                KEY_SHOP_ITEMS -> shopItems = itemCollectionTypeAdapter.read(reader)
                KEY_LOTTERY_ITEMS -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        reader.beginObject()
                        var start = 0
                        var to = Int.MAX_VALUE
                        var items: Collection<Item>? = null
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                KEY_START -> start = reader.nextInt()
                                KEY_TO -> to = reader.nextInt()
                                KEY_ITEMS -> items = itemCollectionTypeAdapter.read(reader)
                            }
                        }
                        lotteryItems[start..to] = items ?: listOf()
                        reader.endObject()
                    }
                    reader.endArray()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return when (type) {
            TYPE_NORMAL -> NormalEventDescriptor(codename, jaName, zhName, enName, date, isRerun,
                    storyItems ?: listOf(),
                    storyItemsIfParticipated ?: listOf(),
                    storyItemsIfNotParticipated ?: listOf(),
                    shopItems ?: listOf())
            TYPE_LOTTERY -> LotteryEventDescriptor(codename, jaName, zhName, enName, date, isRerun,
                    storyItems ?: listOf(),
                    storyItemsIfParticipated ?: listOf(),
                    storyItemsIfNotParticipated ?: listOf(),
                    shopItems ?: listOf(),
                    lotteryItems)
            else -> throw Exception("\"$TYPE_NORMAL\" or \"$TYPE_LOTTERY\" type expected.")
        }
    }

    companion object {
        private const val KEY_CODENAME = "codename"
        private const val KEY_ZH_NAME = "zhName"
        private const val KEY_JA_NAME = "jaName"
        private const val KEY_EH_NAME = "enName"
        private const val KEY_TYPE = "type"
        private const val KEY_DATE = "date"
        private const val KEY_IS_RERUN = "isRerun"
        private const val KEY_STORY_ITEMS = "storyItems"
        private const val KEY_STORY_ITEMS_IF_PARTICIPATED = "storyItemsIfParticipated"
        private const val KEY_STORY_ITEMS_IF_NOT_PARTICIPATED = "storyItemsIfNotParticipated"
        private const val KEY_SHOP_ITEMS = "shopItems"
        private const val KEY_LOTTERY_ITEMS = "lotteryItems"
        private const val KEY_START = "start"
        private const val KEY_TO = "to"
        private const val KEY_ITEMS = "items"

        private const val TYPE_NORMAL = "normal"
        private const val TYPE_LOTTERY = "lottery"

        private val DATE_TIME_FORMATTER_BUILDER = DateTimeFormatterBuilder()
                .appendYear(4, 4)
                .appendLiteral('/')
                .appendMonthOfYear(2)
                .appendLiteral('/')
                .appendDayOfMonth(2)
                .toFormatter()
    }
}