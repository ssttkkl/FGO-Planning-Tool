package com.ssttkkl.fgoplanningtool.data.plan

import android.arch.lifecycle.Observer
import android.util.Log
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.data.perform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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

    operator fun get(servantID: Int): Plan? {
        return cache[servantID]
                ?: runBlocking(Dispatchers.IO) { database.plansDao.getByServantID(servantID) }
    }

    fun insert(plans: Collection<Plan>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.plansDao.insert(plans)
        }
    }

    fun remove(servantIDs: Collection<Int>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            servantIDs.forEach { servantID ->
                database.plansDao.remove(database.plansDao.getByServantID(servantID)!!)
            }
        }
    }

    fun clear(howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.plansDao.remove(database.plansDao.all)
        }
    }

    fun insert(plan: Plan, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        insert(listOf(plan), howToPerform)
    }

    fun remove(servantID: Int, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        remove(listOf(servantID), howToPerform)
    }
}