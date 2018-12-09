package com.ssttkkl.fgoplanningtool.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.EventRepo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.ItemRepo
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.data.plan.PlanRepo

object Repo {
    var uuid = DatabaseDescriptorManager.firstOrCreate.uuid
        private set

    val databaseDescriptor
        get() = DatabaseDescriptorManager[uuid]

    private var database = RepoDatabase.getInstance(uuid)

    val planListLiveData: LiveData<List<Plan>> = MediatorLiveData<List<Plan>>()

    var planRepo: PlanRepo = PlanRepo(database).apply {
        (planListLiveData as MediatorLiveData<List<Plan>>).addSource(liveData) { planListLiveData.value = it }
    }
        set(value) {
            (planListLiveData as MediatorLiveData<List<Plan>>).apply {
                removeSource(field.liveData)
                addSource(value.liveData) { planListLiveData.value = it }
            }
            field = value
        }

    val itemListLiveData: LiveData<List<Item>> = MediatorLiveData<List<Item>>()

    var itemRepo: ItemRepo = ItemRepo(database).apply {
        (itemListLiveData as MediatorLiveData<List<Item>>).addSource(liveData) { itemListLiveData.value = it }
    }
        set(value) {
            (itemListLiveData as MediatorLiveData<List<Item>>).apply {
                removeSource(field.liveData)
                addSource(value.liveData) { itemListLiveData.value = it }
            }
            field = value
        }

    val eventListLiveData: LiveData<Collection<Event>> = MediatorLiveData<Collection<Event>>()

    var eventRepo: EventRepo = EventRepo(database).apply {
        (eventListLiveData as MediatorLiveData<Collection<Event>>).addSource(liveData) { eventListLiveData.value = it }
    }
        set(value) {
            (eventListLiveData as MediatorLiveData<Collection<Event>>).apply {
                removeSource(field.liveData)
                addSource(value.liveData) { eventListLiveData.value = it }
            }
            field = value
        }

    val databaseDescriptorLiveData: LiveData<DatabaseDescriptor> = MediatorLiveData<DatabaseDescriptor>()

    private var databaseDescriptorLiveDataFromManager = DatabaseDescriptorManager.getLiveData(uuid).apply {
        (databaseDescriptorLiveData as MediatorLiveData<DatabaseDescriptor>).addSource(this) { databaseDescriptorLiveData.value = it }
    }
        set(value) {
            (databaseDescriptorLiveData as MediatorLiveData<DatabaseDescriptor>).apply {
                removeSource(field)
                addSource(value) { databaseDescriptorLiveData.value = it }
            }
            field = value
        }

    fun switchDatabase(newUUID: String) {
        database = RepoDatabase.getInstance(newUUID)
        uuid = newUUID
        databaseDescriptorLiveDataFromManager = DatabaseDescriptorManager.getLiveData(newUUID)

        planRepo = PlanRepo(database)
        itemRepo = ItemRepo(database)
        Log.d("Repo", "Database switched to $uuid.")
    }
}