package com.ssttkkl.fgoplanningtool.data.item

import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.LevelValues
import com.ssttkkl.fgoplanningtool.resources.QPValues
import kotlin.math.ceil
import kotlin.math.min

private const val HOLY_GRAIL = "holy_grail"
private const val QP = "qp"
private const val EXP_CARD = "exp_card"

private fun MutableMap<String, Long>.putItems(items: Collection<Item>) {
    items.forEach { item ->
        val oldValue = get(item.codename) ?: 0
        this[item.codename] = oldValue + item.count
    }
}

private fun Plan.calcForExpCards(): Pair<Long, Long> {
    var totExpCards = 0L
    var totQP = 0L

    var curExp = nowExp
    var curLevel = LevelValues.expToLevel(curExp)
    var curStage = LevelValues.levelToStage(servant!!.star, curLevel)
    while (curExp < planExp) {
        val nextExp = LevelValues.levelToExp(servant!!.stageMapToMaxLevel[curStage])
        // if after using 20 exp cards the level wouldn't reach the max, use 20 exp cards at once.
        val costExpCards = if (nextExp >= curExp + 20 * LevelValues.perCardExp)
            20
        else
            ceil(1.0 * (nextExp - curExp) / LevelValues.perCardExp).toInt()
        totExpCards += costExpCards
        totQP += costExpCards * servant!!.calcExpCardQP(curLevel)
        curExp = min(curExp + costExpCards * LevelValues.perCardExp, nextExp)
        curLevel = LevelValues.expToLevel(curExp)
        curStage = LevelValues.levelToStage(servant!!.star, curLevel) +
                if (servant!!.stageMapToMaxLevel.contains(curLevel)) 1 else 0
    }

    return Pair(totExpCards, totQP)
}

val Collection<Plan>.costItems: Collection<Item>
    get() {
        val map = HashMap<String, Long>()
        forEach { plan -> map.putItems(plan.costItems) }
        return map.map { Item(it.key, it.value) }
    }

val Plan.costItems: Collection<Item>
    get() {
        if (servant == null)
            return listOf()
        val map = HashMap<String, Long>()

        var qp = 0L
        var holyGrail = 0L

        for (cur in nowStage until minOf(planStage, 4)) {
            map.putItems(servant!!.ascensionItems[cur])
            qp += servant!!.ascensionQP[cur]
        }

        for (cur in maxOf(nowStage, 4) until planStage) {
            holyGrail++
            qp += servant!!.ascensionQP[cur]
        }

        for ((first, second) in listOf(Pair(nowSkill1, planSkill1), Pair(nowSkill2, planSkill2), Pair(nowSkill3, planSkill3)))
            for (cur in first - 1 until second - 1) {
                map.putItems(servant!!.skillItems[cur])
                qp += servant!!.skillQP[cur]
            }

        for (dress in dressID.map { servant!!.dress[it] }) {
            map.putItems(dress.items)
            qp += QPValues.dressQP
        }

        val (expCards, qpForExpCards) = calcForExpCards()
        qp += qpForExpCards

        if (qp > 0)
            map[QP] = qp
        if (holyGrail > 0)
            map[HOLY_GRAIL] = holyGrail
        if (expCards > 0)
            map[EXP_CARD] = expCards
        return map.map { Item(it.key, it.value) }
    }

// key: item's codename
// value: A map, each of whose entities represents a servant requiring this item.
//        The first is servantID, and the second is requirement.
val Collection<Plan>.groupedCostItems: Map<String, HashMap<Int, Long>>
    get() {
        val map = HashMap<String, HashMap<Int, Long>>()
        this.forEach { plan ->
            plan.costItems.forEach { item ->
                if (!map.containsKey(item.codename))
                    map[item.codename] = HashMap()
                map[item.codename]!![plan.servantId] = item.count
            }
        }
        return map
    }