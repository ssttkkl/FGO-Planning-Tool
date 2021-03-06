package com.ssttkkl.fgoplanningtool.resources

import android.os.Parcelable
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

@Parcelize
data class ResPackInfo(val releaseDate: DateTime,
                       val content: String,
                       val targetVersion: Int) : Parcelable {

    class GsonTypeAdapter : TypeAdapter<ResPackInfo>() {
        override fun write(writer: JsonWriter, value: ResPackInfo) {
            writer.beginObject()

            writer.name(KEY_RELEASE_DATE)
            writer.value(value.releaseDate.toString(dateTimeFormatter).toInt())

            writer.name(KEY_CONTENT)
            writer.value(value.content)

            writer.name(KEY_TARGET_VERSION)
            writer.value(value.targetVersion)

            writer.endObject()
        }

        override fun read(reader: JsonReader): ResPackInfo {
            var releaseDate = DateTime(0)
            var content = ""
            var targetVersion = 0

            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    KEY_RELEASE_DATE -> releaseDate = DateTime.parse(reader.nextInt().toString(), dateTimeFormatter)
                    KEY_CONTENT -> content = reader.nextString()
                    KEY_TARGET_VERSION -> targetVersion = reader.nextInt()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()

            return ResPackInfo(releaseDate, content, targetVersion)
        }

        companion object {
            private const val KEY_RELEASE_DATE = "releaseDate"
            private const val KEY_CONTENT = "content"
            private const val KEY_TARGET_VERSION = "targetVersion"

            private val dateTimeFormatter = DateTimeFormatterBuilder().appendYear(4, 4)
                    .appendMonthOfYear(2)
                    .appendDayOfMonth(2)
                    .toFormatter()
        }
    }
}