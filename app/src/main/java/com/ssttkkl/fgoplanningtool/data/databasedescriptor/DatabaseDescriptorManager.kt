package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.data.migration.DatabaseDescriptorDatabaseCommonMigration1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DatabaseDescriptorManager {
    private val database = DatabaseDescriptorDatabase.instance.apply {
        DatabaseDescriptorDatabaseCommonMigration1.migration(this)
    }

    val all: LiveData<Map<String, DatabaseDescriptor>> = Transformations.map(database.databaseDescriptorDao.allAsLiveData) { descriptors ->
        descriptors.associate { descriptor -> descriptor.uuid to descriptor }
    }

    operator fun get(uuid: String?): DatabaseDescriptor? {
        return runBlocking(Dispatchers.IO) {
            database.databaseDescriptorDao.get(uuid)
        }
    }

    fun getLiveData(uuid: String?): LiveData<DatabaseDescriptor?> {
        return database.databaseDescriptorDao.getAsLiveData(uuid)
    }

    fun insert(descriptors: Collection<DatabaseDescriptor>, immediately: Boolean = false) {
        if (immediately) runBlocking(Dispatchers.IO) {
            database.databaseDescriptorDao.insert(descriptors)
        }
        else GlobalScope.launch(Dispatchers.IO) {
            database.databaseDescriptorDao.insert(descriptors)
        }
    }

    fun insert(descriptor: DatabaseDescriptor, immediately: Boolean = false) {
        insert(listOf(descriptor), immediately)
    }

    fun update(descriptor: DatabaseDescriptor, immediately: Boolean = false) {
        if (immediately) runBlocking(Dispatchers.IO) {
            database.databaseDescriptorDao.update(descriptor)
        }
        else GlobalScope.launch(Dispatchers.IO) {
            database.databaseDescriptorDao.update(descriptor)
        }
    }

    fun remove(uuids: Collection<String>, immediately: Boolean = false) {
        if (immediately) runBlocking(Dispatchers.IO) {
            uuids.forEach { uuid ->
                database.databaseDescriptorDao.remove(database.databaseDescriptorDao.get(uuid)!!)
                RepoDatabase.removeDatabaseFile(uuid)
            }
            GlobalScope.launch(Dispatchers.Main) {
                if (this@DatabaseDescriptorManager[Repo.uuid.value] == null) {
                    Repo.switchDatabase(firstOrCreate.uuid)
                }
            }
        }
        else GlobalScope.launch(Dispatchers.IO) {
            uuids.forEach { uuid ->
                database.databaseDescriptorDao.remove(database.databaseDescriptorDao.get(uuid)!!)
                RepoDatabase.removeDatabaseFile(uuid)
            }
            GlobalScope.launch(Dispatchers.Main) {
                if (this@DatabaseDescriptorManager[Repo.uuid.value] == null) {
                    Repo.switchDatabase(firstOrCreate.uuid)
                }
            }
        }
    }

    fun remove(uuid: String, immediately: Boolean = false) {
        remove(listOf(uuid), immediately)
    }

    private fun getByName(name: String): DatabaseDescriptor? {
        return runBlocking(Dispatchers.IO) {
            database.databaseDescriptorDao.getByName(name)
        }
    }

    private fun generate(): DatabaseDescriptor {
        val name = MyApp.context.getString(R.string.newDatabaseName)
        return if (getByName(name) != null) {
            val nameMultiple = MyApp.context.getString(R.string.newDatabaseNameMultiple)
            var i = 2
            while (getByName(nameMultiple.format(i)) != null)
                i++
            DatabaseDescriptor.generate(nameMultiple.format(i))
        } else {
            DatabaseDescriptor.generate(name)
        }
    }

    fun generateAndInsert(name: String, immediately: Boolean = false): DatabaseDescriptor {
        val descriptor = DatabaseDescriptor.generate(name)
        insert(descriptor, immediately)
        return descriptor
    }

    fun generateAndInsert(immediately: Boolean = false): DatabaseDescriptor {
        val descriptor = generate()
        insert(descriptor, immediately)
        return descriptor
    }

    val firstOrCreate: DatabaseDescriptor
        get() {
            return runBlocking(Dispatchers.IO) { database.databaseDescriptorDao.all.firstOrNull() }
                    ?: generateAndInsert()
        }
}