package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import androidx.core.content.edit
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.DataSet
import com.ssttkkl.fgoplanningtool.data.DatabaseImporterAndExporter
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import com.ssttkkl.fgoplanningtool.data.gson.DataSetGsonTypeAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.set

class DatabaseManageFragmentViewModel : ViewModel() {
    val gotoCreateJsonUIEvent = SingleLiveEvent<Pair<String, Int>>()
    val gotoOpenJsonUIEvent = SingleLiveEvent<Pair<String, Int>>()
    val showMessageEvent = SingleLiveEvent<String>()

    private val indexedData = MutableLiveData<Map<String, EditableDatabaseDescriptor>>()

    val observer = Observer<Map<String, DatabaseDescriptor>> { newData ->
        indexedData.value = newData?.mapValues { (_, it) ->
            EditableDatabaseDescriptor(it, indexedData.value?.get(it.uuid)?.editing == true)
        }
    }

    init {
        DatabaseDescriptorManager.all.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        DatabaseDescriptorManager.all.removeObserver(observer)
    }

    val data: LiveData<List<EditableDatabaseDescriptor>?> = Transformations.map(indexedData) { indexedData ->
        indexedData?.values?.sortedBy { it.databaseDescriptor.createTime }
    }

    val currentDescriptor
        get() = Repo.databaseDescriptor

    val editedName = ObservableArrayMap<String, String>()

    private fun setItemEditing(uuid: String, editing: Boolean) {
        if (editing)
            editedName[uuid] = indexedData.value?.get(uuid)?.databaseDescriptor?.name
        else
            editedName.remove(uuid)

        indexedData.value = indexedData.value?.toMutableMap()?.apply {
            val oldItem = this[uuid] ?: return
            this[uuid] = EditableDatabaseDescriptor(oldItem.databaseDescriptor, editing)
        }
    }

    fun onClickAdd() {
        DatabaseDescriptorManager.generateAndInsert()
    }

    fun onClickItem(uuid: String) {
        Repo.switchDatabase(uuid)
    }

    fun onClickItemMore(uuid: String, view: View) {
        PopupMenu(view.context, view).apply {
            inflate(R.menu.item_databasemanage)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.rename -> onClickItemRename(uuid)
                    R.id.remove -> onClickItemRemove(uuid)
                    R.id.importData -> onClickItemImport(uuid)
                    R.id.exportData -> onClickItemExport(uuid)
                }
                true
            }
        }.show()
    }

    fun onClickItemSave(uuid: String) {
        DatabaseDescriptorManager.update(DatabaseDescriptor(uuid,
                editedName[uuid].toString(),
                indexedData.value?.get(uuid)?.databaseDescriptor?.createTime
                        ?: Date().time))
        setItemEditing(uuid, false)
    }

    val onEditorAction = this::onClickItemSave

    fun onClickItemCancel(uuid: String) {
        setItemEditing(uuid, false)
    }

    fun onClickItemRename(uuid: String) {
        setItemEditing(uuid, true)
    }

    fun onClickItemRemove(uuid: String) {
        DatabaseDescriptorManager.remove(uuid)
    }

    private var databaseToImport: String? = null
    private var databaseToExport: String? = null

    fun onClickItemImport(uuid: String) {
        databaseToImport = uuid
        gotoOpenJsonUIEvent.call(Pair(FILENAME_JSON.format(indexedData.value?.get(uuid)?.databaseDescriptor?.name
                ?: "database"),
                REQUEST_CODE_IMPORT))
    }

    fun onClickItemExport(uuid: String) {
        databaseToExport = uuid
        gotoCreateJsonUIEvent.call(Pair(FILENAME_JSON.format(indexedData.value?.get(uuid)?.databaseDescriptor?.name
                ?: "database"),
                REQUEST_CODE_EXPORT))
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_EXPORT -> performExport(data?.data)
                REQUEST_CODE_IMPORT -> performImport(data?.data)
                else -> return false
            }
            return true
        } else
            return false
    }

    private fun performImport(uri: Uri?) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                MyApp.context.contentResolver.openInputStream(uri!!).use { stream ->
                    InputStreamReader(stream).use { reader ->
                        val dataSet = gson.fromJson<DataSet>(reader, DataSet::class.java)
                        DatabaseImporterAndExporter.import(databaseToImport!!, dataSet)
                        launch(Dispatchers.Main) {
                            showMessageEvent.call(MyApp.context.getString(R.string.importSuccessfully))
                        }
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    showMessageEvent.call(MyApp.context.getString(R.string.importFailed, e.message))
                }
            }
        }
    }

    private fun performExport(uri: Uri?) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                MyApp.context.contentResolver.openOutputStream(uri!!).use { stream ->
                    OutputStreamWriter(stream).use { writer ->
                        val dataSet = DatabaseImporterAndExporter.export(databaseToExport!!)
                        gson.toJson(dataSet, DataSet::class.java, writer)
                        launch(Dispatchers.Main) {
                            showMessageEvent.call(MyApp.context.getString(R.string.exportSuccessfully))
                        }
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    showMessageEvent.call(MyApp.context.getString(R.string.exportFailed, e.message))
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

        val TYPE_JSON: String

        init {
            val mimeTypeMap = MimeTypeMap.getSingleton()
            TYPE_JSON = if (mimeTypeMap.hasExtension("json"))
                mimeTypeMap.getMimeTypeFromExtension("json") ?: "*/*"
            else
                "*/*"
        }
    }
}