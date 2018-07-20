package com.ssttkkl.fgoplanningtool.data.utils

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object IntSetConverter {
    private val gson = Gson()
    private val type = object : TypeToken<Set<Int>>() {}.type

    @JvmStatic
    @TypeConverter
    fun convertIntSet(set: Set<Int>): String {
        return gson.toJson(set, type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertIntSet(value: String): Set<Int> {
        return gson.fromJson(value, type)
    }
}