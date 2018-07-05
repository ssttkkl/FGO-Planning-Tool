package com.ssttkkl.fgoplanningtool.data

import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.data.plan.gson.PlanCollectionGsonTypeAdapter
import java.io.Reader

object RepoImporter {
    private val gson = GsonBuilder().registerTypeAdapter(ItemCollectionGsonTypeAdapter.typeToken.type, ItemCollectionGsonTypeAdapter())
            .registerTypeAdapter(PlanCollectionGsonTypeAdapter.typeToken.type, PlanCollectionGsonTypeAdapter())
            .create()

    fun importItems(reader: Reader) {
        val items = gson.fromJson<List<Item>>(reader, ItemCollectionGsonTypeAdapter.typeToken.type)
        Repo.itemRepo.apply {
            clear()
            update(items)
        }
    }

    fun importPlans(reader: Reader) {
        val plans = gson.fromJson<List<Plan>>(reader, PlanCollectionGsonTypeAdapter.typeToken.type)
        Repo.planRepo.apply {
            clear()
            insert(plans)
        }
    }
}