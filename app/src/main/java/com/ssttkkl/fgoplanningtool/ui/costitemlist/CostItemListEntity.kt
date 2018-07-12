package com.ssttkkl.fgoplanningtool.ui.costitemlist

import java.io.File

data class CostItemListEntity(val name: String,
                              val type: String,
                              val need: Int,
                              val own: Int,
                              val imgFile: File?)