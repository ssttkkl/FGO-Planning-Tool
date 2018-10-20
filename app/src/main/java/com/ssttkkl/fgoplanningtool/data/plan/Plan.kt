package com.ssttkkl.fgoplanningtool.data.plan

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.os.Parcel
import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.data.utils.IntSetConverter
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant

@Entity(tableName = "Plan")
@TypeConverters(IntSetConverter::class)
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
                var dress: Set<Int>) : Parcelable {
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

    @Ignore
    constructor(servantId: Int) : this(
            servantId, 0,
            ConstantValues.getExp(ResourcesProvider.instance.servants[servantId]!!.stageMapToMaxLevel[4]),
            false, false,
            1, 1, 1,
            10, 10, 10,
            setOf())

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.createIntArray().toSet())

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
            plan.dress)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(servantId)
        parcel.writeInt(nowExp)
        parcel.writeInt(planExp)
        parcel.writeByte(if (ascendedOnNowStage) 1 else 0)
        parcel.writeByte(if (ascendedOnPlanStage) 1 else 0)
        parcel.writeInt(nowSkill1)
        parcel.writeInt(nowSkill2)
        parcel.writeInt(nowSkill3)
        parcel.writeInt(planSkill1)
        parcel.writeInt(planSkill2)
        parcel.writeInt(planSkill3)
        parcel.writeIntArray(dress.toIntArray())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Plan> {
        override fun createFromParcel(parcel: Parcel): Plan {
            return Plan(parcel)
        }

        override fun newArray(size: Int): Array<Plan?> {
            return arrayOfNulls(size)
        }
    }

}