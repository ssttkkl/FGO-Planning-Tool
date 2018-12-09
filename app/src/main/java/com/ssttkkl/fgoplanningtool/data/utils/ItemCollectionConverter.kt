package com.ssttkkl.fgoplanningtool.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssttkkl.fgoplanningtool.data.item.Item

object ItemCollectionConverter {
    private val gson = Gson()
    private val type = object : TypeToken<Collection<Item>>() {}.type

    @JvmStatic
    @TypeConverter
    fun convertIntSet(collection: Collection<Item>): String {
        return gson.toJson(collection, type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertIntSet(value: String): Collection<Item> {
        return gson.fromJson(value, type)
    }
}