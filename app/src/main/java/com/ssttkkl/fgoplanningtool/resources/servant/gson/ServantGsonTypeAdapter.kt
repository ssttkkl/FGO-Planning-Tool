package com.ssttkkl.fgoplanningtool.resources.servant.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.servant.Dress
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import java.util.*

class ServantGsonTypeAdapter : TypeAdapter<Servant>() {
    private val itemCollectionAdapter = ItemCollectionGsonTypeAdapter()
    private val dressGsonTypeAdapter = DressGsonTypeAdapter()

    override fun read(reader: JsonReader): Servant {
        var id = 0
        var jaName = ""
        var zhName = ""
        var enName = ""
        var hideRealName = false
        var realJaName = ""
        var realZhName = ""
        var realEnName = ""
        var star = 0
        var theClass = ServantClass.Shielder
        val nickname = ArrayList<String>()
        var wayToGet = WayToGet.BeginnerGift
        var ascensionItems: List<Collection<Item>> = List(4) { listOf<Item>() }
        var skillItems: List<Collection<Item>> = List(9) { listOf<Item>() }
        var dress: List<Dress> = listOf()
        var wikiLinks: Map<String, String> = mapOf()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                NAME_ID -> id = reader.nextInt()
                NAME_JA_NAME -> jaName = reader.nextString()
                NAME_ZH_NAME -> zhName = reader.nextString()
                NAME_EN_NAME -> enName = reader.nextString()
                NAME_HIDE_REAL_NAME -> hideRealName = reader.nextBoolean()
                NAME_REAL_JA_NAME -> realJaName = reader.nextString()
                NAME_REAL_ZH_NAME -> realZhName = reader.nextString()
                NAME_REAL_EN_NAME -> realEnName = reader.nextString()
                NAME_STAR -> star = reader.nextInt()
                NAME_CLASS -> theClass = ServantClass.valueOf(reader.nextString())
                NAME_NICKNAME -> {
                    reader.beginArray()
                    while (reader.hasNext())
                        nickname.add(reader.nextString())
                    reader.endArray()
                }
                NAME_WAY_TO_GET -> wayToGet = WayToGet.valueOf(reader.nextString())
                NAME_ASCENSION_ITEMS -> {
                    reader.beginArray()
                    ascensionItems = List(4) {
                        itemCollectionAdapter.read(reader)
                    }
                    reader.endArray()
                }
                NAME_SKILL_ITEMS -> {
                    reader.beginArray()
                    skillItems = List(9) {
                        itemCollectionAdapter.read(reader)
                    }
                    reader.endArray()
                }
                NAME_CLOTHES_ITEMS -> {
                    reader.beginArray()
                    dress = ArrayList()
                    while (reader.hasNext())
                        dress.add(dressGsonTypeAdapter.read(reader))
                    reader.endArray()
                }
                NAME_WIKI_LINKS -> {
                    reader.beginObject()
                    wikiLinks = HashMap()
                    while (reader.hasNext()) {
                        wikiLinks[reader.nextName()] = reader.nextString()
                    }
                    reader.endObject()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return Servant(id = id,
                jaName = jaName,
                zhName = zhName,
                enName = enName,
                hideRealName = hideRealName,
                realJaName = realJaName,
                realEnName = realEnName,
                realZhName = realZhName,
                star = star,
                theClass = theClass,
                nickname = nickname,
                wayToGet = wayToGet,
                ascensionItems = ascensionItems,
                skillItems = skillItems,
                dress = dress,
                wikiLinks = wikiLinks)
    }

    override fun write(writer: JsonWriter, it: Servant) {
        writer.beginObject()

        writer.name(NAME_ID)
        writer.value(it.id)

        writer.name(NAME_JA_NAME)
        writer.value(it.jaName)

        writer.name(NAME_ZH_NAME)
        writer.value(it.zhName)

        writer.name(NAME_EN_NAME)
        writer.value(it.enName)

        writer.name(NAME_HIDE_REAL_NAME)
        writer.value(it.hideRealName)

        writer.name(NAME_REAL_JA_NAME)
        writer.value(it.realJaName)

        writer.name(NAME_REAL_ZH_NAME)
        writer.value(it.realZhName)

        writer.name(NAME_REAL_EN_NAME)
        writer.value(it.realEnName)

        writer.name(NAME_STAR)
        writer.value(it.star)

        writer.name(NAME_CLASS)
        writer.value(it.theClass.name)

        writer.name(NAME_NICKNAME)
        writer.beginArray()
        it.nickname.forEach {
            writer.value(it)
        }
        writer.endArray()

        writer.name(NAME_WAY_TO_GET)
        writer.value(it.wayToGet.name)

        writer.name(NAME_ASCENSION_ITEMS)
        writer.beginArray()
        it.ascensionItems.forEach {
            itemCollectionAdapter.write(writer, it)
        }
        writer.endArray()

        writer.name(NAME_SKILL_ITEMS)
        writer.beginArray()
        it.skillItems.forEach {
            itemCollectionAdapter.write(writer, it)
        }
        writer.endArray()

        writer.name(NAME_CLOTHES_ITEMS)
        writer.beginArray()
        it.dress.forEach {
            dressGsonTypeAdapter.write(writer, it)
        }
        writer.endArray()

        writer.name(NAME_WIKI_LINKS)
        writer.beginObject()
        it.wikiLinks.forEach { (name, link) ->
            writer.name(name)
            writer.value(link)
        }
        writer.endObject()
        writer.endObject()
    }

    companion object {
        private const val NAME_ID = "id"
        private const val NAME_JA_NAME = "jaName"
        private const val NAME_ZH_NAME = "zhName"
        private const val NAME_EN_NAME = "enName"
        private const val NAME_HIDE_REAL_NAME = "hideRealName"
        private const val NAME_REAL_JA_NAME = "realJaName"
        private const val NAME_REAL_ZH_NAME = "realZhName"
        private const val NAME_REAL_EN_NAME = "realEnName"
        private const val NAME_STAR = "star"
        private const val NAME_CLASS = "class"
        private const val NAME_NICKNAME = "nickname"
        private const val NAME_WAY_TO_GET = "wayToGet"
        private const val NAME_ASCENSION_ITEMS = "ascensionItems"
        private const val NAME_SKILL_ITEMS = "skillItems"
        private const val NAME_CLOTHES_ITEMS = "dress"
        private const val NAME_WIKI_LINKS = "wikiLinks"
    }
}