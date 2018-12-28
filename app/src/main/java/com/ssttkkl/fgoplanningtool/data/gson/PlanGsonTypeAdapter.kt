package com.ssttkkl.fgoplanningtool.data.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.LevelValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

class PlanGsonTypeAdapter : TypeAdapter<Plan>() {
    override fun write(writer: JsonWriter, it: Plan) {
        writer.beginObject()

        writer.name(NAME_SERVANT_ID)
        writer.value(it.servantId)

        writer.name(NAME_EXP)
        writer.beginArray()
        writer.value(it.nowExp)
        writer.value(it.planExp)
        writer.endArray()

        writer.name(NAME_ASCENDED_ON_STAGE)
        writer.beginArray()
        writer.value(it.ascendedOnNowStage)
        writer.value(it.ascendedOnPlanStage)
        writer.endArray()

        writer.name(NAME_SKILL_1)
        writer.beginArray()
        writer.value(it.nowSkill1)
        writer.value(it.planSkill1)
        writer.endArray()

        writer.name(NAME_SKILL_2)
        writer.beginArray()
        writer.value(it.nowSkill2)
        writer.value(it.planSkill2)
        writer.endArray()

        writer.name(NAME_SKILL_3)
        writer.beginArray()
        writer.value(it.nowSkill3)
        writer.value(it.planSkill3)
        writer.endArray()

        writer.name(NAME_DRESS)
        writer.beginArray()
        it.dressID.forEach {
            writer.value(it)
        }
        writer.endArray()

        writer.endObject()
    }

    override fun read(reader: JsonReader): Plan {
        var servantId: Int? = null
        var nowExp: Int? = null
        var planExp: Int? = null
        var ascendedOnNowStage: Boolean? = false
        var ascendedOnPlanStage: Boolean? = false
        var nowStage: Int? = -1
        var planStage: Int? = -1
        var nowSkill1: Int? = -1
        var planSkill1: Int? = -1
        var nowSkill2: Int? = -1
        var planSkill2: Int? = -1
        var nowSkill3: Int? = -1
        var planSkill3: Int? = -1
        val dress = HashSet<Int>()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                NAME_SERVANT_ID -> servantId = reader.nextInt()
                NAME_EXP -> {
                    reader.beginArray()
                    nowExp = reader.nextInt()
                    planExp = reader.nextInt()
                    reader.endArray()
                }
                NAME_ASCENDED_ON_STAGE -> {
                    reader.beginArray()
                    ascendedOnNowStage = reader.nextBoolean()
                    ascendedOnPlanStage = reader.nextBoolean()
                    reader.endArray()
                }
                NAME_SKILL_1 -> {
                    reader.beginArray()
                    nowSkill1 = reader.nextInt()
                    planSkill1 = reader.nextInt()
                    reader.endArray()
                }
                NAME_SKILL_2 -> {
                    reader.beginArray()
                    nowSkill2 = reader.nextInt()
                    planSkill2 = reader.nextInt()
                    reader.endArray()
                }
                NAME_SKILL_3 -> {
                    reader.beginArray()
                    nowSkill3 = reader.nextInt()
                    planSkill3 = reader.nextInt()
                    reader.endArray()
                }
                NAME_DRESS -> {
                    reader.beginArray()
                    while (reader.hasNext())
                        dress.add(reader.nextInt())
                    reader.endArray()
                }
                // desperate
                NAME_STAGE -> {
                    reader.beginArray()
                    nowStage = reader.nextInt()
                    planStage = reader.nextInt()
                    reader.endArray()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (nowExp == null && planExp == null && nowStage != null && planStage != null) {
            val servant = ResourcesProvider.instance.servants[servantId]!!
            val nowLevel = LevelValues.stageMapToMaxLevel[servant.star][nowStage]
            val planLevel = LevelValues.stageMapToMaxLevel[servant.star][planStage]
            nowExp = LevelValues.levelToExp(nowLevel)
            planExp = LevelValues.levelToExp(planLevel)
        }

        if (listOf(servantId, nowExp, planExp, ascendedOnNowStage, ascendedOnPlanStage, nowSkill1, nowSkill2, nowSkill3, planSkill1, planSkill2, planSkill3).any { it == null })
            throw Exception(MyApp.context.getString(R.string.exc_fileFormatError_planGsonTypeAdapter))

        return Plan(servantId = servantId!!,
                nowExp = nowExp!!,
                planExp = planExp!!,
                ascendedOnNowStage = ascendedOnNowStage!!,
                ascendedOnPlanStage = ascendedOnPlanStage!!,
                nowSkill1 = nowSkill1!!,
                nowSkill2 = nowSkill2!!,
                nowSkill3 = nowSkill3!!,
                planSkill1 = planSkill1!!,
                planSkill2 = planSkill2!!,
                planSkill3 = planSkill3!!,
                dressID = dress)
    }

    companion object {
        private const val NAME_SERVANT_ID = "servant_id"
        private const val NAME_EXP = "exp"
        private const val NAME_ASCENDED_ON_STAGE = "ascendedOnStage"
        private const val NAME_SKILL_1 = "skill_1"
        private const val NAME_SKILL_2 = "skill_2"
        private const val NAME_SKILL_3 = "skill_3"
        private const val NAME_DRESS = "dress"

        // desperate
        private const val NAME_STAGE = "stage"
    }
}