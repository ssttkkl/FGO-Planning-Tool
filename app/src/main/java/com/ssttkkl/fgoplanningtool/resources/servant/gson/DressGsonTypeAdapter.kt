package com.ssttkkl.fgoplanningtool.resources.servant.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.resources.servant.Dress

class DressGsonTypeAdapter : TypeAdapter<Dress>() {
    private val itemCollectionGsonTypeAdapter = ItemCollectionGsonTypeAdapter()

    override fun read(reader: JsonReader): Dress {
        var zhName = ""
        var jaName = ""
        var enName = ""
        var items: Collection<Item> = listOf()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                KEY_ZH_NAME -> zhName = reader.nextString()
                KEY_JA_NAME -> jaName = reader.nextString()
                KEY_EN_NAME -> enName = reader.nextString()
                KEY_ITEMS -> items = itemCollectionGsonTypeAdapter.read(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return Dress(zhName = zhName,
                jaName = jaName,
                enName = enName,
                items = items)
    }

    override fun write(writer: JsonWriter, value: Dress) {
        writer.beginObject()

        writer.name(KEY_ZH_NAME)
        writer.value(value.zhName)

        writer.name(KEY_JA_NAME)
        writer.value(value.jaName)

        writer.name(KEY_EN_NAME)
        writer.value(value.enName)

        writer.name(KEY_ITEMS)
        itemCollectionGsonTypeAdapter.write(writer, value.items)

        writer.endObject()
    }

    companion object {
        private const val KEY_ZH_NAME = "zhName"
        private const val KEY_JA_NAME = "jaName"
        private const val KEY_EN_NAME = "enName"
        private const val KEY_ITEMS = "items"
    }
}