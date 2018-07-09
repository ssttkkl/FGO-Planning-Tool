package com.ssttkkl.fgoplanningtool.data.item

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

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
