package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.arch.lifecycle.Observer
import android.content.Intent
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import kotlinx.android.synthetic.main.activity_databasemanage.*

class DatabaseManageActivityPresenter(val view: DatabaseManageActivity) : DatabaseManageRecViewAdapter.Callback {
    init {
        DatabaseDescriptorManager.liveData.observe(view, Observer { onDataChanged(it ?: listOf()) })
    }

    private fun onDataChanged(newData: List<DatabaseDescriptor>) {
        view.data = newData

        // If there's no such uuid Repo holding, switch Repo's database.
        // If Repo opened a database exists in newData, select it.
        // Else do nothing because the new database will be selected in the next time onDataChanged called.
        if (newData.none { it.uuid == Repo.uuid })
            Repo.switchDatabase(DatabaseDescriptorManager.firstOrCreate.uuid)
        if (newData.any { it.uuid == Repo.uuid })
            (view.recView.adapter as DatabaseManageRecViewAdapter).selectedPosition = newData.indexOfFirst { it.uuid == Repo.uuid }
    }

    override fun onSelectedPositionChanged(newPos: Int, newItem: DatabaseDescriptor) {
        Repo.switchDatabase(newItem.uuid)
    }

    override fun onItemNameChange(pos: Int, item: DatabaseDescriptor, newName: String) {
        DatabaseDescriptorManager.update(item.apply { name = newName }, HowToPerform.Launch)
    }

    override fun onItemRemove(pos: Int, item: DatabaseDescriptor) {
        DatabaseDescriptorManager.remove(item.uuid, HowToPerform.Launch)
    }

    private val helper = DatabaseImportAndExportHelper(view)

    fun onActivityResultOK(requestCode: Int, data: Intent?) {
        helper.onActivityResultOK(requestCode, data)
    }

    override fun onImportPlans(pos: Int, item: DatabaseDescriptor) {
        helper.onImportPlans(item)
    }

    override fun onImportItems(pos: Int, item: DatabaseDescriptor) {
        helper.onImportItems(item)
    }

    override fun onExportPlans(pos: Int, item: DatabaseDescriptor) {
        helper.onExportPlans(item)
    }

    override fun onExportItems(pos: Int, item: DatabaseDescriptor) {
        helper.onExportItems(item)
    }

    fun onAddAction() {
        DatabaseDescriptorManager.generateAndInsert(HowToPerform.Launch)
    }

    fun onDestroy() {
        if (DatabaseDescriptorManager.all.none { it.uuid == Repo.uuid })
            Repo.switchDatabase(DatabaseDescriptorManager.firstOrCreate.uuid)
        DatabaseDescriptorManager.all.forEach {
            if (it.uuid != Repo.uuid)
                RepoDatabase.dispose(it.uuid)
        }
    }
}