package com.ssttkkl.fgoplanningtool.resources.servant.gson

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.resources.servant.Servant

class ServantMapGsonTypeAdapter : TypeAdapter<Map<Int, Servant>>() {
    private val servantAdapter = ServantGsonTypeAdapter()

    override fun read(reader: JsonReader): Map<Int, Servant> {
        val map = HashMap<Int, Servant>()
        reader.beginArray()
        while (reader.hasNext()) {
            servantAdapter.read(reader).also {
                map[it.id] = it
            }
        }
        reader.endArray()
        return map
    }

    override fun write(writer: JsonWriter, map: Map<Int, Servant>) {
        writer.beginArray()
        map.values.forEach {
            servantAdapter.write(writer, it)
        }
        writer.endArray()
    }

    companion object {
        val typeToken = object : TypeToken<Map<Int, Servant>>() {}
    }
}