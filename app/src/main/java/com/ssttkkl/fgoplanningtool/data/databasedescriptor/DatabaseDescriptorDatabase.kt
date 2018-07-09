package com.ssttkkl.fgoplanningtool.data.databasedescriptor

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.ssttkkl.fgoplanningtool.MyApp

@Database(entities = [DatabaseDescriptor::class], version = 1)
abstract class DatabaseDescriptorDatabase : RoomDatabase() {
    abstract val databaseDescriptorDao: DatabaseDescriptorDao

    companion object {
        private var INSTANCE: DatabaseDescriptorDatabase? = null

        val instance: DatabaseDescriptorDatabase
            get() {
                if (INSTANCE == null) {
                    synchronized(DatabaseDescriptorDatabase.Companion::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = Room.databaseBuilder(MyApp.context,
                                    DatabaseDescriptorDatabase::class.java, "repo_descriptor.db").build()
                        }
                    }
                }
                return INSTANCE!!
            }
    }
}