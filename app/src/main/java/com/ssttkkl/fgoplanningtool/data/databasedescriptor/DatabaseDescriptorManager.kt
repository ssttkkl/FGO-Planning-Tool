package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import android.arch.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object DatabaseDescriptorManager : Observer<List<DatabaseDescriptor>> {
    private val cache = ConcurrentHashMap<String, DatabaseDescriptor>()

    private val database = DatabaseDescriptorDatabase.instance.apply {
        Log.d("DBDescriptorManager", "Loading database...")
        synchronized(cache) {
            cache.putAll(runBlocking(Dispatchers.db) { databaseDescriptorDao.all }.associate { Pair(it.uuid, it) })
        }
        Log.d("DBDescriptorManager", "Database loaded.")
    }

    val liveData = database.databaseDescriptorDao.allLiveData
            .apply { observeForever(this@DatabaseDescriptorManager) }

    override fun onChanged(t: List<DatabaseDescriptor>?) {
        synchronized(cache) {
            cache.clear()
            if (t != null)
                cache.putAll(t.associate { Pair(it.uuid, it) })
        }
        Log.d("DBDescriptorManager", "Database updated.")
    }

    val all
        get() = cache.values.sortedBy { it.uuid }

    operator fun get(uuid: String) = cache[uuid]

    fun getLiveData(uuid: String) = database.databaseDescriptorDao.getLiveDataByUUID(uuid)

    private fun performTransication(action: () -> Unit) = runBlocking(Dispatchers.db) { action.invoke() }

    private fun performTransicationAsync(action: () -> Unit) = async(Dispatchers.db) { action.invoke() }

    private fun insertImpl(descriptors: Collection<DatabaseDescriptor>) {
        database.databaseDescriptorDao.insert(descriptors)
    }

    fun insert(descriptors: Collection<DatabaseDescriptor>) = performTransication { insertImpl(descriptors) }

    fun insertAsync(descriptors: Collection<DatabaseDescriptor>) = performTransicationAsync { insertImpl(descriptors) }

    fun insert(descriptor: DatabaseDescriptor) {
        insert(listOf(descriptor))
    }

    fun insertAsync(descriptor: DatabaseDescriptor) {
        insertAsync(listOf(descriptor))
    }

    private fun removeImpl(uuids: Collection<String>) {
        uuids.forEach { uuid ->
            database.databaseDescriptorDao.remove(database.databaseDescriptorDao.getByUUID(uuid)!!)
        }
    }

    fun remove(uuids: Collection<String>) = performTransication { removeImpl(uuids) }

    fun removeAsync(uuids: Collection<String>) = performTransicationAsync { removeImpl(uuids) }

    fun remove(uuid: String) {
        remove(listOf(uuid))
    }

    fun removeAsync(uuid: String) {
        removeAsync(listOf(uuid))
    }

    private fun clearImpl() {
        database.databaseDescriptorDao.remove(database.databaseDescriptorDao.all)
    }

    fun clear() = performTransication { clearImpl() }

    fun clearAsync() = performTransicationAsync { clearImpl() }

    fun generateAndInsert(name: String): DatabaseDescriptor {
        val descriptor = DatabaseDescriptor(UUID.randomUUID().toString().filter { it != '-' }.toLowerCase(), name)
        insert(descriptor)
        return descriptor
    }

    fun generateAndInsert(): DatabaseDescriptor {
        val name = MyApp.context.getString(R.string.newDatabaseName)
        return if (cache.containsKey(name)) {
            val exists = cache.keys.filter { it.contains(name) }
            val nameMultiple = MyApp.context.getString(R.string.newDatabaseNameMultiple)
            var i = 2
            while (exists.contains(nameMultiple.format(i)))
                i++
            generateAndInsert(nameMultiple.format(i))
        } else {
            generateAndInsert(name)
        }
    }

    val firstOrCreate: DatabaseDescriptor
        get() {
            return all.firstOrNull() ?: generateAndInsert()
        }
}