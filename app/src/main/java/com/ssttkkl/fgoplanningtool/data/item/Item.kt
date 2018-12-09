package com.ssttkkl.fgoplanningtool.data.item

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Item")
@Parcelize
data class Item(@PrimaryKey val codename: String,
                var count: Long) : Parcelable {
    val descriptor
        get() = ResourcesProvider.instance.itemDescriptors[codename]

    @Ignore
    constructor(item: Item) : this(item.codename, item.count)
}

fun Collection<Item>.sorted(): List<Item> {
    return sortedBy { it.descriptor?.rank }
}