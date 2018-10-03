package com.ssttkkl.fgoplanningtool.data

import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan

data class DataSet(val plans: Collection<Plan>?,
                   val items: Collection<Item>?)