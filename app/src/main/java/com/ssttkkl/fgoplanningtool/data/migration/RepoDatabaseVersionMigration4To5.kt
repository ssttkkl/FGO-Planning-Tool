package com.ssttkkl.fgoplanningtool.data.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

object RepoDatabaseVersionMigration4To5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `NormalEvent` (`codename` TEXT NOT NULL, `rerunAndParticipated` INTEGER NOT NULL, `checkedShopItems` TEXT NOT NULL, PRIMARY KEY(`codename`))")
        database.execSQL("CREATE TABLE IF NOT EXISTS `LotteryEvent` (`codename` TEXT NOT NULL, `rerunAndParticipated` INTEGER NOT NULL, `checkedShopItems` TEXT NOT NULL, `boxCount` INTEGER NOT NULL, PRIMARY KEY(`codename`))")
    }
}