package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = "DatabaseDescriptor")
data class DatabaseDescriptor(@PrimaryKey val uuid: String,
                              var name: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DatabaseDescriptor> {
        override fun createFromParcel(parcel: Parcel): DatabaseDescriptor {
            return DatabaseDescriptor(parcel)
        }

        override fun newArray(size: Int): Array<DatabaseDescriptor?> {
            return arrayOfNulls(size)
        }

        fun generate(name: String) =
                DatabaseDescriptor(UUID.randomUUID().toString().filter { it != '-' }, name)
    }
}