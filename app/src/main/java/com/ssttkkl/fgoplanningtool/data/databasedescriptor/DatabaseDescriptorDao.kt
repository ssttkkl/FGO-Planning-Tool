package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import android.arch.persistence.room.*

@Dao
interface DatabaseDescriptorDao {
    @get:Query("SELECT * FROM DatabaseDescriptor")
    val all: List<DatabaseDescriptor>

    @Query("SELECT * FROM DatabaseDescriptor WHERE uuid=:uuid")
    fun getByUUID(uuid: String): DatabaseDescriptor?

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(descriptor: Collection<DatabaseDescriptor>)

    @Delete
    fun remove(descriptor: DatabaseDescriptor)

    @Delete
    fun remove(descriptors: Collection<DatabaseDescriptor>)
}