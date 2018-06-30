package com.ssttkkl.fgoplanningtool.data.item

import com.ssttkkl.fgoplanningtool.data.plan.Plan

val Collection<Plan>.costItems: Collection<Item>
    get() {
        val map = HashMap<String, Int>()
        this.forEach {
            for ((codename, count) in it.costItems)
                map[codename] = map[codename]?.plus(count) ?: count
        }
        return map.map { Item(it.key, it.value) }
    }

val Plan.costItems: Collection<Item>
    get() {
        val map = HashMap<String, Int>()
        for (cur in nowStage until planStage) {
            for ((codename, count) in servant.ascensionItems[cur])
                map[codename] = map[codename]?.plus(count) ?: count

            val curQp = servant.ascensionQP[cur]
            map["qp"] = map["qp"]?.plus(curQp) ?: curQp
        }
        for ((first, second) in listOf(Pair(nowSkill1, planSkill1), Pair(nowSkill2, planSkill2), Pair(nowSkill3, planSkill3)))
            for (cur in first - 1 until second - 1) {
                for ((codename, count) in servant.skillItems[cur])
                    map[codename] = map[codename]?.plus(count) ?: count

                val curQp = servant.skillQP[cur]
                map["qp"] = map["qp"]?.plus(curQp) ?: curQp
            }
        return map.map { Item(it.key, it.value) }
    }