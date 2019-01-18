package com.ssttkkl.fgoplanningtool.resources.eventdescriptor.gson

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor

class EventDescriptorMapGsonTypeAdapter : TypeAdapter<Map<String, EventDescriptor>>() {
    private val eventDescriptorAdapter = EventDescriptorGsonTypeAdapter()

    override fun read(reader: JsonReader): Map<String, EventDescriptor> {
        val map = HashMap<String, EventDescriptor>()
        reader.beginArray()
        while (reader.hasNext()) {
            eventDescriptorAdapter.read(reader).also {
                map[it.codename] = it
            }
        }
        reader.endArray()
        return map
    }

    override fun write(writer: JsonWriter, map: Map<String, EventDescriptor>) {
        writer.beginArray()
        map.values.forEach {
            eventDescriptorAdapter.write(writer, it)
        }
        writer.endArray()
    }

    companion object {
        val typeToken = object : TypeToken<Map<String, EventDescriptor>>() {}
    }
}