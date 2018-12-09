package com.ssttkkl.fgoplanningtool.data.event

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LotteryEventDao {
    @get:Query("SELECT * FROM LotteryEvent")
    val all: List<LotteryEvent>

    @get:Query("SELECT * FROM LotteryEvent")
    val allLiveData: LiveData<List<LotteryEvent>>

    @Query("SELECT * FROM LotteryEvent WHERE codename=:codename")
    fun getByCodename(codename: String): LotteryEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(events: Collection<LotteryEvent>)

    @Delete
    fun remove(event: LotteryEvent)

    @Delete
    fun remove(events: Collection<LotteryEvent>)
}