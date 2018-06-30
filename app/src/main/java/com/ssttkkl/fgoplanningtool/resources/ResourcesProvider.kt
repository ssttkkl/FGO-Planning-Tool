package com.ssttkkl.fgoplanningtool.resources

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.gson.ServantMapGsonTypeAdapter
import java.io.InputStreamReader

object ResourcesProvider {
    val itemDescriptors: Map<String, ItemDescriptor> = ResourcesBuilder.buildItemDescriptors(MyApp.context)
    val servants: Map<Int, Servant> = ResourcesBuilder.buildServants(MyApp.context)
    val ascensionQPInfo: List<List<Int>>
    val skillQPInfo: List<List<Int>>

    init {
        val (ascension, skill) = ResourcesBuilder.buildQPInfo(MyApp.context)
        ascensionQPInfo = ascension
        skillQPInfo = skill
    }

    private object ResourcesBuilder {
        private val gson = GsonBuilder().registerTypeAdapter(ServantMapGsonTypeAdapter.typeToken.type, ServantMapGsonTypeAdapter())
                .create()

        private const val itemInfoFilename = "item_info.json"
        private const val servantInfoFilename = "servant_info.json"
        private const val qpInfoFilename = "qp_info.json"

        fun buildItemDescriptors(context: Context): Map<String, ItemDescriptor> {
            context.assets.open(itemInfoFilename).use { stream ->
                InputStreamReader(stream).use { reader ->
                    return gson.fromJson<List<ItemDescriptor>>(reader, object : TypeToken<List<ItemDescriptor>>() {}.type)
                            .associate { Pair(it.codename, it) }
                }
            }
        }

        fun buildServants(context: Context): Map<Int, Servant> {
            context.assets.open(servantInfoFilename).use { stream ->
                InputStreamReader(stream).use { reader ->
                    return gson.fromJson(reader, ServantMapGsonTypeAdapter.typeToken.type)
                }
            }
        }

        fun buildQPInfo(context: Context): Pair<List<List<Int>>, List<List<Int>>> {
            context.assets.open(qpInfoFilename).use { stream ->
                InputStreamReader(stream).use { reader ->
                    val map = gson.fromJson<Map<String, List<List<Int>>>>(reader, object : TypeToken<Map<String, List<List<Int>>>>() {}.type)
                    return Pair(map["ascension"]!!, map["skill"]!!)
                }
            }
        }
    }
}