package com.ssttkkl.fgoplanningtool.data.item

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemsDao {
    @get:Query("SELECT * FROM item")
    val all: List<Item>

    @get:Query("SELECT * FROM item")
    val allLiveData: LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE codename=:codename")
    fun getByName(codename: String): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: Collection<Item>)
}
