package com.ssttkkl.fgoplanningtool.data

import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.data.item.ItemsRepository
import com.ssttkkl.fgoplanningtool.data.item.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.data.plan.PlansRepository
import com.ssttkkl.fgoplanningtool.data.plan.gson.PlanCollectionGsonTypeAdapter
import java.io.Writer

object DatabaseExporter {
    private val gson = GsonBuilder().registerTypeAdapter(ItemCollectionGsonTypeAdapter.typeToken.type, ItemCollectionGsonTypeAdapter())
            .registerTypeAdapter(PlanCollectionGsonTypeAdapter.typeToken.type, PlanCollectionGsonTypeAdapter())
            .create()

    fun exportItems(writer: Writer) {
        val items = ItemsRepository.getAll().filter { it.count != 0 }
        gson.toJson(items, ItemCollectionGsonTypeAdapter.typeToken.type, writer)
    }

    fun exportPlans(writer: Writer) {
        val plans = PlansRepository.getAll()
        gson.toJson(plans, PlanCollectionGsonTypeAdapter.typeToken.type, writer)
    }
}