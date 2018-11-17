package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "DatabaseDescriptor")
@Parcelize
data class DatabaseDescriptor(@PrimaryKey val uuid: String,
                              var name: String,
                              val createTime: Long = Date().time) : Parcelable {
    companion object {
        fun generate(name: String) = DatabaseDescriptor(UUID.randomUUID().toString().filter { it != '-' }.toLowerCase(), name)
    }
}