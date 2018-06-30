package com.ssttkkl.fgoplanningtool.data.plan.gson

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.plan.Plan

class PlanCollectionGsonTypeAdapter : TypeAdapter<Collection<Plan>>() {
    private val planAdapter = PlanGsonTypeAdapter()

    override fun write(writer: JsonWriter, collection: Collection<Plan>) {
        writer.beginArray()
        collection.forEach {
            planAdapter.write(writer, it)
        }
        writer.endArray()
    }

    override fun read(reader: JsonReader): Collection<Plan> {
        val list = ArrayList<Plan>()
        reader.beginArray()
        while (reader.hasNext())
            list.add(planAdapter.read(reader))
        reader.endArray()
        return list
    }

    companion object {
        val typeToken = object : TypeToken<Collection<Plan>>() {}
    }
}