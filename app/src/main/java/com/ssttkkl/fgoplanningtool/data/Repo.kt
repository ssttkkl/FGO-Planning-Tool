package com.ssttkkl.fgoplanningtool.data

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object Repo {
    val uuid: LiveData<String?> = object : MutableLiveData<String?>() {
        override fun setValue(value: String?) {
            val oldValue = getValue()
            if (oldValue != value) {
                RepoDatabase.dispose(oldValue)
                super.setValue(value)
            }
        }
    }

    private val database = object : LiveData<RepoDatabase?>() {
        init {
            uuid.observeForever {
                value = if (it != null)
                    RepoDatabase.getInstance(it)
                else
                    null
            }
        }
    }

    val databaseDescriptor: LiveData<DatabaseDescriptor?> = Transformations.switchMap(uuid) {
        DatabaseDescriptorManager.getLiveData(it)
    }

    fun switchDatabase(uuid: String) {
        (this.uuid as MutableLiveData).value = uuid

        PreferenceManager.getDefaultSharedPreferences(MyApp.context).edit {
            putString(PreferenceKeys.KEY_DEFAULT_DB_UUID, uuid)
            apply()
        }
    }

    private fun exec(immediately: Boolean, action: (RepoDatabase) -> Unit) {
        if (immediately) runBlocking(Dispatchers.IO) {
            database.value?.also { action.invoke(it) }
        } else GlobalScope.launch(Dispatchers.IO) {
            database.value?.also { action.invoke(it) }
        }
    }

    init {
        val pref = PreferenceManager.getDefaultSharedPreferences(MyApp.context)
        // switch to default database
        val uuidInPref = pref.getString(PreferenceKeys.KEY_DEFAULT_DB_UUID, "") ?: ""
        val uuid = if (DatabaseDescriptorManager[uuidInPref] != null)
            uuidInPref
        else
            DatabaseDescriptorManager.firstOrCreate.uuid
        switchDatabase(uuid)
    }

    object PlanRepo {
        val all: Map<Int, Plan>
            get() = runBlocking(Dispatchers.IO) {
                database.value?.plansDao?.all?.associate { it.servantId to it } ?: mapOf()
            }

        val allAsLiveData: LiveData<Map<Int, Plan>> = Transformations.switchMap(database) { database ->
            Transformations.map(database?.plansDao?.allAsLiveData ?: MutableLiveData()) {
                it?.associate { plan -> plan.servantId to plan } ?: mapOf()
            }
        }

        operator fun get(servantId: Int): Plan? {
            return runBlocking(Dispatchers.IO) {
                database.value?.plansDao?.get(servantId)
            }
        }

        fun insert(plans: Collection<Plan>, immediately: Boolean = false) {
            exec(immediately) { db ->
                db.plansDao.insert(plans)
            }
        }

        fun remove(servantIds: Collection<Int>, immediately: Boolean = false) {
            exec(immediately) { db ->
                db.plansDao.remove(servantIds.mapNotNull { database.value?.plansDao?.get(it) })
            }
        }

        fun insert(plan: Plan, immediately: Boolean = false) {
            insert(listOf(plan), immediately)
        }

        fun remove(servantId: Int, immediately: Boolean = false) {
            remove(listOf(servantId), immediately)
        }

        fun clear(immediately: Boolean = false) {
            exec(immediately) { db ->
                db.plansDao.clear()
            }
        }
    }

    object ItemRepo {
        val all: Map<String, Item>
            get() = runBlocking(Dispatchers.IO) {
                val map = database.value?.itemsDao?.all?.associate { it.codename to it }?.toMutableMap()
                        ?: HashMap()
                ResourcesProvider.instance.itemDescriptors.forEach { (codename, _) ->
                    if (!map.containsKey(codename))
                        map[codename] = Item(codename, 0)
                }
                map.toMap()
            }

        val allAsLiveData: LiveData<Map<String, Item>> = Transformations.switchMap(database) { database ->
            Transformations.map(database?.itemsDao?.allAsLiveData ?: MutableLiveData()) {
                val map = it.associate { item -> item.codename to item }.toMutableMap()
                ResourcesProvider.instance.itemDescriptors.forEach { (codename, _) ->
                    if (!map.containsKey(codename))
                        map[codename] = Item(codename, 0)
                }
                map.toMap()
            }
        }

        operator fun get(codename: String): Item {
            return runBlocking(Dispatchers.IO) {
                database.value?.itemsDao?.get(codename)
                        ?: Item(codename, 0)
            }
        }

        fun update(items: Collection<Item>, immediately: Boolean = false) {
            exec(immediately) { db ->
                db.itemsDao.update(items)
            }
        }

        fun update(item: Item, immediately: Boolean = false) {
            update(listOf(item), immediately)
        }

        fun add(itemToAdd: Collection<Item>, immediately: Boolean = false) {
            val items = itemToAdd.map {
                val itemInRepo = get(it.codename)
                Item(it.codename, itemInRepo.count + it.count)
            }
            update(items, immediately)
        }

        fun deduct(itemsToDeduct: Collection<Item>, immediately: Boolean = false) {
            val items = itemsToDeduct.map {
                val itemInRepo = get(it.codename)
                if (it.count > itemInRepo.count)
                    throw Exception("Number of item ${it.codename} to deduct is grater than that in repo. ")
                Item(it.codename, itemInRepo.count - it.count)
            }
            update(items, immediately)
        }

        fun clear(immediately: Boolean = false) {
            exec(immediately) { db ->
                db.itemsDao.clear()
            }
        }
    }

    object EventRepo {
        val all: Map<String, Event>
            get() = runBlocking(Dispatchers.IO) {
                database.value?.eventsDao?.all?.associate { it.codename to it } ?: mapOf()
            }

        val allAsLiveData: LiveData<Map<String, Event>> = Transformations.switchMap(database) { database ->
            Transformations.map(database?.eventsDao?.allAsLiveData ?: MutableLiveData()) {
                it?.associate { event -> event.codename to event } ?: mapOf()
            }
        }

        operator fun get(codename: String): Event? {
            return runBlocking(Dispatchers.IO) {
                database.value?.eventsDao?.get(codename)
            }
        }

        fun insert(events: Collection<Event>, immediately: Boolean = false) {
            exec(immediately) { db ->
                db.eventsDao.insert(events)
            }
        }

        fun remove(codenames: Collection<String>, immediately: Boolean = false) {
            exec(immediately) { db ->
                db.eventsDao.remove(codenames.mapNotNull {
                    database.value?.eventsDao?.get(it)
                })
            }
        }

        fun insert(event: Event, immediately: Boolean = false) {
            insert(listOf(event), immediately)
        }

        fun remove(codename: String, immediately: Boolean = false) {
            remove(listOf(codename), immediately)
        }

        fun clear(immediately: Boolean = false) {
            exec(immediately) { db ->
                db.eventsDao.clear()
            }
        }
    }
}