package com.ssttkkl.fgoplanningtool.data.item

import com.ssttkkl.fgoplanningtool.data.plan.Plan

private const val HOLY_GRAIL = "holy_grail"
private const val QP = "qp"

private fun MutableMap<String, Int>.putItems(items: Collection<Item>) {
    items.forEach { item ->
        val oldValue = get(item.codename) ?: 0
        this[item.codename] = oldValue + item.count
    }
}

val Collection<Plan>.costItems: Collection<Item>
    get() {
        val map = HashMap<String, Int>()
        forEach { plan -> map.putItems(plan.costItems) }
        return map.map { Item(it.key, it.value) }
    }

val Plan.costItems: Collection<Item>
    get() {
        val map = HashMap<String, Int>()
        var qp = 0
        var holyGrail = 0

        for (cur in nowStage until minOf(planStage, 4)) {
            map.putItems(servant.ascensionItems[cur])
            qp += servant.ascensionQP[cur]
        }

        for (cur in maxOf(nowStage, 4) until planStage) {
            holyGrail++
            qp += servant.palingenesisQP[cur - 4]
        }

        for ((first, second) in listOf(Pair(nowSkill1, planSkill1), Pair(nowSkill2, planSkill2), Pair(nowSkill3, planSkill3)))
            for (cur in first - 1 until second - 1) {
                map.putItems(servant.skillItems[cur])
                qp += servant.skillQP[cur]
            }

        if (qp > 0)
            map[QP] = qp
        if (holyGrail > 0)
            map[HOLY_GRAIL] = holyGrail
        return map.map { Item(it.key, it.value) }
    }