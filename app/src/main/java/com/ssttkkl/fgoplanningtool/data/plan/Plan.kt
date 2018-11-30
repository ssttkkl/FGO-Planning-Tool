package com.ssttkkl.fgoplanningtool.data.plan

import android.os.Parcelable
import androidx.room.*
import com.ssttkkl.fgoplanningtool.data.utils.IntSetConverter
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Dress
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Plan")
@TypeConverters(IntSetConverter::class)
@Parcelize
data class Plan(@PrimaryKey var servantId: Int,
                var nowExp: Int,
                var planExp: Int,
                var ascendedOnNowStage: Boolean,
                var ascendedOnPlanStage: Boolean,
                var nowSkill1: Int,
                var nowSkill2: Int,
                var nowSkill3: Int,
                var planSkill1: Int,
                var planSkill2: Int,
                var planSkill3: Int,
                @ColumnInfo(name = "dress") var dressID: Set<Int>) : Parcelable {
    val servant: Servant?
        get() = ResourcesProvider.instance.servants[servantId]

    val nowLevel
        get() = ConstantValues.getLevel(nowExp)

    val planLevel
        get() = ConstantValues.getLevel(planExp)

    val nowStage
        get() = ConstantValues.getStage(servant!!.star, nowLevel) + if (ascendedOnNowStage) 1 else 0

    val planStage
        get() = ConstantValues.getStage(servant!!.star, planLevel) + if (ascendedOnPlanStage) 1 else 0

    var dress: Set<Dress>
        get() = dressID.mapNotNull { servant?.dress?.get(it) }.toSet()
        set(value) {
            dressID = value.map { servant!!.dress.indexOf(it) }.toSet()
        }

    @Ignore
    constructor(servantId: Int) : this(
            servantId, 0,
            ConstantValues.getExp(ResourcesProvider.instance.servants[servantId]!!.stageMapToMaxLevel[4]),
            false, false,
            1, 1, 1,
            10, 10, 10,
            setOf())

    @Ignore
    constructor(plan: Plan) : this(plan.servantId,
            plan.nowExp,
            plan.planExp,
            plan.ascendedOnNowStage,
            plan.ascendedOnPlanStage,
            plan.nowSkill1,
            plan.nowSkill2,
            plan.nowSkill3,
            plan.planSkill1,
            plan.planSkill2,
            plan.planSkill3,
            plan.dressID)
}