package com.ssttkkl.fgoplanningtool.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
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

    val planListLiveData: LiveData<List<Plan>> = MutableLiveData<List<Plan>>()

    private val planListObserver = Observer<List<Plan>> {
        (planListLiveData as MutableLiveData<List<Plan>>).postValue(it)
    }

    var planRepo: PlanRepo = PlanRepo(database)
            .apply { liveData.observeForever(planListObserver) }
        set(value) {
            field.liveData.removeObserver(planListObserver)
            value.liveData.observeForever(planListObserver)
            field = value
        }

    val itemListLiveData: LiveData<List<Item>> = MutableLiveData<List<Item>>()

    private val itemListObserver = Observer<List<Item>> {
        (itemListLiveData as MutableLiveData<List<Item>>).postValue(it)
    }

    var itemRepo: ItemRepo = ItemRepo(database)
            .apply { liveData.observeForever(itemListObserver) }
        set(value) {
            field.liveData.removeObserver(itemListObserver)
            value.liveData.observeForever(itemListObserver)
            field = value
        }

    val databaseDescriptorLiveData: LiveData<DatabaseDescriptor> = MutableLiveData<DatabaseDescriptor>()

    private val databaseDescriptorObserver = Observer<DatabaseDescriptor> {
        (databaseDescriptorLiveData as MutableLiveData<DatabaseDescriptor>).postValue(it)
    }

    private var databaseDescriptorLiveDataFromManager = DatabaseDescriptorManager.getLiveData(uuid)
            .apply { observeForever(databaseDescriptorObserver) }
        set(value) {
            field.removeObserver(databaseDescriptorObserver)
            value.observeForever(databaseDescriptorObserver)
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