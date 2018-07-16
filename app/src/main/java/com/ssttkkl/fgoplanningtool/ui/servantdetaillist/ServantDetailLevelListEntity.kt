package com.ssttkkl.fgoplanningtool.ui.servantdetaillist

import com.ssttkkl.fgoplanningtool.data.item.Item

data class ServantDetailLevelListEntity(val start: String,
                                        val to: String,
                                        val items: List<Item>)