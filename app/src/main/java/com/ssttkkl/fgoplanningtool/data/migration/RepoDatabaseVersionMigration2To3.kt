package com.ssttkkl.fgoplanningtool.data.migration

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object RepoDatabaseVersionMigration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table plan add dress text default '[]' not null")
    }
}