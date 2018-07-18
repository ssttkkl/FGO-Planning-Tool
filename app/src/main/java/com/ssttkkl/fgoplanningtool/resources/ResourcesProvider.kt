package com.ssttkkl.fgoplanningtool.resources

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.gson.ServantMapGsonTypeAdapter
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.io.InputStreamReader

class ResourcesProvider(context: Context) {
    val resourcesDir = File(context.filesDir, DIRECTORYNAME_RESOURCES)
    val avatarDir = File(resourcesDir, DIRECTORYNAME_AVATAR)
    val itemImgDir = File(resourcesDir, DIRECTORYNAME_ITEM)

    val version: String
        get() {
            val versionFile = File(resourcesDir, FILENAME_VERSION)
            return if (!versionFile.exists()) "" else versionFile.readText()
        }

    val servants: Map<Int, Servant> = buildServants()

    val itemRank: Map<String, Int>
    val itemDescriptors: Map<String, ItemDescriptor>

    init {
        val list = buildItemDescriptors()
        itemDescriptors = list.associate { Pair(it.codename, it) }
        itemRank = list.mapIndexed { idx, it -> Pair(it.codename, idx) }.toMap(HashMap())
    }

    val ascensionQPInfo: List<List<Long>>
    val skillQPInfo: List<List<Long>>
    val palingenesisQPInfo: List<List<Long>>

    init {
        val map = buildQPInfo()
        ascensionQPInfo = map[ASCENSION] ?: List(6) { List(4) { 0L } }
        skillQPInfo = map[SKILL] ?: List(6) { List(9) { 0L } }
        palingenesisQPInfo = map[PALINGENESIS] ?: List(6) { List(10) { 0L } }
    }

    val broken: Boolean
        get() {
            val list = resourcesDir.listFiles()
            return listOf(FILENAME_SERVANT_INFO, FILENAME_ITEM_INFO, FILENAME_QP_INFO, FILENAME_VERSION).any { list.none { file -> file.name == it && file.isFile } }
                    && listOf(DIRECTORYNAME_AVATAR, DIRECTORYNAME_ITEM).any { list.none { file -> file.name == it && file.isDirectory } }
        }

    val absent: Boolean
        get() = !resourcesDir.exists() || !resourcesDir.isDirectory || resourcesDir.list().isEmpty()

    private fun buildItemDescriptors(): List<ItemDescriptor> {
        val file = File(resourcesDir, FILENAME_ITEM_INFO)
        if (!file.exists())
            return listOf()
        file.inputStream().use { stream ->
            InputStreamReader(stream).use { reader ->
                return gson.fromJson<List<ItemDescriptor>>(reader, object : TypeToken<List<ItemDescriptor>>() {}.type)
            }
        }
    }

    private fun buildServants(): Map<Int, Servant> {
        val file = File(resourcesDir, FILENAME_SERVANT_INFO)
        if (!file.exists())
            return mapOf()
        file.inputStream().use { stream ->
            InputStreamReader(stream).use { reader ->
                return gson.fromJson(reader, ServantMapGsonTypeAdapter.typeToken.type)
            }
        }
    }

    private fun buildQPInfo(): Map<String, List<List<Long>>> {
        val file = File(resourcesDir, FILENAME_QP_INFO)
        if (!file.exists())
            return mapOf()
        file.inputStream().use { stream ->
            InputStreamReader(stream).use { reader ->
                return gson.fromJson(reader, object : TypeToken<Map<String, List<List<Int>>>>() {}.type)
            }
        }
    }

    companion object {
        const val DIRECTORYNAME_RESOURCES = "resources"
        const val DIRECTORYNAME_AVATAR = "avatar"
        const val DIRECTORYNAME_ITEM = "item"
        const val FILENAME_ITEM_INFO = "item_info.json"
        const val FILENAME_SERVANT_INFO = "servant_info.json"
        const val FILENAME_QP_INFO = "qp_info.json"
        const val FILENAME_VERSION = "version"

        private const val ASCENSION = "ascension"
        private const val SKILL = "skill"
        private const val PALINGENESIS = "palingenesis"

        private val gson = GsonBuilder().registerTypeAdapter(ServantMapGsonTypeAdapter.typeToken.type, ServantMapGsonTypeAdapter())
                .create()

        private var INSTANCE: ResourcesProvider? = null

        val instance: ResourcesProvider
            get() {
                if (INSTANCE == null) {
                    synchronized(ResourcesProvider.Companion::class.java) {
                        if (INSTANCE == null)
                            INSTANCE = ResourcesProvider(MyApp.context)
                    }
                }
                return INSTANCE!!
            }

        fun renewInstance() {
            synchronized(ResourcesProvider.Companion::class.java) {
                INSTANCE = ResourcesProvider(MyApp.context)
            }
        }
    }
}