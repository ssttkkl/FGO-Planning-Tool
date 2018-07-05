package com.ssttkkl.fgoplanningtool.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan

object Repo {
    private var uuid: String

    init {
        var descriptor = DatabaseDescriptorManager.getAll().firstOrNull()
        if (descriptor == null) {
            descriptor = DatabaseDescriptor.generate(MyApp.context.getString(R.string.new_database_name))
            DatabaseDescriptorManager.insert(descriptor)
        }
        uuid = descriptor.uuid
    }

    val databaseDescriptor
        get() = DatabaseDescriptorManager.get(uuid)

    private var database: RepoDatabase

    var planRepo: PlanRepo

    private val planListObserver = Observer<List<Plan>> {
        (planListLiveData as MutableLiveData<List<Plan>>).postValue(it)
    }

    var itemRepo: ItemRepo

    private val itemListObserver = Observer<List<Item>> {
        (itemListLiveData as MutableLiveData<List<Item>>).postValue(it)
    }

    init {
        database = RepoDatabase.getInstances(uuid)
        planRepo = PlanRepo(database).apply {
            liveData.observeForever(planListObserver)
        }
        itemRepo = ItemRepo(database).apply {
            liveData.observeForever(itemListObserver)
        }
    }

    val planListLiveData: LiveData<List<Plan>> = MutableLiveData<List<Plan>>().apply {
        planRepo.liveData.observeForever(planListObserver)
    }

    val itemListLiveData: LiveData<List<Item>> = MutableLiveData<List<Item>>().apply {
        itemRepo.liveData.observeForever(itemListObserver)
    }

    fun switchDatabase(newUUID: String) {
        database = RepoDatabase.getInstances(newUUID)
        uuid = newUUID

        planRepo.liveData.removeObserver(planListObserver)
        planRepo = PlanRepo(database).apply {
            liveData.observeForever(planListObserver)
        }

        itemRepo.liveData.removeObserver(itemListObserver)
        itemRepo = ItemRepo(database).apply {
            liveData.observeForever(itemListObserver)
        }

        Log.d("Repo", "Database switched to $uuid.")
    }
}