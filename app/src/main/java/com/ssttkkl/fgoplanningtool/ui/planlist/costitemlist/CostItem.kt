package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity

data class CostItem(val codename: String,
                    val require: Long,
                    val own: Long,
                    val requirements: Collection<RequirementListEntity>) {
    val descriptor
        get() = ResourcesProvider.instance.itemDescriptors[codename]

    val delta
        get() = own - require
}

fun Collection<CostItem>.sorted(): List<CostItem> {
    return this.groupBy { it.descriptor?.type }
            .map { (type, items) -> Pair(type, items.sortedBy { item -> item.descriptor?.rank }) }
            .sortedBy { (type, _) -> type }
            .flatMap { (_, items) -> items }
}