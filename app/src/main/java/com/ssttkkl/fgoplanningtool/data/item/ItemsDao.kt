package com.ssttkkl.fgoplanningtool.data.item

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemsDao {
    @get:Query("SELECT * FROM Item")
    val all: List<Item>

    @get:Query("SELECT * FROM Item")
    val allAsLiveData: LiveData<List<Item>>

    @Query("SELECT * FROM Item WHERE codename=:codename")
    fun get(codename: String): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: Collection<Item>)

    @Query("DELETE FROM Item")
    fun clear()
}
