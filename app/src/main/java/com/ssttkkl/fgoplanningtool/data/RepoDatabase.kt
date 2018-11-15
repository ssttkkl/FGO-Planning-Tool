package com.ssttkkl.fgoplanningtool.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.ItemsDao
import com.ssttkkl.fgoplanningtool.data.migration.RepoDatabaseVersionMigration1To2
import com.ssttkkl.fgoplanningtool.data.migration.RepoDatabaseVersionMigration2To3
import com.ssttkkl.fgoplanningtool.data.migration.RepoDatabaseVersionMigration3To4
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.data.plan.PlansDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Plan::class, Item::class], version = 4)
abstract class RepoDatabase : RoomDatabase() {
    abstract val plansDao: PlansDao
    abstract val itemsDao: ItemsDao

    companion object {
        private val instances = HashMap<String, RepoDatabase>()

        fun getInstance(filename: String): RepoDatabase {
            if (!instances.containsKey(filename)) {
                synchronized(instances) {
                    if (!instances.containsKey(filename))
                        instances[filename] = Room.databaseBuilder(MyApp.context,
                                RepoDatabase::class.java, "$filename.db")
                                .addMigrations(RepoDatabaseVersionMigration1To2,
                                        RepoDatabaseVersionMigration2To3,
                                        RepoDatabaseVersionMigration3To4).build()
                }
            }
            return instances[filename]!!
        }

        fun dispose(filename: String) {
            if (instances.containsKey(filename)) {
                synchronized(instances) {
                    if (instances.containsKey(filename)) {
                        instances[filename]!!.close()
                        instances.remove(filename)
                    }
                }
            }
        }

        fun remove(filename: String) {
            dispose(filename)
            GlobalScope.launch(Dispatchers.IO) { MyApp.context.deleteDatabase("$filename.db") }
        }
    }
}