package com.ssttkkl.fgoplanningtool.data.item

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

@Entity(tableName = "Item")
data class Item(@PrimaryKey val codename: String,
                var count: Int) : Parcelable {
    val descriptor
        get() = ResourcesProvider.itemDescriptors[codename]

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt())

    constructor(item: Item) : this(item.codename, item.count)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(codename)
        parcel.writeInt(count)
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