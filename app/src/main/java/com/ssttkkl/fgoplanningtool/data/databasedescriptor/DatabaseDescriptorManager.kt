package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import android.arch.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.data.migration.DatabaseDescriptorDatabaseCommonMigration1
import com.ssttkkl.fgoplanningtool.data.perform
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

object DatabaseDescriptorManager : Observer<List<DatabaseDescriptor>> {
    private val cache = ConcurrentHashMap<String, DatabaseDescriptor>()

    private val database = DatabaseDescriptorDatabase.instance.apply {
        DatabaseDescriptorDatabaseCommonMigration1.migration(this)

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
        get() = cache.values.sortedBy { it.createTime }

    operator fun get(uuid: String) = cache[uuid]

    fun getLiveData(uuid: String) = database.databaseDescriptorDao.getLiveDataByUUID(uuid)

    fun insert(descriptors: Collection<DatabaseDescriptor>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.databaseDescriptorDao.insert(descriptors)
        }
    }

    fun insert(descriptor: DatabaseDescriptor, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        insert(listOf(descriptor), howToPerform)
    }

    fun update(descriptor: DatabaseDescriptor, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.databaseDescriptorDao.update(descriptor)
        }
    }

    fun remove(uuids: Collection<String>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            uuids.forEach { uuid ->
                database.databaseDescriptorDao.remove(database.databaseDescriptorDao.getByUUID(uuid)!!)
                RepoDatabase.remove(uuid)
            }
        }
    }

    fun remove(uuid: String, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        remove(listOf(uuid), howToPerform)
    }

    fun clear(howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.databaseDescriptorDao.remove(database.databaseDescriptorDao.all)
        }
    }

    fun generate(): DatabaseDescriptor {
        val names = all.map { it.name }.toSet()
        val name = MyApp.context.getString(R.string.newDatabaseName)
        return if (names.contains(name)) {
            val nameMultiple = MyApp.context.getString(R.string.newDatabaseNameMultiple)
            var i = 2
            while (names.contains(nameMultiple.format(i)))
                i++
            DatabaseDescriptor.generate(nameMultiple.format(i))
        } else {
            DatabaseDescriptor.generate(name)
        }
    }

    fun generateAndInsert(name: String, howToPerform: HowToPerform = HowToPerform.RunBlocking): DatabaseDescriptor {
        val descriptor = DatabaseDescriptor.generate(name)
        insert(descriptor, howToPerform)
        return descriptor
    }

    fun generateAndInsert(howToPerform: HowToPerform = HowToPerform.RunBlocking): DatabaseDescriptor {
        val descriptor = generate()
        insert(descriptor, howToPerform)
        return descriptor
    }

    val firstOrCreate: DatabaseDescriptor
        get() {
            return all.firstOrNull() ?: generateAndInsert()
        }
}