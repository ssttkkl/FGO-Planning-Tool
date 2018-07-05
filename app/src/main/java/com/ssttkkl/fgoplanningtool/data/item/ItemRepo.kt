package com.ssttkkl.fgoplanningtool.data.item

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

class ItemRepo(private val database: RepoDatabase) {
    private val cache = ConcurrentHashMap<String, Item>()

    val liveData = MutableLiveData<List<Item>>()

    init {
        Log.d("ItemRepo", "Loading database...")
        val exists = runBlocking(Dispatchers.db) { database.itemsDao.all.associate { Pair(it.codename, it) } }
        val nonExists = ResourcesProvider.itemDescriptors.values
                .filter { !exists.containsKey(it.codename) }
                .associate { Pair(it.codename, Item(it.codename, 0)) }
        cache.putAll(exists + nonExists)
        runBlocking(Dispatchers.db) { database.itemsDao.update(cache.values) }
        liveData.postValue(all)
        Log.d("ItemRepo", "Database loaded.")
    }

    operator fun get(codename: String): Item? {
        return cache[codename]
    }

    val all: List<Item>
        get() = cache.values.sortedBy { it.codename }

    private fun performTransaction(action: () -> Unit) {
        runBlocking(Dispatchers.db) {
            action.invoke()
        }
        liveData.postValue(all)
    }

    private fun performTransactionAsync(action: () -> Unit) =
            async(Dispatchers.db) {
                action.invoke()
                liveData.postValue(all)
            }

    private fun updateImpl(items: Collection<Item>) {
        items.forEach { item ->
            cache[item.codename] = item
        }
        database.itemsDao.update(items)
    }

    fun update(items: Collection<Item>) = performTransaction { updateImpl(items) }

    fun updateAsync(items: Collection<Item>) = performTransactionAsync { updateImpl(items) }

    private fun clearImpl() {
        val newItems = ResourcesProvider.itemDescriptors.values
                .map { Item(it.codename, 0) }
        cache.clear()
        cache.putAll(newItems.map { Pair(it.codename, it) })
        database.itemsDao.update(newItems)
    }

    fun clear() = performTransaction { clearImpl() }

    fun clearAsync() = performTransactionAsync { clearImpl() }

    private fun processDeductItems(itemsToDeduct: Collection<Item>) =
            itemsToDeduct.map {
                val itemInRepo = get(it.codename)
                        ?: throw Exception("Item ${it.codename} non-exists. ")
                if (it.count > itemInRepo.count)
                    throw Exception("Number of item ${it.codename} to deduct is grater than this in repo. ")
                Item(it.codename, itemInRepo.count - it.count)
            }

    fun deductItems(itemsToDeduct: Collection<Item>) {
        update(processDeductItems(itemsToDeduct))
    }

    fun deductItemsAsync(itemsToDeduct: Collection<Item>) {
        updateAsync(processDeductItems(itemsToDeduct))
    }

    fun update(item: Item) {
        update(listOf(item))
    }

    fun updateAsync(item: Item) {
        updateAsync(listOf(item))
    }
}