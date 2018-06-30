package com.ssttkkl.fgoplanningtool.data.plan

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.ssttkkl.fgoplanningtool.data.MyDatabase

object PlansRepository {
    private val database = MyDatabase.getInstance()

    private var cache: MutableMap<Int, PlanDatabaseEntity> = HashMap()

    fun getAll(): List<Plan> {
        if (cache.isEmpty()) {
            cache = database.plansDao().allPlans.associateTo(HashMap()) { Pair(it.servantId, it) }
        }
        return cache.values.map { it.plan }
    }

    private fun getEntity(servantId: Int): PlanDatabaseEntity? {
        if (!cache.contains(servantId)) {
            val cachedPlan = database.plansDao().getPlanByServantId(servantId)
            if (cachedPlan != null)
                cache[servantId] = cachedPlan
        }
        return cache[servantId]
    }

    fun get(servantId: Int): Plan? = getEntity(servantId)?.plan

    fun insert(plan: Plan) {
        PlanDatabaseEntity(plan).also {
            database.plansDao().insertPlan(it)
            cache[it.servantId] = it
        }
    }

    fun insert(plans: Collection<Plan>) {
        plans.map { PlanDatabaseEntity(it) }.also {
            database.plansDao().insertPlan(it)
            it.forEach { cache[it.servantId] = it }
        }
    }

    fun remove(servantId: Int) {
        database.plansDao().delete(getEntity(servantId))
        cache.remove(servantId)
    }

    fun remove(servantId: Collection<Int>) {
        database.plansDao().delete(servantId.map { getEntity(it) })
        servantId.forEach { cache.remove(it) }
    }

    fun clear() {
        cache.values.forEach {
            database.plansDao().delete(it)
        }
        cache.clear()
    }

    fun observeAllPlanList(owner: LifecycleOwner, observer: Observer<List<Plan>>) {
        database.plansDao().allPlansAsLiveData.observe(owner, Observer {
            observer.onChanged(it?.map { it.plan })
        })
    }

    fun observeAllPlanListForever(observer: Observer<List<Plan>>) {
        database.plansDao().allPlansAsLiveData.observeForever {
            observer.onChanged(it?.map { it.plan })
        }
    }
}