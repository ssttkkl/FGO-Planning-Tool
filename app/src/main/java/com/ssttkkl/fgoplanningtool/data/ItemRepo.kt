package com.ssttkkl.fgoplanningtool.data

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

class ItemRepo(private val database: RepoDatabase) {
    private val cache = ConcurrentHashMap<String, Item>()

    internal val liveData = MutableLiveData<List<Item>>()

    init {
        Log.d("ItemRepo", "Loading database...")
        val exists = runBlocking(Dispatchers.db) { database.itemsDao.all.associate { Pair(it.codename, it) } }
        val nonExists = ResourcesProvider.itemDescriptors.values
                .filter { !exists.containsKey(it.codename) }
                .associate { Pair(it.codename, Item(it.codename, 0)) }
        cache.putAll(exists + nonExists)
        runBlocking(Dispatchers.db) { database.itemsDao.update(cache.values) }
        liveData.postValue(getAll())
        Log.d("ItemRepo", "Database loaded.")
    }

    private inline fun <T> performTransaction(action: () -> T): T {
        val ret = action.invoke()
        liveData.postValue(getAll())
        return ret
    }

    fun get(codename: String): Item? {
        return cache[codename]
    }

    fun getAll(): List<Item> {
        return cache.values.sortedBy { it.codename }
    }

    fun update(items: Collection<Item>) = performTransaction {
        items.forEach { item ->
            cache[item.codename] = item
        }
        database.itemsDao.update(items)
    }

    fun clear() = performTransaction {
        val newItems = ResourcesProvider.itemDescriptors.values
                .map { Item(it.codename, 0) }
        cache.clear()
        cache.putAll(newItems.map { Pair(it.codename, it) })
        database.itemsDao.update(newItems)
    }

    fun deductItems(itemsToDeduct: Collection<Item>) {
        update(itemsToDeduct.map {
            val itemInRepo = get(it.codename)
                    ?: throw Exception("Item ${it.codename} non-exists. ")
            if (it.count > itemInRepo.count)
                throw Exception("Number of item ${it.codename} to deduct is grater than this in repo. ")
            Item(it.codename, itemInRepo.count - it.count)
        })
    }

    fun update(item: Item) {
        update(listOf(item))
    }
}