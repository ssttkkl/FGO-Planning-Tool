package com.ssttkkl.fgoplanningtool.data.event

import android.os.Parcel
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.utils.ItemCollectionConverter
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.eventdescriptor.NormalEventDescriptor
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@TypeConverters(ItemCollectionConverter::class)
@Entity
@Parcelize
data class NormalEvent(@PrimaryKey override val codename: String,
                       override val rerunAndParticipated: Boolean,
                       override val checkedShopItems: Collection<Item>) : Event {
    override val descriptor: NormalEventDescriptor?
        get() = ResourcesProvider.instance.eventDescriptors[codename] as? NormalEventDescriptor

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

    override val items: Collection<Item>
        get() = (checkedShopItems.associate { item -> item.codename to item.count }
                + storyItems.associate { item -> item.codename to item.count })
                .map { Item(it.key, it.value) }

    @Ignore
    constructor(codename: String)
            : this(codename, false,
            (ResourcesProvider.instance.eventDescriptors[codename] as? NormalEventDescriptor)?.shopItems
                    ?: listOf())

    companion object : Parceler<NormalEvent> {
        override fun create(parcel: Parcel): NormalEvent {
            return NormalEvent(parcel.readString()!!,
                    parcel.readByte().toInt() == 1,
                    parcel.readParcelableArray(Item::class.java.classLoader)?.mapNotNull { it as? Item }
                            ?: listOf())
        }

        override fun NormalEvent.write(parcel: Parcel, flags: Int) {
            parcel.writeString(codename)
            parcel.writeByte(if (rerunAndParticipated) 1 else 0)
            parcel.writeParcelableArray(checkedShopItems.toTypedArray(), 0)
        }
    }
}