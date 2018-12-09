package com.ssttkkl.fgoplanningtool.data.event

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NormalEventDao {
    @get:Query("SELECT * FROM NormalEvent")
    val all: List<NormalEvent>

    @get:Query("SELECT * FROM NormalEvent")
    val allLiveData: LiveData<List<NormalEvent>>

    @Query("SELECT * FROM NormalEvent WHERE codename=:codename")
    fun getByCodename(codename: String): NormalEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(events: Collection<NormalEvent>)

    @Delete
    fun remove(event: NormalEvent)

    @Delete
    fun remove(events: Collection<NormalEvent>)
}