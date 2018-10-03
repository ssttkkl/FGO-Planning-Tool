package com.ssttkkl.fgoplanningtool.data

import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.item.Item
import kotlinx.coroutines.experimental.runBlocking

object DatabaseImporterAndExporter {
    fun import(dbDescriptor: DatabaseDescriptor, dataSet: DataSet) {
        val db = RepoDatabase.getInstance(dbDescriptor.uuid)
        if (dataSet.plans != null) {
            runBlocking(Dispatchers.db) {
                db.plansDao.remove(db.plansDao.all)
                db.plansDao.insert(dataSet.plans)
            }
        }
        if (dataSet.items != null) {
            runBlocking(Dispatchers.db) {
                val codenameFromJson = dataSet.items.map { it.codename }.toHashSet()
                val absentFromJson = db.itemsDao.all.filter { !codenameFromJson.contains(it.codename) }.map { Item(it.codename, 0) }
                db.itemsDao.update(absentFromJson + dataSet.items)
            }
        }
    }

    fun export(dbDescriptor: DatabaseDescriptor): DataSet {
        val db = RepoDatabase.getInstance(dbDescriptor.uuid)
        val items = runBlocking(Dispatchers.db) {
            db.itemsDao.all.filter { it.count != 0L }
        }
        val plans = runBlocking(Dispatchers.db) {
            db.plansDao.all
        }
        return DataSet(plans, items)
    }
}