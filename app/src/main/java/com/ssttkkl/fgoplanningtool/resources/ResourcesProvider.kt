package com.ssttkkl.fgoplanningtool.resources

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.resources.migration.CommonMigration1
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.gson.ServantMapGsonTypeAdapter
import java.io.File
import java.util.*

class ResourcesProvider(context: Context) {
    private val gson = GsonBuilder().registerTypeAdapter(ServantMapGsonTypeAdapter.typeToken.type, ServantMapGsonTypeAdapter())
            .create()

    val resourcesDir = File(context.filesDir, DIRECTORYNAME_RESOURCES)
    val avatarDir = File(resourcesDir, DIRECTORYNAME_AVATAR)
    val itemImgDir = File(resourcesDir, DIRECTORYNAME_ITEM)

    init {
        CommonMigration1.migration(resourcesDir)
    }

    val resPackInfo = buildResPackInfo()

    val isAbsent = !(resourcesDir.exists() && resourcesDir.isDirectory && resourcesDir.list().isNotEmpty())

    val isNotTargeted = resPackInfo.targetVersion != TARGET_VERSION

    var isBroken = false
        private set

    val servants: Map<Int, Servant> = buildServants()

    val itemRank: Map<String, Int>
    val itemDescriptors: Map<String, ItemDescriptor>

    init {
        val list = buildItemDescriptors()
        itemDescriptors = list.associate { Pair(it.codename, it) }
        itemRank = list.mapIndexed { idx, it -> Pair(it.codename, idx) }.toMap(HashMap())
    }

    private fun buildResPackInfo(): ResPackInfo {
        try {
            File(resourcesDir, FILENAME_RES_PACK_INFO).bufferedReader().use { reader ->
                return gson.fromJson(reader, ResPackInfo::class.java)
            }
        } catch (exc: Exception) {
            Log.e("ResProvider", "Failed to build ResPackInfo. $exc")
            isBroken = true
            return ResPackInfo(0, "", 1)
        }
    }

    private fun buildItemDescriptors(): List<ItemDescriptor> {
        try {
            File(resourcesDir, FILENAME_ITEM_INFO).bufferedReader().use { reader ->
                return gson.fromJson(reader, object : TypeToken<List<ItemDescriptor>>() {}.type)
            }
        } catch (exc: Exception) {
            Log.e("ResProvider", "Failed to build ItemDescriptor list. $exc")
            isBroken = true
            return listOf()
        }
    }

    private fun buildServants(): Map<Int, Servant> {
        try {
            File(resourcesDir, FILENAME_SERVANT_INFO).bufferedReader().use { reader ->
                return gson.fromJson(reader, ServantMapGsonTypeAdapter.typeToken.type)
            }
        } catch (exc: Exception) {
            Log.e("ResProvider", "Failed to build Servant map. $exc")
            isBroken = true
            return mapOf()
        }
    }

    companion object {
        const val TARGET_VERSION = 3

        const val DIRECTORYNAME_RESOURCES = "resources"
        const val DIRECTORYNAME_AVATAR = "avatar"
        const val DIRECTORYNAME_ITEM = "item"
        const val FILENAME_ITEM_INFO = "item_info.json"
        const val FILENAME_SERVANT_INFO = "servant_info.json"
        const val FILENAME_RES_PACK_INFO = "res_pack_info.json"

        private var INSTANCE: ResourcesProvider? = null

        val instance: ResourcesProvider
            get() {
                if (INSTANCE == null)
                    renewInstance()
                return INSTANCE!!
            }

        private val listeners = WeakHashMap<Any, () -> Unit>()

        fun addOnRenewListener(observer: Any, listener: () -> Unit) {
            if (listeners.containsKey(observer))
                Log.w("ResourcesProvider", "An observer can only register one listener. The old listener would be replaced.")
            listeners[observer] = listener
        }

        fun removeOnRenewListener(observer: Any) {
            listeners.remove(observer)
        }

        fun renewInstance() {
            synchronized(ResourcesProvider.Companion::class.java) {
                INSTANCE = ResourcesProvider(MyApp.context)
                listeners.forEach { (_, listener) ->
                    listener?.invoke()
                }
            }
        }
    }
}