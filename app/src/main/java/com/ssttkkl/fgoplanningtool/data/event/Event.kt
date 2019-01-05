package com.ssttkkl.fgoplanningtool.data.event

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.EventDescriptor
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@TypeConverters(Converter::class)
@Entity
@Parcelize
data class Event(@PrimaryKey val codename: String,
                 val rerunAndParticipated: Boolean,
                 val checkedShopItems: Collection<Item>,
                 val lotteryBoxCount: Map<String, Int>,
                 val point: Map<String, Long>) : Parcelable {
    val descriptor: EventDescriptor?
        get() = ResourcesProvider.instance.eventDescriptors[codename]

    val storyItems: Collection<Item>
        get() {
            val map = descriptor?.storyItems?.associateTo(HashMap()) { it.codename to it.count }
                    ?: HashMap<String, Long>()
            if (rerunAndParticipated)
                descriptor?.storyItemsIfParticipated?.forEach {
                    map[it.codename] = (map[it.codename] ?: 0) + it.count
                }
            else
                descriptor?.storyItemsIfNotParticipated?.forEach {
                    map[it.codename] = (map[it.codename] ?: 0) + it.count
                }
            return map.map { Item(it.key, it.value) }
        }

    val lotteryItems: Map<String, Collection<Item>>
        get() = descriptor?.lotteries?.mapValues {
            it.value.getItems(lotteryBoxCount[it.key] ?: 0)
        } ?: mapOf()

    val pointItems: Map<String, Collection<Item>>
        get() = descriptor?.pointPools?.mapValues {
            it.value.getItems(point[it.key] ?: 0)
        } ?: mapOf()

    val items: Collection<Item>
        get() {
            val map = HashMap<String, Long>()
            checkedShopItems.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            storyItems.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            lotteryItems.values.flatten().forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            pointItems.values.flatten().forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            return map.map { Item(it.key, it.value) }
        }

    @Ignore
    constructor(codename: String)
            : this(codename, false,
            ResourcesProvider.instance.eventDescriptors[codename]?.shopItems ?: listOf(),
            ResourcesProvider.instance.eventDescriptors[codename]?.lotteries?.keys?.associate { it to 0 }
                    ?: mapOf(),
            ResourcesProvider.instance.eventDescriptors[codename]?.pointPools?.mapValues { (_, pool) ->
                pool.items.keys.max() ?: 0
            } ?: mapOf<String, Long>())

    companion object : Parceler<Event> {
        override fun create(parcel: Parcel): Event {
            val codename = parcel.readString() ?: ""
            val rerunAndParticipated = parcel.readByte().toInt() == 1
            val checkedShopItems = parcel.readParcelableArray(Item::class.java.classLoader)?.mapNotNull { it as? Item }
                    ?: listOf()

            val lotteryBoxCountSize = parcel.readInt()
            val lotteryBoxCount = HashMap<String, Int>()
            for (i in 0 until lotteryBoxCountSize) {
                val key = parcel.readString()!!
                val value = parcel.readInt()
                lotteryBoxCount[key] = value
            }

            val pointSize = parcel.readInt()
            val pointCount = HashMap<String, Long>()
            for (i in 0 until pointSize) {
                val key = parcel.readString()!!
                val value = parcel.readLong()
                pointCount[key] = value
            }

            return Event(codename, rerunAndParticipated, checkedShopItems, lotteryBoxCount, pointCount)
        }

        override fun Event.write(parcel: Parcel, flags: Int) {
            parcel.writeString(codename)
            parcel.writeByte(if (rerunAndParticipated) 1 else 0)
            parcel.writeParcelableArray(checkedShopItems.toTypedArray(), 0)

            parcel.writeInt(lotteryBoxCount.size)
            lotteryBoxCount.forEach { (key, value) ->
                parcel.writeString(key)
                parcel.writeInt(value)
            }

            parcel.writeInt(point.size)
            point.forEach { (key, value) ->
                parcel.writeString(key)
                parcel.writeLong(value)
            }
        }
    }
}


private object Converter {
    private val gson = Gson()

    @JvmStatic
    @TypeConverter
    fun convertItemCollection(collection: Collection<Item>): String {
        return gson.toJson(collection, object : TypeToken<Collection<Item>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertItemCollection(value: String): Collection<Item> {
        return gson.fromJson(value, object : TypeToken<Collection<Item>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun convertStringToIntMap(map: Map<String, Int>): String {
        return gson.toJson(map, object : TypeToken<Map<String, Int>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertStringToIntMap(value: String): Map<String, Int> {
        return gson.fromJson(value, object : TypeToken<Map<String, Int>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun convertStringToLongMap(map: Map<String, Long>): String {
        return gson.toJson(map, object : TypeToken<Map<String, Long>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertStringToLongMap(value: String): Map<String, Long> {
        return gson.fromJson(value, object : TypeToken<Map<String, Long>>() {}.type)
    }
}