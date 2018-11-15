package com.ssttkkl.fgoplanningtool.data.plan

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlansDao {
    @get:Query("SELECT * FROM Plan")
    val all: List<Plan>

    @get:Query("SELECT * FROM Plan")
    val allLiveData: LiveData<List<Plan>>

    @Query("SELECT * FROM Plan WHERE servantID=:servantID")
    fun getByServantID(servantID: Int): Plan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plan: Collection<Plan>)

    @Delete
    fun remove(plan: Plan)

    @Delete
    fun remove(plans: Collection<Plan>)
}
