package com.ssttkkl.fgoplanningtool.ui.servantinfo

import com.ssttkkl.fgoplanningtool.data.item.Item

data class ServantInfoLevelListEntity(val start: String,
                                      val to: String,
                                      val items: List<Item>)