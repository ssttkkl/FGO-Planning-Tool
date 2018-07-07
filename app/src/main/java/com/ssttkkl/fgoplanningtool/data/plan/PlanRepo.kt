package com.ssttkkl.fgoplanningtool.data.plan

import android.arch.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.ConcurrentHashMap

class PlanRepo(private val database: RepoDatabase) : Observer<List<Plan>> {
    private val cache = ConcurrentHashMap<Int, Plan>()

    val liveData = database.plansDao.allLiveData
            .apply { observeForever(this@PlanRepo) }

    override fun onChanged(t: List<Plan>?) {
        synchronized(cache) {
            cache.clear()
            if (t != null)
                cache.putAll(t.associate { Pair(it.servantId, it) })
        }
        Log.d("PlanRepo", "Database updated.")
    }

    val all: List<Plan>
        get() = cache.values.sortedBy { it.servantId }

    operator fun get(servantId: Int) = cache[servantId]

    private fun performTransication(action: () -> Unit) = runBlocking(Dispatchers.db) { action.invoke() }

    private fun performTransicationAsync(action: () -> Unit) = async(Dispatchers.db) { action.invoke() }

    private fun insertImpl(plans: Collection<Plan>) {
        database.plansDao.insert(plans)
    }

    fun insert(plans: Collection<Plan>) = performTransication { insertImpl(plans) }

    fun insertAsync(plans: Collection<Plan>) = performTransicationAsync { insertImpl(plans) }

    private fun removeImpl(servantIDs: Collection<Int>) {
        servantIDs.forEach { servantID ->
            database.plansDao.remove(database.plansDao.getByServantID(servantID)!!)
        }
    }

    fun remove(servantIDs: Collection<Int>) = performTransication { removeImpl(servantIDs) }

    fun removeAsync(servantIDs: Collection<Int>) = performTransicationAsync { removeImpl(servantIDs) }

    private fun clearImpl() {
        database.plansDao.remove(database.plansDao.all)
    }

    fun clear() = performTransication { clearImpl() }

    fun clearAsync() = performTransicationAsync { clearImpl() }

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