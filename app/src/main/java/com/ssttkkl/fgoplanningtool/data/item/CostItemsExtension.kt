package com.ssttkkl.fgoplanningtool.data.item

import com.ssttkkl.fgoplanningtool.data.plan.Plan

private const val POLY_GRAIL = "holy_grail"
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
        for (cur in nowStage until minOf(planStage, 4)) {
            map.putItems(servant.ascensionItems[cur])
            qp += servant.ascensionQP[cur]
        }
        if (planStage > 4)
            map[POLY_GRAIL] = planStage - maxOf(nowStage, 4)
        for ((first, second) in listOf(Pair(nowSkill1, planSkill1), Pair(nowSkill2, planSkill2), Pair(nowSkill3, planSkill3)))
            for (cur in first - 1 until second - 1) {
                map.putItems(servant.skillItems[cur])
                qp += servant.skillQP[cur]
            }
        return map.map { Item(it.key, it.value) } + Item(QP, qp)
    }