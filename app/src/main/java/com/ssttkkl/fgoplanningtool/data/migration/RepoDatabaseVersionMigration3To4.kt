package com.ssttkkl.fgoplanningtool.data.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ssttkkl.fgoplanningtool.resources.LevelValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

object RepoDatabaseVersionMigration3To4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table plan rename to plan_old")
        database.execSQL("CREATE TABLE IF NOT EXISTS `plan` (" +
                "`servantId` INTEGER NOT NULL, " +
                "`nowExp` INTEGER NOT NULL, " +
                "`planExp` INTEGER NOT NULL, " +
                "`ascendedOnNowStage` INTEGER NOT NULL, " +
                "`ascendedOnPlanStage` INTEGER NOT NULL, " +
                "`nowSkill1` INTEGER NOT NULL, " +
                "`nowSkill2` INTEGER NOT NULL, " +
                "`nowSkill3` INTEGER NOT NULL, " +
                "`planSkill1` INTEGER NOT NULL, " +
                "`planSkill2` INTEGER NOT NULL, " +
                "`planSkill3` INTEGER NOT NULL, " +
                "`dress` TEXT NOT NULL, " +
                "PRIMARY KEY(`servantId`))")
        database.query("select * from plan_old").use {
            val colServantId = it.getColumnIndexOrThrow("servantId")
            val colNowStage = it.getColumnIndexOrThrow("nowStage")
            val colPlanStage = it.getColumnIndexOrThrow("planStage")
            val colNowSkill1 = it.getColumnIndexOrThrow("nowSkill1")
            val colNowSkill2 = it.getColumnIndexOrThrow("nowSkill2")
            val colNowSkill3 = it.getColumnIndexOrThrow("nowSkill3")
            val colPlanSkill1 = it.getColumnIndexOrThrow("planSkill1")
            val colPlanSkill2 = it.getColumnIndexOrThrow("planSkill2")
            val colPlanSkill3 = it.getColumnIndexOrThrow("planSkill3")
            val dress = it.getColumnIndexOrThrow("dress")
            it.moveToFirst()
            while (it.moveToNext()) {
                val servantID = it.getInt(colServantId)
                val servant = ResourcesProvider.instance.servants[servantID]
                val nowLevel = if (servant != null) LevelValues.stageMapToMaxLevel[servant.star][it.getInt(colNowStage)] else 0
                val planLevel = if (servant != null) LevelValues.stageMapToMaxLevel[servant.star][it.getInt(colPlanStage)] else 0

                val cv = ContentValues()
                cv.put("servantId", servantID)
                cv.put("nowSkill1", it.getInt(colNowSkill1))
                cv.put("nowSkill2", it.getInt(colNowSkill2))
                cv.put("nowSkill3", it.getInt(colNowSkill3))
                cv.put("planSkill1", it.getInt(colPlanSkill1))
                cv.put("planSkill2", it.getInt(colPlanSkill2))
                cv.put("planSkill3", it.getInt(colPlanSkill3))
                cv.put("nowExp", LevelValues.levelToExp(nowLevel))
                cv.put("planExp", LevelValues.levelToExp(planLevel))
                cv.put("ascendedOnNowStage", false)
                cv.put("ascendedOnPlanStage", false)
                cv.put("dress", it.getString(dress))
                database.insert("plan", SQLiteDatabase.CONFLICT_IGNORE, cv)
            }
        }
        database.execSQL("drop table plan_old")
    }
}