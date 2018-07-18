package com.ssttkkl.fgoplanningtool.ui.costitemlist

import java.io.File

data class CostItemListEntity(val name: String,
                              val type: String,
                              val need: Long,
                              val own: Long,
                              val imgFile: File?)