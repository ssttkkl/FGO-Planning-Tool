package com.ssttkkl.fgoplanningtool.data.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

object RepoDatabaseVersionMigration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table plan add dress text default '[]' not null")
    }
}