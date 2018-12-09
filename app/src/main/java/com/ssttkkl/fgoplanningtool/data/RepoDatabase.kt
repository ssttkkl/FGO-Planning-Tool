package com.ssttkkl.fgoplanningtool.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.data.event.*
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.ItemsDao
import com.ssttkkl.fgoplanningtool.data.migration.*
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.data.plan.PlansDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Plan::class, Item::class, NormalEvent::class, LotteryEvent::class], version = 5)
abstract class RepoDatabase : RoomDatabase() {
    abstract val plansDao: PlansDao
    abstract val itemsDao: ItemsDao
    abstract val normalEventsDao: NormalEventDao
    abstract val lotteryEventsDao: LotteryEventDao

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
                                        RepoDatabaseVersionMigration3To4,
                                        RepoDatabaseVersionMigration4To5).build()
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