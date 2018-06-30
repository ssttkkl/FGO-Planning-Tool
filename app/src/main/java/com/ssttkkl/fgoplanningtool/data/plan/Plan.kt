package com.ssttkkl.fgoplanningtool.data.plan

import android.os.Parcel
import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant

data class Plan(var servantId: Int,
                var nowStage: Int,
                var planStage: Int,
                var nowSkill1: Int,
                var nowSkill2: Int,
                var nowSkill3: Int,
                var planSkill1: Int,
                var planSkill2: Int,
                var planSkill3: Int) : Parcelable {
    val servant: Servant
        get() = ResourcesProvider.servants[servantId]!!

    constructor(plan: Plan) : this(plan.servantId,
            plan.nowStage,
            plan.planStage,
            plan.nowSkill1,
            plan.nowSkill2,
            plan.nowSkill3,
            plan.planSkill1,
            plan.planSkill2,
            plan.planSkill3)

    constructor(parcel: Parcel) : this(parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(servantId)
        dest.writeInt(nowStage)
        dest.writeInt(planStage)
        dest.writeInt(nowSkill1)
        dest.writeInt(nowSkill2)
        dest.writeInt(nowSkill3)
        dest.writeInt(planSkill1)
        dest.writeInt(planSkill2)
        dest.writeInt(planSkill3)
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