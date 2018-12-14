package com.ssttkkl.fgoplanningtool.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object DatabaseImporterAndExporter {
    fun import(uuid: String, dataSet: DataSet) {
        val oldUUID = Repo.uuid.value
        runBlocking(Dispatchers.Main) { Repo.switchDatabase(uuid) }
        if (dataSet.plans != null) {
            Repo.PlanRepo.clear(true)
            Repo.PlanRepo.insert(dataSet.plans, true)
        }
        if (dataSet.items != null) {
            Repo.ItemRepo.clear(true)
            Repo.ItemRepo.update(dataSet.items, true)
        }
        if (oldUUID != null)
            runBlocking(Dispatchers.Main) { Repo.switchDatabase(oldUUID) }
    }

    fun export(uuid: String): DataSet {
        val oldUUID = Repo.uuid.value
        runBlocking(Dispatchers.Main) { Repo.switchDatabase(uuid) }
        val plans = Repo.PlanRepo.all.values
        val items = Repo.ItemRepo.all.values.filter { it.count != 0L }
        if (oldUUID != null)
            runBlocking(Dispatchers.Main) { Repo.switchDatabase(oldUUID) }
        return DataSet(plans, items)
    }
}