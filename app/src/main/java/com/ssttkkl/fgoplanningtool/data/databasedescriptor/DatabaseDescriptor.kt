package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = "DatabaseDescriptor")
data class DatabaseDescriptor(@PrimaryKey val uuid: String,
                              var name: String,
                              val createTime: Long = Date().time) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeString(name)
        parcel.writeLong(createTime)
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

        fun generate(name: String) = DatabaseDescriptor(UUID.randomUUID().toString().filter { it != '-' }.toLowerCase(), name)
    }
}