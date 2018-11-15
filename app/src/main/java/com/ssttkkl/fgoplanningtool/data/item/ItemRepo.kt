package com.ssttkkl.fgoplanningtool.data.item

import android.arch.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.data.perform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

class ItemRepo(private val database: RepoDatabase) : Observer<List<Item>> {
    private val cache = ConcurrentHashMap<String, Item>()

    val liveData = database.itemsDao.allLiveData
            .apply { observeForever(this@ItemRepo) }

    override fun onChanged(t: List<Item>?) {
        synchronized(cache) {
            cache.clear()
            if (t != null)
                cache.putAll(t.associate { Pair(it.codename, it) })
        }
        Log.d("ItemRepo", "Database updated.")
    }

    val all: List<Item>
        get() = cache.values.sortedBy { it.codename }

    operator fun get(codename: String): Item {
        return cache[codename]
                ?: runBlocking(Dispatchers.IO) { database.itemsDao.getByName(codename) }
                ?: Item(codename, 0)
    }

    fun update(items: Collection<Item>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.itemsDao.update(items)
        }
    }

    fun clear(howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.itemsDao.update(database.itemsDao.all)
        }
    }

    private fun processDeductItems(itemsToDeduct: Collection<Item>) =
            itemsToDeduct.map {
                val itemInRepo = get(it.codename)
                if (it.count > itemInRepo.count)
                    throw Exception("Number of item ${it.codename} to deduct is grater than that in repo. ")
                Item(it.codename, itemInRepo.count - it.count)
            }

    fun deductItems(itemsToDeduct: Collection<Item>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        update(processDeductItems(itemsToDeduct), howToPerform)
    }

    fun update(item: Item, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        update(listOf(item), howToPerform)
    }
}