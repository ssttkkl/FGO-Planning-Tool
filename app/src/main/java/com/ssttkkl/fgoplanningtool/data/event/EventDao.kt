package com.ssttkkl.fgoplanningtool.data.event

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {
    @get:Query("SELECT * FROM Event")
    val all: List<Event>

    @get:Query("SELECT * FROM Event")
    val allAsLiveData: LiveData<List<Event>>

    @Query("SELECT * FROM Event WHERE codename=:codename")
    fun get(codename: String): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(events: Collection<Event>)

    @Delete
    fun remove(events: Collection<Event>)

    @Query("DELETE FROM Event")
    fun clear()
}