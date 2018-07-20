package com.ssttkkl.fgoplanningtool.data.plan

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.os.Parcel
import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.data.utils.IntSetConverter
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant

@Entity(tableName = "Plan")
@TypeConverters(IntSetConverter::class)
data class Plan(@PrimaryKey val servantId: Int,
                var nowStage: Int,
                var planStage: Int,
                var nowSkill1: Int,
                var nowSkill2: Int,
                var nowSkill3: Int,
                var planSkill1: Int,
                var planSkill2: Int,
                var planSkill3: Int,
                var dress: Set<Int>) : Parcelable {
    val servant: Servant?
        get() = ResourcesProvider.instance.servants[servantId]

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.createIntArray().toSet())

    constructor(plan: Plan) : this(plan.servantId,
            plan.nowStage,
            plan.planStage,
            plan.nowSkill1,
            plan.nowSkill2,
            plan.nowSkill3,
            plan.planSkill1,
            plan.planSkill2,
            plan.planSkill3,
            plan.dress)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(servantId)
        parcel.writeInt(nowStage)
        parcel.writeInt(planStage)
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