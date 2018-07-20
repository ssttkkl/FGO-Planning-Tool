package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.gson.ItemCollectionGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.data.plan.gson.PlanCollectionGsonTypeAdapter
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer

class DatabaseImportAndExportHelper(val view: DatabaseManageActivity) {
    private var databaseToImport: DatabaseDescriptor? = null

    fun onImportPlans(descriptor: DatabaseDescriptor) {
        databaseToImport = descriptor
        gotoOpenJsonUi(FILENAME_PLANS_JSON, REQUEST_CODE_IMPORT_PLANS)
    }

    fun onImportItems(descriptor: DatabaseDescriptor) {
        databaseToImport = descriptor
        gotoOpenJsonUi(FILENAME_ITEMS_JSON, REQUEST_CODE_IMPORT_ITEMS)
    }

    private var databaseToExport: DatabaseDescriptor? = null

    fun onExportPlans(descriptor: DatabaseDescriptor) {
        databaseToExport = descriptor
        gotoCreateJsonUi(FILENAME_PLANS_JSON, REQUEST_CODE_EXPORT_PLANS)
    }

    fun onExportItems(descriptor: DatabaseDescriptor) {
        databaseToExport = descriptor
        gotoCreateJsonUi(FILENAME_ITEMS_JSON, REQUEST_CODE_EXPORT_ITEMS)
    }

    private fun gotoCreateJsonUi(filename: String, requestCode: Int) {
        view.startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = TYPE_JSON
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, filename)
        }, requestCode)
    }

    private fun gotoOpenJsonUi(filename: String, requestCode: Int) {
        view.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = TYPE_JSON
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, filename)
        }, requestCode)
    }

    fun onActivityResultOK(requestCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_EXPORT_ITEMS -> performExportItems(data!!.data)
            REQUEST_CODE_EXPORT_PLANS -> performExportPlans(data!!.data)
            REQUEST_CODE_IMPORT_ITEMS -> performImportItems(data!!.data)
            REQUEST_CODE_IMPORT_PLANS -> performImportPlans(data!!.data)
        }
    }

    private fun performImportAsync(uri: Uri,
                                   successMessage: String,
                                   errorMessage: String,
                                   action: (Reader) -> Unit) = async(Dispatchers.file) {
        try {
            view.contentResolver.openInputStream(uri).use { stream ->
                InputStreamReader(stream).use { streamReader ->
                    action.invoke(streamReader)
                    launch(UI) { view.showMessage(successMessage) }
                }
            }
        } catch (e: Exception) {
            launch(UI) {
                view.showMessage(view.getString(R.string.errorMessageFormat_databasemanage,
                        errorMessage, e.toString()))
            }
        }
    }

    private fun performExportAsync(uri: Uri,
                                   successMessage: String,
                                   errorMessage: String,
                                   action: (Writer) -> Unit) = async(Dispatchers.file) {
        try {
            view.contentResolver.openOutputStream(uri).use { stream ->
                OutputStreamWriter(stream).use { streamWriter ->
                    action.invoke(streamWriter)
                    launch(UI) { view.showMessage(successMessage) }
                }
            }
        } catch (e: Exception) {
            launch(UI) {
                view.showMessage(view.getString(R.string.errorMessageFormat_databasemanage,
                        errorMessage, e.toString()))
            }
        }
    }

    private fun performImportPlans(uri: Uri) {
        performImportAsync(uri, view.getString(R.string.importSuccessful_databasemanage), view.getString(R.string.importError_databasemanage)) {
            val plans = gson.fromJson<List<Plan>>(it, PlanCollectionGsonTypeAdapter.typeToken.type)
            runBlocking(Dispatchers.db) {
                val db = RepoDatabase.getInstance(databaseToImport!!.uuid)
                db.plansDao.remove(db.plansDao.all)
                db.plansDao.insert(plans)
            }
        }
    }

    private fun performImportItems(uri: Uri) {
        performImportAsync(uri, view.getString(R.string.importSuccessful_databasemanage), view.getString(R.string.importError_databasemanage)) {
            val items = gson.fromJson<List<Item>>(it, ItemCollectionGsonTypeAdapter.typeToken.type)
            runBlocking(Dispatchers.db) {
                val db = RepoDatabase.getInstance(databaseToImport!!.uuid)
                val codenameFromJson = items.map { it.codename }.toHashSet()
                val absentFromJson = db.itemsDao.all.filter { !codenameFromJson.contains(it.codename) }.map { Item(it.codename, 0) }
                db.itemsDao.update(absentFromJson + items)
            }
        }
    }

    private fun performExportPlans(uri: Uri) {
        performExportAsync(uri, view.getString(R.string.exportSuccessful_databasemanage), view.getString(R.string.exportError_databasemanage)) {
            val plans = runBlocking(Dispatchers.db) {
                val db = RepoDatabase.getInstance(databaseToExport!!.uuid)
                db.plansDao.all
            }
            gson.toJson(plans, PlanCollectionGsonTypeAdapter.typeToken.type, it)
        }
    }

    private fun performExportItems(uri: Uri) {
        performExportAsync(uri, view.getString(R.string.exportSuccessful_databasemanage), view.getString(R.string.exportError_databasemanage)) {
            val items = runBlocking(Dispatchers.db) {
                val db = RepoDatabase.getInstance(databaseToExport!!.uuid)
                db.itemsDao.all.filter { it.count != 0L }
            }
            gson.toJson(items, ItemCollectionGsonTypeAdapter.typeToken.type, it)
        }
    }

    companion object {
        private val gson = GsonBuilder().registerTypeAdapter(ItemCollectionGsonTypeAdapter.typeToken.type, ItemCollectionGsonTypeAdapter())
                .registerTypeAdapter(PlanCollectionGsonTypeAdapter.typeToken.type, PlanCollectionGsonTypeAdapter())
                .create()

        private const val REQUEST_CODE_EXPORT_ITEMS = 1
        private const val REQUEST_CODE_EXPORT_PLANS = 2
        private const val REQUEST_CODE_IMPORT_ITEMS = 3
        private const val REQUEST_CODE_IMPORT_PLANS = 4

        private const val FILENAME_ITEMS_JSON = "items.json"
        private const val FILENAME_PLANS_JSON = "plans.json"

        private val TYPE_JSON: String

        init {
            val mimeTypeMap = MimeTypeMap.getSingleton()
            TYPE_JSON = if (mimeTypeMap.hasExtension("json"))
                mimeTypeMap.getMimeTypeFromExtension("json")
            else
                "*/*"
        }
    }
}