package com.ssttkkl.fgoplanningtool.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
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
    }

    private fun exec(immediately: Boolean, action: (RepoDatabase) -> Unit) {
        if (immediately) runBlocking(Dispatchers.IO) {
            database.value?.also { action.invoke(it) }
        } else GlobalScope.launch(Dispatchers.IO) {
            database.value?.also { action.invoke(it) }
        }
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
            exec(immediately) { database ->
                database.plansDao.insert(plans)
            }
        }

        fun remove(servantIds: Collection<Int>, immediately: Boolean = false) {
            exec(immediately) {
                database.value?.plansDao?.remove(servantIds.mapNotNull { database.value?.plansDao?.get(it) })
            }
        }

        fun insert(plan: Plan, immediately: Boolean = false) {
            insert(listOf(plan), immediately)
        }

        fun remove(servantId: Int, immediately: Boolean = false) {
            remove(listOf(servantId), immediately)
        }

        fun clear(immediately: Boolean = false) {
            exec(immediately) {
                database.value?.plansDao?.clear()
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
            exec(immediately) {
                database.value?.itemsDao?.update(items)
            }
        }

        fun update(item: Item, immediately: Boolean = false) {
            update(listOf(item), immediately)
        }

        fun deduct(itemsToDeduct: Collection<Item>, immediately: Boolean = false) {
            exec(immediately) {
                itemsToDeduct.map {
                    val itemInRepo = get(it.codename)
                    if (it.count > itemInRepo.count)
                        throw Exception("Number of item ${it.codename} to deduct is grater than that in repo. ")
                    Item(it.codename, itemInRepo.count - it.count)
                }
            }
        }

        fun clear(immediately: Boolean = false) {
            exec(immediately) {
                database.value?.itemsDao?.clear()
            }
        }
    }

    object EventRepo {
        val all: Map<String, Event>
            get() = runBlocking(Dispatchers.IO) {
                (database.value?.normalEventsDao?.all?.associate { it.codename to it }
                        ?: mapOf()) + (database.value?.lotteryEventsDao?.all?.associate { it.codename to it }
                        ?: mapOf())
            }

        val allAsLiveData: LiveData<Map<String, Event>> = object : MediatorLiveData<Map<String, Event>>() {
            private var latestNormalEvents: Map<String, NormalEvent> = mapOf()
                set(value) {
                    field = value
                    this.value = latestNormalEvents + latestLotteryEvents
                }
            private var latestLotteryEvents: Map<String, LotteryEvent> = mapOf()
                set(value) {
                    field = value
                    this.value = latestNormalEvents + latestLotteryEvents
                }

            init {
                addSource(database) { database ->
                    addSource(database?.normalEventsDao?.allAsLiveData ?: MutableLiveData()) {
                        latestNormalEvents = it.associate { event -> event.codename to event }
                    }
                    addSource(database?.lotteryEventsDao?.allAsLiveData ?: MutableLiveData()) {
                        latestLotteryEvents = it.associate { event -> event.codename to event }
                    }
                    Unit
                }
                value = mapOf()
            }
        }

        operator fun get(codename: String): Event? {
            return runBlocking(Dispatchers.IO) {
                database.value?.normalEventsDao?.get(codename)
                        ?: database.value?.lotteryEventsDao?.get(codename)
            }
        }

        fun insert(events: Collection<Event>, immediately: Boolean = false) {
            exec(immediately) {
                database.value?.normalEventsDao?.insert(events.mapNotNull { it as? NormalEvent })
                database.value?.lotteryEventsDao?.insert(events.mapNotNull { it as? LotteryEvent })
            }
        }

        fun remove(codenames: Collection<String>, immediately: Boolean = false) {
            exec(immediately) {
                database.value?.normalEventsDao?.remove(codenames.mapNotNull {
                    database.value?.normalEventsDao?.get(it)
                })
                database.value?.lotteryEventsDao?.remove(codenames.mapNotNull {
                    database.value?.lotteryEventsDao?.get(it)
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
            exec(immediately) {
                database.value?.normalEventsDao?.clear()
                database.value?.lotteryEventsDao?.clear()
            }
        }
    }
}