package com.ssttkkl.fgoplanningtool.utils

import android.os.Parcelable
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

@Parcelize
data class ResPackUpdateInfo(val releaseDate: DateTime,
                             val content: String,
                             val targetVersion: Int,
                             val downloadLink: String) : Parcelable {

    class GsonTypeAdapter : TypeAdapter<ResPackUpdateInfo>() {
        override fun write(writer: JsonWriter, value: ResPackUpdateInfo) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun read(reader: JsonReader): ResPackUpdateInfo {
            var releaseDate = DateTime(0)
            var content = ""
            var targetVersion = 0
            var downloadLink = ""

            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    KEY_RELEASE_DATE -> releaseDate = DateTime.parse(reader.nextInt().toString(), dateTimeFormatter)
                    KEY_CONTENT -> content = reader.nextString()
                    KEY_TARGET_VERSION -> targetVersion = reader.nextInt()
                    KEY_DOWNLOAD_LINK -> downloadLink = reader.nextString()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()

            return ResPackUpdateInfo(releaseDate, content, targetVersion, downloadLink)
        }

        companion object {
            private const val KEY_RELEASE_DATE = "releaseDate"
            private const val KEY_CONTENT = "content"
            private const val KEY_TARGET_VERSION = "targetVersion"
            private const val KEY_DOWNLOAD_LINK = "downloadLink"

            private val dateTimeFormatter = DateTimeFormatterBuilder().appendYear(4, 4)
                    .appendMonthOfYear(2)
                    .appendDayOfMonth(2)
                    .toFormatter()
        }
    }
}