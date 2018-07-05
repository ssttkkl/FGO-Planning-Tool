package com.ssttkkl.fgoplanningtool.data.plan

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

class PlanRepo(private val database: RepoDatabase) {
    private val cache = ConcurrentHashMap<Int, Plan>()

    val liveData = MutableLiveData<List<Plan>>()

    init {
        Log.d("PlanRepo", "Loading database...")
        cache.putAll(runBlocking(Dispatchers.db) { database.plansDao.all }
                .map { Pair(it.servantId, it) })
        liveData.postValue(all)
        Log.d("PlanRepo", "Database loaded.")
    }

    operator fun get(servantId: Int): Plan? {
        return cache[servantId]
    }

    val all: List<Plan>
        get() = cache.values.sortedBy { it.servantId }

    private fun performTransaction(action: () -> Unit) {
        runBlocking(Dispatchers.db) {
            action.invoke()
        }
        liveData.postValue(all)
    }

    private fun performTransactionAsync(action: () -> Unit) =
            async(Dispatchers.db) {
                action.invoke()
                liveData.postValue(all)
            }

    private fun insertImpl(plans: Collection<Plan>) {
        plans.forEach { plan ->
            cache[plan.servantId] = plan
        }
        database.plansDao.insert(plans)
    }

    fun insert(plans: Collection<Plan>) = performTransaction { insertImpl(plans) }

    fun insertAsync(plans: Collection<Plan>) = performTransactionAsync { insertImpl(plans) }

    private fun removeImpl(servantIDs: Collection<Int>) {
        servantIDs.forEach { servantID ->
            cache.remove(servantID)
            val plan = database.plansDao.getByServantID(servantID)
            if (plan != null)
                database.plansDao.remove(plan)
        }
    }

    fun remove(servantIDs: Collection<Int>) = performTransaction { removeImpl(servantIDs) }

    fun removeAsync(servantIDs: Collection<Int>) = performTransactionAsync { removeImpl(servantIDs) }

    private fun clearImpl() {
        cache.clear()
        database.plansDao.remove(database.plansDao.all)
    }

    fun clear() = performTransaction { clearImpl() }

    fun clearAsync() = performTransactionAsync { clearImpl() }

    fun insert(plan: Plan) {
        insert(listOf(plan))
    }

    fun insertAsync(plan: Plan) {
        insertAsync(listOf(plan))
    }

    fun remove(servantID: Int) {
        remove(listOf(servantID))
    }

    fun removeAsync(servantID: Int) {
        removeAsync(listOf(servantID))
    }
}