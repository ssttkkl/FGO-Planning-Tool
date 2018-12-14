package com.ssttkkl.fgoplanningtool.data.event

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NormalEventDao {
    @get:Query("SELECT * FROM NormalEvent")
    val all: List<NormalEvent>

    @get:Query("SELECT * FROM NormalEvent")
    val allAsLiveData: LiveData<List<NormalEvent>>

    @Query("SELECT * FROM NormalEvent WHERE codename=:codename")
    fun get(codename: String): NormalEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(events: Collection<NormalEvent>)

    @Delete
    fun remove(events: Collection<NormalEvent>)

    @Query("DELETE FROM NormalEvent")
    fun clear()
}