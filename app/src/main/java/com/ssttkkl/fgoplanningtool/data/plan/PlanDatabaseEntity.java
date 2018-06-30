package com.ssttkkl.fgoplanningtool.data.plan;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Plan")
public class PlanDatabaseEntity {
    @PrimaryKey
    public int servantId;

    public int nowStage, planStage;

    public int nowSkill1, nowSkill2, nowSkill3;

    public int planSkill1, planSkill2, planSkill3;

    public PlanDatabaseEntity(int servantId, int nowStage, int planStage, int nowSkill1, int nowSkill2, int nowSkill3, int planSkill1, int planSkill2, int planSkill3) {
        this.servantId = servantId;
        this.nowStage = nowStage;
        this.planStage = planStage;
        this.nowSkill1 = nowSkill1;
        this.nowSkill2 = nowSkill2;
        this.nowSkill3 = nowSkill3;
        this.planSkill1 = planSkill1;
        this.planSkill2 = planSkill2;
        this.planSkill3 = planSkill3;
    }

    @Ignore
    public PlanDatabaseEntity(Plan plan) {
        servantId = plan.getServantId();
        nowStage = plan.getNowStage();
        planStage = plan.getPlanStage();
        nowSkill1 = plan.getNowSkill1();
        planSkill1 = plan.getPlanSkill1();
        nowSkill2 = plan.getNowSkill2();
        planSkill2 = plan.getPlanSkill2();
        nowSkill3 = plan.getNowSkill3();
        planSkill3 = plan.getPlanSkill3();
    }

    public Plan getPlan() {
        return new Plan(servantId, nowStage, planStage, nowSkill1, nowSkill2, nowSkill3, planSkill1, planSkill2, planSkill3);
    }
}