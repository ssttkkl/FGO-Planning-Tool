package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

object DatabaseDescriptorManager {
    private val cache = ConcurrentHashMap<String, DatabaseDescriptor>()

    private val mutableLiveData = MutableLiveData<List<DatabaseDescriptor>>()

    val liveData: LiveData<List<DatabaseDescriptor>>
        get() = mutableLiveData

    private val database = DatabaseDescriptorDatabase.instance.apply {
        Log.d("DBDescriptorManager", "Loading database...")
        cache.putAll(runBlocking(Dispatchers.db) { databaseDescriptorDao.all }
                .map { Pair(it.uuid, it) })
        mutableLiveData.postValue(getAll())
        Log.d("DBDescriptorManager", "Database loaded.")
    }

    private fun performTransaction(action: () -> Unit) {
        runBlocking(Dispatchers.db) {
            action.invoke()
        }
        mutableLiveData.postValue(getAll())
    }

    fun getAll(): List<DatabaseDescriptor> =
            cache.values.sortedBy { it.uuid }

    fun get(uuid: String): DatabaseDescriptor? =
            cache[uuid]

    fun insert(descriptors: Collection<DatabaseDescriptor>) = performTransaction {
        descriptors.forEach { descriptor ->
            cache[descriptor.uuid] = descriptor
        }
        database.databaseDescriptorDao.insert(descriptors)
    }

    fun remove(uuids: Collection<String>) = performTransaction {
        uuids.forEach { uuid ->
            cache.remove(uuid)
            val descriptor = database.databaseDescriptorDao.getByUUID(uuid)
            if (descriptor != null)
                database.databaseDescriptorDao.remove(descriptor)
        }
    }

    fun clear() = performTransaction {
        cache.clear()
        database.databaseDescriptorDao.remove(database.databaseDescriptorDao.all)
    }

    fun insert(descriptor: DatabaseDescriptor) {
        insert(listOf(descriptor))
    }

    fun remove(uuid: String) {
        remove(listOf(uuid))
    }
}