package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDescriptorDao {
    @get:Query("SELECT * FROM DatabaseDescriptor")
    val all: List<DatabaseDescriptor>

    @get:Query("SELECT * FROM DatabaseDescriptor")
    val allAsLiveData: LiveData<List<DatabaseDescriptor>>

    @Query("SELECT * FROM DatabaseDescriptor WHERE uuid=:uuid")
    fun get(uuid: String?): DatabaseDescriptor?

    @Query("SELECT * FROM DatabaseDescriptor WHERE uuid=:uuid")
    fun getAsLiveData(uuid: String?): LiveData<DatabaseDescriptor?>

    @Query("SELECT * FROM DatabaseDescriptor WHERE name=:name")
    fun getByName(name: String?): DatabaseDescriptor?

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(descriptor: Collection<DatabaseDescriptor>)

    @Update
    fun update(descriptor: DatabaseDescriptor)

    @Delete
    fun remove(descriptor: DatabaseDescriptor)

    @Delete
    fun remove(descriptors: Collection<DatabaseDescriptor>)
}