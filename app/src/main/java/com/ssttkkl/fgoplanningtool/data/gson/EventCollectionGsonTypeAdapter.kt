package com.ssttkkl.fgoplanningtool.data.gson

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.plan.Plan

class EventCollectionGsonTypeAdapter : TypeAdapter<Collection<Event>>() {
    private val eventAdapter = EventGsonTypeAdapter()

    override fun write(writer: JsonWriter, collection: Collection<Event>) {
        writer.beginArray()
        collection.forEach {
            eventAdapter.write(writer, it)
        }
        writer.endArray()
    }

    override fun read(reader: JsonReader): Collection<Event> {
        val list = ArrayList<Event>()
        reader.beginArray()
        while (reader.hasNext())
            list.add(eventAdapter.read(reader))
        reader.endArray()
        return list
    }

    companion object {
        val typeToken = object : TypeToken<Collection<Event>>() {}
    }
}