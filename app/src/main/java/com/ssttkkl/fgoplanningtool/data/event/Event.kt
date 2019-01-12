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
                 val lotteryBoxCount: List<Int>,
                 val point: List<Long>) : Parcelable {
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

    val lotteryItems: List<Collection<Item>>
        get() = descriptor?.lotteries?.mapIndexed { idx, it ->
            it.getItems(lotteryBoxCount[idx])
        } ?: listOf()

    val pointItems: List<Collection<Item>>
        get() = descriptor?.pointPools?.mapIndexed { idx, it ->
            it.getItems(point[idx])
        } ?: listOf()

    val items: Collection<Item>
        get() {
            val map = HashMap<String, Long>()
            checkedShopItems.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            storyItems.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            lotteryItems.flatten().forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            pointItems.flatten().forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            return map.map { Item(it.key, it.value) }
        }

    @Ignore
    constructor(codename: String)
            : this(codename, false,
            ResourcesProvider.instance.eventDescriptors[codename]?.shopItems ?: listOf(),
            List(ResourcesProvider.instance.eventDescriptors[codename]?.lotteries?.size ?: 0) { 0 },
            ResourcesProvider.instance.eventDescriptors[codename]?.pointPools?.map {
                it.items.keys.max() ?: 0
            } ?: listOf<Long>())

    companion object : Parceler<Event> {
        override fun create(parcel: Parcel): Event {
            val codename = parcel.readString() ?: ""
            val rerunAndParticipated = parcel.readByte().toInt() == 1

            val checkedShopItems = ArrayList<Item>()
            val checkedShopItemsSize = parcel.readInt()
            for (i in 0 until checkedShopItemsSize) {
                val item = parcel.readParcelable<Item>(Item::class.java.classLoader)
                if (item != null)
                    checkedShopItems.add(item)
            }

            val lotteryBoxCount = ArrayList<Int>()
            val lotteryBoxCountSize = parcel.readInt()
            for (i in 0 until lotteryBoxCountSize) {
                val item = parcel.readInt()
                lotteryBoxCount.add(item)
            }

            val pointCount = ArrayList<Long>()
            val pointCountSize = parcel.readLong()
            for (i in 0 until pointCountSize) {
                val item = parcel.readLong()
                pointCount.add(item)
            }

            return Event(codename, rerunAndParticipated, checkedShopItems, lotteryBoxCount, pointCount)
        }

        override fun Event.write(parcel: Parcel, flags: Int) {
            parcel.writeString(codename)
            parcel.writeByte(if (rerunAndParticipated) 1 else 0)

            parcel.writeInt(checkedShopItems.size)
            checkedShopItems.forEach {
                parcel.writeParcelable(it, 0)
            }

            parcel.writeInt(lotteryBoxCount.size)
            lotteryBoxCount.forEach {
                parcel.writeInt(it)
            }

            parcel.writeInt(point.size)
            point.forEach {
                parcel.writeLong(it)
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
    fun convertIntList(list: List<Int>): String {
        return gson.toJson(list, object : TypeToken<List<Int>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertIntList(value: String): List<Int> {
        return gson.fromJson(value, object : TypeToken<List<Int>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun convertLongList(list: List<Long>): String {
        return gson.toJson(list, object : TypeToken<List<Long>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun reconvertLongList(value: String): List<Long> {
        return gson.fromJson(value, object : TypeToken<List<Long>>() {}.type)
    }
}