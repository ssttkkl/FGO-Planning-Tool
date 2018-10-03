package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.DataSet
import com.ssttkkl.fgoplanningtool.data.DatabaseImporterAndExporter
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.gson.DataSetGsonTypeAdapter
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class DatabaseImportAndExportHelper(val view: DatabaseManageActivity) {
    private var databaseToImport: DatabaseDescriptor? = null

    fun onImport(descriptor: DatabaseDescriptor) {
        databaseToImport = descriptor
        gotoOpenJsonUi(FILENAME_JSON.format(descriptor.name), REQUEST_CODE_IMPORT)
    }

    private var databaseToExport: DatabaseDescriptor? = null

    fun onExport(descriptor: DatabaseDescriptor) {
        databaseToExport = descriptor
        gotoCreateJsonUi(FILENAME_JSON.format(descriptor.name), REQUEST_CODE_EXPORT)
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
            REQUEST_CODE_EXPORT -> performExport(data!!.data)
            REQUEST_CODE_IMPORT -> performImport(data!!.data)
        }
    }

    private fun performImport(uri: Uri) {
        launch(Dispatchers.file) {
            try {
                view.contentResolver.openInputStream(uri).use { stream ->
                    InputStreamReader(stream).use { reader ->
                        val dataSet = gson.fromJson<DataSet>(reader, DataSet::class.java)
                        DatabaseImporterAndExporter.import(databaseToImport!!, dataSet)
                        launch(UI) { view.showMessage(view.getString(R.string.importSuccessful_databasemanage)) }
                    }
                }
            } catch (e: Exception) {
                launch(UI) {
                    view.showMessage(view.getString(R.string.errorMessageFormat_databasemanage,
                            view.getString(R.string.importError_databasemanage), e.toString()))
                }
            }
        }
    }

    private fun performExport(uri: Uri) {
        async(Dispatchers.file) {
            try {
                view.contentResolver.openOutputStream(uri).use { stream ->
                    OutputStreamWriter(stream).use { writer ->
                        val dataSet = DatabaseImporterAndExporter.export(databaseToExport!!)
                        gson.toJson(dataSet, DataSet::class.java, writer)
                        launch(UI) { view.showMessage(view.getString(R.string.exportSuccessful_databasemanage)) }
                    }
                }
            } catch (e: Exception) {
                launch(UI) {
                    view.showMessage(view.getString(R.string.errorMessageFormat_databasemanage,
                            view.getString(R.string.exportError_databasemanage), e.toString()))
                }
            }
        }
    }

    companion object {
        private val gson = GsonBuilder().registerTypeAdapter(DataSet::class.java, DataSetGsonTypeAdapter())
                .create()

        private const val REQUEST_CODE_EXPORT = 1
        private const val REQUEST_CODE_IMPORT = 2

        private const val FILENAME_JSON = "%s.json"

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