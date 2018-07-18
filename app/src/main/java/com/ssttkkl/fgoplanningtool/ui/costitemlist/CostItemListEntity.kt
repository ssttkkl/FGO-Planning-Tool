package com.ssttkkl.fgoplanningtool.ui.costitemlist

import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import java.io.File

data class CostItemListEntity(val name: String,
                              val type: ItemType?,
                              val need: Long,
                              val own: Long,
                              val imgFile: File?,
                              val codename: String,
                              val requirements: List<RequirementListEntity>)