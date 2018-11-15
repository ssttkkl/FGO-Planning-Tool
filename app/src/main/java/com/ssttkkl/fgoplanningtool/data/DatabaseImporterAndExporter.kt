package com.ssttkkl.fgoplanningtool.data

import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.item.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object DatabaseImporterAndExporter {
    fun import(dbDescriptor: DatabaseDescriptor, dataSet: DataSet) {
        val db = RepoDatabase.getInstance(dbDescriptor.uuid)
        if (dataSet.plans != null) {
            runBlocking(Dispatchers.IO) {
                db.plansDao.remove(db.plansDao.all)
                db.plansDao.insert(dataSet.plans)
            }
        }
        if (dataSet.items != null) {
            runBlocking(Dispatchers.IO) {
                val codenameFromJson = dataSet.items.map { it.codename }.toHashSet()
                val absentFromJson = db.itemsDao.all.filter { !codenameFromJson.contains(it.codename) }.map { Item(it.codename, 0) }
                db.itemsDao.update(absentFromJson + dataSet.items)
            }
        }
    }

    fun export(dbDescriptor: DatabaseDescriptor): DataSet {
        val db = RepoDatabase.getInstance(dbDescriptor.uuid)
        val items = runBlocking(Dispatchers.IO) {
            db.itemsDao.all.filter { it.count != 0L }
        }
        val plans = runBlocking(Dispatchers.IO) {
            db.plansDao.all
        }
        return DataSet(plans, items)
    }
}