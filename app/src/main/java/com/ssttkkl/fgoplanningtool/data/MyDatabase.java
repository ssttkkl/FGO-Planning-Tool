package com.ssttkkl.fgoplanningtool.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.ssttkkl.fgoplanningtool.MyApp;
import com.ssttkkl.fgoplanningtool.data.item.ItemDatabaseEntity;
import com.ssttkkl.fgoplanningtool.data.item.ItemsDao;
import com.ssttkkl.fgoplanningtool.data.plan.PlanDatabaseEntity;
import com.ssttkkl.fgoplanningtool.data.plan.PlansDao;

import org.jetbrains.annotations.NotNull;

@Database(entities = {PlanDatabaseEntity.class, ItemDatabaseEntity.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract PlansDao plansDao();

    public abstract ItemsDao itemsDao();

    private static String databaseFilename = "database.db";

    private static MyDatabase INSTANCE = null;

    @NotNull
    public static MyDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (MyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(MyApp.getContext(),
                            MyDatabase.class,
                            databaseFilename
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}