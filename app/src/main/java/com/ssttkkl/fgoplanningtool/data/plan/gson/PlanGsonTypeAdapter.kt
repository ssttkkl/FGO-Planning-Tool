package com.ssttkkl.fgoplanningtool.data.plan.gson

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ssttkkl.fgoplanningtool.data.plan.Plan

class PlanGsonTypeAdapter : TypeAdapter<Plan>() {
    override fun write(writer: JsonWriter, it: Plan) {
        writer.beginObject()

        writer.name(NAME_SERVANT_ID)
        writer.value(it.servantId)

        writer.name(NAME_STAGE)
        writer.beginArray()
        writer.value(it.nowStage)
        writer.value(it.planStage)
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
        it.dress.forEach {
            writer.value(it)
        }
        writer.endArray()

        writer.endObject()
    }

    override fun read(reader: JsonReader): Plan {
        var servantId = -1
        var nowStage = -1
        var planStage = -1
        var nowSkill1 = -1
        var planSkill1 = -1
        var nowSkill2 = -1
        var planSkill2 = -1
        var nowSkill3 = -1
        var planSkill3 = -1
        val dress = HashSet<Int>()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                NAME_SERVANT_ID -> servantId = reader.nextInt()
                NAME_STAGE -> {
                    reader.beginArray()
                    nowStage = reader.nextInt()
                    planStage = reader.nextInt()
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
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (listOf(servantId, nowStage, planStage, nowSkill1, nowSkill2, nowSkill3, planSkill1, planSkill2, planSkill3).any { it == -1 })
            throw JsonSyntaxException("Some values are not found when serializer a plan. ")

        return Plan(servantId, nowStage, planStage, nowSkill1, nowSkill2, nowSkill3, planSkill1, planSkill2, planSkill3, dress)
    }

    companion object {
        private const val NAME_SERVANT_ID = "servant_id"
        private const val NAME_STAGE = "stage"
        private const val NAME_SKILL_1 = "skill_1"
        private const val NAME_SKILL_2 = "skill_2"
        private const val NAME_SKILL_3 = "skill_3"
        private const val NAME_DRESS = "dress"
    }
}