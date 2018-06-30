package com.ssttkkl.fgoplanningtool.data.plan;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Collection;
import java.util.List;

@Dao
public interface PlansDao {
    @Query("SELECT * FROM plan")
    List<PlanDatabaseEntity> getAllPlans();

    @Query("SELECT * FROM plan")
    LiveData<List<PlanDatabaseEntity>> getAllPlansAsLiveData();

    @Query("SELECT * FROM plan WHERE servantId=:servantId")
    PlanDatabaseEntity getPlanByServantId(int servantId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlan(PlanDatabaseEntity plan);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlan(Collection<PlanDatabaseEntity> plan);

    @Delete
    void delete(PlanDatabaseEntity plan);

    @Delete
    void delete(Collection<PlanDatabaseEntity> plans);
}
