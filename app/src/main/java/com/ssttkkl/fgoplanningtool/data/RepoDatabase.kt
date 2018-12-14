package com.ssttkkl.fgoplanningtool.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.LotteryEventDao
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEventDao
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.ItemsDao
import com.ssttkkl.fgoplanningtool.data.migration.RepoDatabaseVersionMigration1To2
import com.ssttkkl.fgoplanningtool.data.migration.RepoDatabaseVersionMigration2To3
import com.ssttkkl.fgoplanningtool.data.migration.RepoDatabaseVersionMigration3To4
import com.ssttkkl.fgoplanningtool.data.migration.RepoDatabaseVersionMigration4To5
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

        fun getInstance(uuid: String): RepoDatabase {
            if (!instances.containsKey(uuid)) {
                synchronized(instances) {
                    if (!instances.containsKey(uuid))
                        instances[uuid] = Room.databaseBuilder(MyApp.context,
                                RepoDatabase::class.java, "$uuid.db")
                                .addMigrations(RepoDatabaseVersionMigration1To2,
                                        RepoDatabaseVersionMigration2To3,
                                        RepoDatabaseVersionMigration3To4,
                                        RepoDatabaseVersionMigration4To5).build()
                }
            }
            return instances[uuid]!!
        }

        fun dispose(uuid: String?) {
            if (instances.containsKey(uuid)) {
                synchronized(instances) {
                    if (instances.containsKey(uuid)) {
                        instances[uuid]!!.close()
                        instances.remove(uuid)
                    }
                }
            }
        }

        fun removeDatabaseFile(uuid: String) {
            dispose(uuid)
            GlobalScope.launch(Dispatchers.IO) { MyApp.context.deleteDatabase("$uuid.db") }
        }
    }
}