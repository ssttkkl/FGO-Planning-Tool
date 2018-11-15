package com.ssttkkl.fgoplanningtool.data.item

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

@Entity(tableName = "Item")
data class Item(@PrimaryKey val codename: String,
                var count: Long) : Parcelable {
    val descriptor
        get() = ResourcesProvider.instance.itemDescriptors[codename]

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong())

    constructor(item: Item) : this(item.codename, item.count)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(codename)
        parcel.writeLong(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}