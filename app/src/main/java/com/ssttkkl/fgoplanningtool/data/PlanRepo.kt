package com.ssttkkl.fgoplanningtool.data

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

class PlanRepo(private val database: RepoDatabase) {
    private val cache = ConcurrentHashMap<Int, Plan>()

    internal val liveData = MutableLiveData<List<Plan>>()

    init {
        Log.d("PlanRepo", "Loading database...")
        cache.putAll(runBlocking(Dispatchers.db) { database.plansDao.all }
                .map { Pair(it.servantId, it) })
        liveData.postValue(getAll())
        Log.d("PlanRepo", "Database loaded.")
    }

    private fun performTransaction(action: () -> Unit) {
        action.invoke()
        liveData.postValue(getAll())
    }

    fun get(servantId: Int): Plan? {
        return cache[servantId]
    }

    fun getAll(): List<Plan> {
        return cache.values.sortedBy { it.servantId }
    }

    fun insert(plans: Collection<Plan>) = performTransaction {
        plans.forEach { plan ->
            cache[plan.servantId] = plan
        }
        database.plansDao.insert(plans)
    }

    fun remove(servantIDs: Collection<Int>) = performTransaction {
        servantIDs.forEach { servantID ->
            cache.remove(servantID)
            val plan = database.plansDao.getByServantID(servantID)
            if (plan != null)
                database.plansDao.remove(plan)
        }
    }

    fun clear() = performTransaction {
        cache.clear()
        database.plansDao.remove(database.plansDao.all)
    }

    fun insert(plan: Plan) {
        insert(listOf(plan))
    }

    fun remove(servantID: Int) {
        remove(listOf(servantID))
    }
}