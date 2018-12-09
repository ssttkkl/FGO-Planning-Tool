package com.ssttkkl.fgoplanningtool.data.event

import android.os.Parcel
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.utils.ItemCollectionConverter
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.LotteryEventDescriptor
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlin.math.min

@TypeConverters(ItemCollectionConverter::class)
@Entity
@Parcelize
data class LotteryEvent(@PrimaryKey override val codename: String,
                        override val rerunAndParticipated: Boolean,
                        override val checkedShopItems: Collection<Item>,
                        val boxCount: Int) : Event {
    override val descriptor: LotteryEventDescriptor?
        get() = ResourcesProvider.instance.eventDescriptors[codename] as? LotteryEventDescriptor

    override val storyItems: Collection<Item>
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

    val totalLotteryItems: Collection<Item>
        get() {
            val map = HashMap<String, Long>()
            descriptor?.lotteryItems?.forEach { (range, items) ->
                if (range.first <= boxCount) {
                    val times = min(range.endInclusive, boxCount) - range.first + 1
                    items.forEach { (codename, count) ->
                        map[codename] = (map[codename] ?: 0) + count * times
                    }
                }
            }
            return map.map { Item(it.key, it.value) }
        }

    override val items: Collection<Item>
        get() {
            val map = HashMap<String, Long>()
            checkedShopItems.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            descriptor?.storyItems?.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            totalLotteryItems.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
            return map.map { Item(it.key, it.value) }
        }

    @Ignore
    constructor(codename: String)
            : this(codename, false, listOf(), 0)

    companion object : Parceler<LotteryEvent> {
        override fun create(parcel: Parcel): LotteryEvent {
            return LotteryEvent(parcel.readString()!!,
                    parcel.readByte().toInt() == 1,
                    parcel.readParcelableArray(Item::class.java.classLoader)?.mapNotNull { it as? Item }
                            ?: listOf(),
                    parcel.readInt())
        }

        override fun LotteryEvent.write(parcel: Parcel, flags: Int) {
            parcel.writeString(codename)
            parcel.writeByte(if (rerunAndParticipated) 1 else 0)
            parcel.writeParcelableArray(checkedShopItems.toTypedArray(), 0)
            parcel.writeInt(boxCount)
        }
    }
}