package com.ssttkkl.fgoplanningtool.utils

import android.os.Parcelable
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

@Parcelize
data class AppUpdateInfo(val versionName: String,
                         val versionCode: Int,
                         val releaseDate: DateTime,
                         val information: String,
                         val downloadLink: String) : Parcelable {

    class GsonTypeAdapter : TypeAdapter<AppUpdateInfo>() {
        override fun write(writer: JsonWriter, value: AppUpdateInfo) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun read(reader: JsonReader): AppUpdateInfo {
            var versionName = ""
            var versionCode = 0
            var releaseDate = DateTime(0)
            var information = ""
            var downloadLink = ""

            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    KEY_VERSION_NAME -> versionName = reader.nextString()
                    KEY_VERSION_CODE -> versionCode = reader.nextInt()
                    KEY_RELEASE_DATE -> releaseDate = DateTime.parse(reader.nextInt().toString(), dateTimeFormatter)
                    KEY_INFORMATION -> information = reader.nextString()
                    KEY_DOWNLOAD_LINK -> downloadLink = reader.nextString()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()

            return AppUpdateInfo(versionName, versionCode, releaseDate, information, downloadLink)
        }

        companion object {
            private const val KEY_VERSION_NAME = "versionName"
            private const val KEY_VERSION_CODE = "versionCode"
            private const val KEY_RELEASE_DATE = "releaseDate"
            private const val KEY_INFORMATION = "information"
            private const val KEY_DOWNLOAD_LINK = "downloadLink"

            private val dateTimeFormatter = DateTimeFormatterBuilder().appendYear(4, 4)
                    .appendMonthOfYear(2)
                    .appendDayOfMonth(2)
                    .toFormatter()
        }
    }
}