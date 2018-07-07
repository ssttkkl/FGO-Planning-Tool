package com.ssttkkl.fgoplanningtool.data.item

import android.arch.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

class ItemRepo(private val database: RepoDatabase) : Observer<List<Item>> {
    private val cache = ConcurrentHashMap<String, Item>()

    val liveData = database.itemsDao.allLiveData
            .apply { observeForever(this@ItemRepo) }

    override fun onChanged(t: List<Item>?) {
        synchronized(cache) {
            cache.clear()
            if (t != null) {
                val existCodename = t.map { it.codename }.toSet()
                val absentCodename = ResourcesProvider.itemDescriptors.keys.filter { !existCodename.contains(it) }.toSet()
                if (absentCodename.isNotEmpty()) {
                    launch(Dispatchers.db) { database.itemsDao.update(absentCodename.map { Item(it, 0) }) }
                    Log.d("ItemRepo", "Inserted ${absentCodename.size} Item(s) absent from database. ")
                }

                cache.putAll(t.associate { Pair(it.codename, it) })
            }
        }
        Log.d("ItemRepo", "Database updated.")
    }

    val all: List<Item>
        get() = cache.values.sortedBy { it.codename }

    operator fun get(codename: String): Item? {
        return cache[codename]
    }

    private fun performTransaction(action: () -> Unit) = runBlocking(Dispatchers.db) { action.invoke() }

    private fun performTransactionAsync(action: () -> Unit) = async(Dispatchers.db) { action.invoke() }

    private fun updateImpl(items: Collection<Item>) {
        database.itemsDao.update(items)
    }

    fun update(items: Collection<Item>) = performTransaction { updateImpl(items) }

    fun updateAsync(items: Collection<Item>) = performTransactionAsync { updateImpl(items) }

    private fun clearImpl() {
        val newItems = database.itemsDao.all.apply {
            forEach { it.count = 0 }
        }
        database.itemsDao.update(newItems)
    }

    fun clear() = performTransaction { clearImpl() }

    fun clearAsync() = performTransactionAsync { clearImpl() }

    private fun processDeductItems(itemsToDeduct: Collection<Item>) =
            itemsToDeduct.map {
                val itemInRepo = get(it.codename)
                        ?: throw Exception("Item ${it.codename} is absent. ")
                if (it.count > itemInRepo.count)
                    throw Exception("Number of item ${it.codename} to deduct is grater than that in repo. ")
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