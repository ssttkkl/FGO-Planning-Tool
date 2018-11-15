package com.ssttkkl.fgoplanningtool.data.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

object RepoDatabaseVersionMigration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table item rename to item_old")
        database.execSQL("CREATE TABLE IF NOT EXISTS `item` (`codename` TEXT NOT NULL, `count` INTEGER NOT NULL, PRIMARY KEY(`codename`))")
        database.query("select * from item_old").use {
            val colCodename = it.getColumnIndexOrThrow("codename")
            val colCount = it.getColumnIndexOrThrow("count")
            it.moveToFirst()
            while (it.moveToNext()) {
                val cv = ContentValues()
                cv.put("codename", it.getString(colCodename))
                cv.put("count", it.getInt(colCount))
                database.insert("item", SQLiteDatabase.CONFLICT_IGNORE, cv)
            }
        }
        database.execSQL("drop table item_old")
    }
}