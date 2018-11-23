package com.ssttkkl.fgoplanningtool.ui.costitemlist

import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity

data class CostItem(val codename: String,
                    val require: Long,
                    val own: Long,
                    val requirements: List<RequirementListEntity>) {
    val descriptor
        get() = ResourcesProvider.instance.itemDescriptors[codename]
}