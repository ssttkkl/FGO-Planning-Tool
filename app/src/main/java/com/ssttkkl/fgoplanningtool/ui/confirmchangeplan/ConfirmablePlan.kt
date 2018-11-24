package com.ssttkkl.fgoplanningtool.ui.confirmchangeplan

import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfirmablePlan(val old: Plan,
                           val new: Plan?,
                           val mode: Mode) : Parcelable {
    val delta: Plan
        get() {
            return if (new != null)
                Plan(servantId = old.servantId,
                        nowExp = old.nowExp,
                        planExp = new.nowExp,
                        ascendedOnNowStage = old.ascendedOnNowStage,
                        ascendedOnPlanStage = new.ascendedOnNowStage,
                        nowSkill1 = old.nowSkill1,
                        planSkill1 = new.nowSkill1,
                        nowSkill2 = old.nowSkill2,
                        planSkill2 = new.nowSkill2,
                        nowSkill3 = old.nowSkill3,
                        planSkill3 = new.nowSkill3,
                        dress = old.dress - old.dress.intersect(new.dress))
            else
                old
        }
}