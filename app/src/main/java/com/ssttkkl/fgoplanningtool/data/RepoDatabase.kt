package com.ssttkkl.fgoplanningtool.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.ItemsDao
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.data.plan.PlansDao

@Database(entities = [Plan::class, Item::class], version = 2)
abstract class RepoDatabase : RoomDatabase() {
    abstract val plansDao: PlansDao
    abstract val itemsDao: ItemsDao

    companion object {
        private val instances = HashMap<String, RepoDatabase>()

        fun getInstances(uuid: String): RepoDatabase {
            if (!instances.containsKey(uuid))
                synchronized(instances) {
                    if (!instances.containsKey(uuid))
                        instances[uuid] = Room.databaseBuilder(MyApp.context,
                                RepoDatabase::class.java, "$uuid.db").build()
                }
            return instances[uuid]!!
        }
    }
}