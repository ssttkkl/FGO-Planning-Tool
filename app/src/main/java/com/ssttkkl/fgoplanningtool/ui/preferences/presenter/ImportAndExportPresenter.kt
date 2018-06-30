package com.ssttkkl.fgoplanningtool.ui.preferences

import android.content.Intent
import android.net.Uri
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.DatabaseExporter
import com.ssttkkl.fgoplanningtool.data.DatabaseImporter
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ImportAndExportPresenter(val view: PreferencesFragment) {
    init {
        listOf(KEY_IMPORT_ITEMS, KEY_EXPORT_ITEMS, KEY_IMPORT_PLANS, KEY_EXPORT_PLANS).forEach { key ->
            view.findPreference(key).setOnPreferenceClickListener {
                onPreferenceClick(key)
                true
            }
        }
    }

    private fun gotoCreateJsonUi(filename: String, requestCode: Int) {
        view.startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, filename)
        }, requestCode)
    }

    private fun gotoOpenJsonUi(filename: String, requestCode: Int) {
        view.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, filename)
        }, requestCode)
    }

    private fun onPreferenceClick(key: String) {
        when (key) {
            KEY_EXPORT_ITEMS -> gotoCreateJsonUi(ITEMS_JSON_FILENAME, REQUEST_CODE_EXPORT_ITEMS)
            KEY_EXPORT_PLANS -> gotoCreateJsonUi(PLANS_JSON_FILENAME, REQUEST_CODE_EXPORT_PLANS)
            KEY_IMPORT_ITEMS -> gotoOpenJsonUi(ITEMS_JSON_FILENAME, REQUEST_CODE_IMPORT_ITEMS)
            KEY_IMPORT_PLANS -> gotoOpenJsonUi(PLANS_JSON_FILENAME, REQUEST_CODE_IMPORT_PLANS)
        }
    }

    fun onActivityResultOK(requestCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_EXPORT_ITEMS -> exportItems(data!!.data)
            REQUEST_CODE_EXPORT_PLANS -> exportPlans(data!!.data)
            REQUEST_CODE_IMPORT_ITEMS -> importItems(data!!.data)
            REQUEST_CODE_IMPORT_PLANS -> importPlans(data!!.data)
        }
    }

    private fun performImportAsync(uri: Uri,
                                   successMessage: String,
                                   errorMessage: String,
                                   action: (BufferedReader) -> Unit) =
            launch(CommonPool) {
                try {
                    view.activity.contentResolver.openInputStream(uri).use { stream ->
                        InputStreamReader(stream).use { streamReader ->
                            BufferedReader(streamReader).use { bufferedReader ->
                                action.invoke(bufferedReader)
                                launch(UI) {
                                    view.showMessage(successMessage)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    launch(UI) {
                        view.showMessage(view.getString(R.string.errorMessageFormat_pref,
                                errorMessage, e.localizedMessage))
                    }
                }
            }

    private fun performExportAsync(uri: Uri,
                                   successMessage: String,
                                   errorMessage: String,
                                   action: (BufferedWriter) -> Unit) =
            launch(CommonPool) {
                try {
                    view.activity.contentResolver.openOutputStream(uri).use { stream ->
                        OutputStreamWriter(stream).use { streamWriter ->
                            BufferedWriter(streamWriter).use { bufferedWriter ->
                                action.invoke(bufferedWriter)
                                launch(UI) {
                                    view.showMessage(successMessage)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    launch(UI) {
                        view.showMessage(view.getString(R.string.errorMessageFormat_pref,
                                errorMessage, e.localizedMessage))
                    }
                }
            }

    private fun exportItems(uri: Uri) {
        performExportAsync(uri, view.getString(R.string.exportSuccessful_pref), view.getString(R.string.exportError_pref)) {
            DatabaseExporter.exportItems(it)
        }
    }

    private fun exportPlans(uri: Uri) {
        performExportAsync(uri, view.getString(R.string.exportSuccessful_pref), view.getString(R.string.exportError_pref)) {
            DatabaseExporter.exportPlans(it)
        }
    }

    private fun importPlans(uri: Uri) {
        performImportAsync(uri, view.getString(R.string.importSuccessful_pref), view.getString(R.string.importError_pref)) {
            DatabaseImporter.importPlans(it)
        }
    }

    private fun importItems(uri: Uri) {
        performImportAsync(uri, view.getString(R.string.importSuccessful_pref), view.getString(R.string.importError_pref)) {
            DatabaseImporter.importItems(it)
        }
    }

    companion object {
        const val KEY_VERSION = "version"

        private const val KEY_IMPORT_ITEMS = "import_items"
        private const val KEY_EXPORT_ITEMS = "export_items"
        private const val KEY_IMPORT_PLANS = "import_plans"
        private const val KEY_EXPORT_PLANS = "export_plans"

        private const val REQUEST_CODE_EXPORT_ITEMS = 1
        private const val REQUEST_CODE_EXPORT_PLANS = 2
        private const val REQUEST_CODE_IMPORT_ITEMS = 3
        private const val REQUEST_CODE_IMPORT_PLANS = 4

        private const val ITEMS_JSON_FILENAME = "items.json"
        private const val PLANS_JSON_FILENAME = "plans.json"
    }
}