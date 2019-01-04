package com.ssttkkl.fgoplanningtool.data.gson

import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.DataSet
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan

class DataSetGsonTypeAdapter : TypeAdapter<DataSet>() {
    private val planCollectionAdapter = PlanCollectionGsonTypeAdapter()
    private val itemCollectionAdapter = ItemCollectionGsonTypeAdapter()

    override fun read(reader: JsonReader): DataSet {
        val doc = JsonParser().parse(reader)
        if (doc.isJsonObject && doc.asJsonObject.has(NAME_PLANS) && doc.asJsonObject.has(NAME_ITEMS)) {
            val plans = planCollectionAdapter.fromJson(doc.asJsonObject[NAME_PLANS].toString())
            val items = itemCollectionAdapter.fromJson(doc.asJsonObject[NAME_ITEMS].toString())
            return DataSet(plans, items)
        } else {
            val plans: Collection<Plan>? = try {
                planCollectionAdapter.fromJson(doc.toString())
            } catch (exc: Exception) {
                null
            }
            val items: Collection<Item>? = try {
                itemCollectionAdapter.fromJson(doc.toString())
            } catch (exc: Exception) {
                null
            }
            if (plans == null && items == null)
                throw Exception(MyApp.context.getString(R.string.fileFormatError))
            else
                return DataSet(plans, items)
        }
    }

    override fun write(writer: JsonWriter, value: DataSet) {
        writer.beginObject()
        writer.name(NAME_PLANS)
        planCollectionAdapter.write(writer, value.plans ?: listOf())
        writer.name(NAME_ITEMS)
        itemCollectionAdapter.write(writer, value.items ?: listOf())
        writer.endObject()
    }

    companion object {
        private const val NAME_PLANS = "plans"
        private const val NAME_ITEMS = "items"
    }
}