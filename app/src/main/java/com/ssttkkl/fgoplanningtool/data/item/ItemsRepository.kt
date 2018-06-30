package com.ssttkkl.fgoplanningtool.data.item

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.ssttkkl.fgoplanningtool.data.MyDatabase
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking

object ItemsRepository {
    private val database = MyDatabase.getInstance()

    private val cache = HashMap<String, ItemDatabaseEntity>()

    fun getAll(): List<Item> {
        return ResourcesProvider.itemDescriptors.keys.map { get(it) }
    }

    fun get(codename: String): Item {
        if (!cache.contains(codename)) {
            cache[codename] = database.itemsDao().getItemByName(codename)
                    ?: ItemDatabaseEntity(codename, 0).also { database.itemsDao().updateItem(it) }
        }
        return cache[codename]!!.item
    }

    fun update(item: Item) {
        ItemDatabaseEntity(item).also {
            database.itemsDao().updateItem(it)
            cache[it.codename] = it
        }
    }

    fun update(items: Collection<Item>) {
        items.map { ItemDatabaseEntity(it) }.also {
            database.itemsDao().updateItems(it)
            it.forEach {
                cache[it.codename] = it
            }
        }
    }

    fun deductItems(itemsToDeduct: Collection<Item>) {
        update(itemsToDeduct.map {
            val itemInRepo = get(it.codename)
            if (it.count > itemInRepo.count)
                throw Exception("Count of items to minus can't be greater than them in repository.")
            Item(it.codename, itemInRepo.count - it.count)
        })
    }

    fun clear() {
        cache.forEach { (_, value) -> value.count = 0 }
        database.itemsDao().updateItems(cache.values.toList())
    }

    fun observeAll(owner: LifecycleOwner, observer: Observer<List<Item>>) {
        runBlocking(CommonPool) { getAll() }
        database.itemsDao().allItemsAsLiveData.observe(owner, Observer {
            observer.onChanged(it?.map { it.item })
        })
    }
}