package com.ssttkkl.fgoplanningtool.data.migration

import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorDatabase
import kotlinx.coroutines.experimental.runBlocking
import java.io.File

object DatabaseDescriptorDatabaseCommonMigration1 {
    fun migration(database: DatabaseDescriptorDatabase) {
        if (MyApp.context.databaseList().contains("database.db")) {
            val descriptor = DatabaseDescriptor.generate(MyApp.context.getString(R.string.databaseFromMigrationName))
            val path = MyApp.context.getDatabasePath("database.db")
            path.parentFile.listFiles { _, name -> name.contains("database.db") }.forEach { file ->
                file.renameTo(File(file.parent, file.name.replace("database", descriptor.uuid)))
            }

            runBlocking(Dispatchers.db) { database.databaseDescriptorDao.insert(listOf(descriptor)) }
        }
    }
}