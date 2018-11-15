package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDescriptorDao {
    @get:Query("SELECT * FROM DatabaseDescriptor")
    val all: List<DatabaseDescriptor>

    @get:Query("SELECT * FROM DatabaseDescriptor")
    val allLiveData: LiveData<List<DatabaseDescriptor>>

    @Query("SELECT * FROM DatabaseDescriptor WHERE uuid=:uuid")
    fun getByUUID(uuid: String): DatabaseDescriptor?

    @Query("SELECT * FROM DatabaseDescriptor WHERE uuid=:uuid")
    fun getLiveDataByUUID(uuid: String): LiveData<DatabaseDescriptor>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(descriptor: Collection<DatabaseDescriptor>)

    @Update
    fun update(descriptor: DatabaseDescriptor)

    @Delete
    fun remove(descriptor: DatabaseDescriptor)

    @Delete
    fun remove(descriptors: Collection<DatabaseDescriptor>)
}