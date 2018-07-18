package com.ssttkkl.fgoplanningtool.data.item.gson

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.item.Item

class ItemCollectionGsonTypeAdapter : TypeAdapter<Collection<Item>>() {
    override fun write(writer: JsonWriter, it: Collection<Item>) {
        writer.beginObject()
        it.forEach {
            writer.name(it.codename)
            writer.value(it.count)
        }
        writer.endObject()
    }

    override fun read(reader: JsonReader): Collection<Item> {
        val list = ArrayList<Item>()
        reader.beginObject()
        while (reader.hasNext()) {
            list.add(Item(reader.nextName(), reader.nextLong()))
        }
        reader.endObject()
        return list
    }

    companion object {
        val typeToken = object : TypeToken<Collection<Item>>() {}
    }
}