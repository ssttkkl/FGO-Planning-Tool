package com.ssttkkl.fgoplanningtool.data.item

import android.os.Parcel
import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

data class Item(val codename: String,
                var count: Int) : Parcelable {
    val descriptor
        get() = ResourcesProvider.itemDescriptors[codename]

    constructor(item: Item) : this(item.codename, item.count)

    constructor(p: Parcel) : this(p.readString(), p.readInt())

    override fun describeContents() = 0

    override fun writeToParcel(p: Parcel, flags: Int) {
        p.writeString(codename)
        p.writeInt(count)
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