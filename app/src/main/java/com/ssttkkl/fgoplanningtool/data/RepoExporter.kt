package com.ssttkkl.fgoplanningtool.data

import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.data.item.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.data.plan.gson.PlanCollectionGsonTypeAdapter
import java.io.Writer

object RepoExporter {
    private val gson = GsonBuilder().registerTypeAdapter(ItemCollectionGsonTypeAdapter.typeToken.type, ItemCollectionGsonTypeAdapter())
            .registerTypeAdapter(PlanCollectionGsonTypeAdapter.typeToken.type, PlanCollectionGsonTypeAdapter())
            .create()

    fun exportItems(writer: Writer) {
        val items = Repo.itemRepo.all.filter { it.count != 0 }
        gson.toJson(items, ItemCollectionGsonTypeAdapter.typeToken.type, writer)
    }

    fun exportPlans(writer: Writer) {
        val plans = Repo.planRepo.all
        gson.toJson(plans, PlanCollectionGsonTypeAdapter.typeToken.type, writer)
    }
}