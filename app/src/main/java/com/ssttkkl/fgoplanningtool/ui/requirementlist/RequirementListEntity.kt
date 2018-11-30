package com.ssttkkl.fgoplanningtool.ui.requirementlist

import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequirementListEntity(val servantID: Int,
                                 val requirement: Long) : Parcelable {
    val servant
        get() = ResourcesProvider.instance.servants[servantID]
}