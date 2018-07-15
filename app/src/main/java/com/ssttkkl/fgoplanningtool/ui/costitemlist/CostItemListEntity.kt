package com.ssttkkl.fgoplanningtool.ui.costitemlist

import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.costitemlist.requireservantlist.RequireServantsListEntity
import java.io.File

data class CostItemListEntity(val name: String,
                              val type: ItemType?,
                              val need: Int,
                              val own: Int,
                              val imgFile: File?,
                              val codename: String,
                              val requireServants: List<RequireServantsListEntity>)