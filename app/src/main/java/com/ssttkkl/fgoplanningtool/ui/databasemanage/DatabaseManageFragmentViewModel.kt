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
import com.ssttkkl.fgoplanningtool.data.HowToPerform
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

class DatabaseManageFragmentViewModel : ViewModel() {
    val gotoCreateJsonUIEvent = SingleLiveEvent<Pair<String,Int>>()
    val gotoOpenJsonUIEvent = SingleLiveEvent<Pair<String,Int>>()
    val showMessageEvent = SingleLiveEvent<String>()

    private val indexedData = MutableLiveData<Map<String, EditableDatabaseDescriptor>>()

    val data: LiveData<List<EditableDatabaseDescriptor>?> = Transformations.map(indexedData) { indexedData ->
        indexedData?.values?.sortedBy { it.databaseDescriptor.createTime }
    }

    private fun setItemEditing(uuid: String, editing: Boolean) {
        synchronized(indexedData) {
            val oldData = indexedData.value ?: return
            val oldItem = oldData[uuid] ?: return
            indexedData.value = oldData - uuid + Pair(uuid, EditableDatabaseDescriptor(oldItem.databaseDescriptor, editing))
        }
    }

    val currentDescriptor
        get() = Repo.databaseDescriptorLiveData

    val observer = Observer<List<DatabaseDescriptor>> { newData ->
        synchronized(indexedData) {
            indexedData.value = newData?.associate {
                Pair(it.uuid, EditableDatabaseDescriptor(it, indexedData.value?.get(it.uuid)?.editing == true))
            }
        }
    }

    init {
        DatabaseDescriptorManager.liveData.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        DatabaseDescriptorManager.liveData.removeObserver(observer)
    }

    val editedName = ObservableArrayMap<String, String>()

    fun onClickAdd() {
        DatabaseDescriptorManager.generateAndInsert(HowToPerform.Launch)
    }

    fun onClickItem(uuid: String) {
        Repo.switchDatabase(uuid)
        PreferenceManager.getDefaultSharedPreferences(MyApp.context).edit {
            putString(PreferenceKeys.KEY_DEFAULT_DB_UUID, uuid)
            apply()
        }
    }

    fun onClickItemMore(uuid: String, view: View) {
        PopupMenu(view.context, view).apply {
            inflate(R.menu.item_databasemanage)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.rename_action -> onClickItemRename(uuid)
                    R.id.remove_action -> onClickItemRemove(uuid)
                    R.id.import_action -> onClickItemImport(uuid)
                    R.id.export_action -> onClickItemExport(uuid)
                }
                true
            }
        }.show()
    }

    fun onClickItemSave(uuid: String) {
        DatabaseDescriptorManager.update(DatabaseDescriptor(uuid,
                editedName[uuid].toString(),
                indexedData.value?.get(uuid)?.databaseDescriptor?.createTime
                        ?: Date().time), HowToPerform.Launch)
        setItemEditing(uuid, false)
    }

    fun onClickItemCancel(uuid: String) {
        setItemEditing(uuid, false)
    }

    fun onClickItemRename(uuid: String) {
        setItemEditing(uuid, true)
    }

    fun onClickItemRemove(uuid: String) {
        DatabaseDescriptorManager.remove(uuid, HowToPerform.Launch)
    }

    private var databaseToImport: DatabaseDescriptor? = null
    private var databaseToExport: DatabaseDescriptor? = null

    fun onClickItemImport(uuid: String) {
        val descriptor = DatabaseDescriptorManager[uuid] ?: return
        databaseToImport = descriptor
        gotoOpenJsonUIEvent.call(Pair(FILENAME_JSON.format(descriptor.name), REQUEST_CODE_IMPORT))
    }

    fun onClickItemExport(uuid: String) {
        val descriptor = DatabaseDescriptorManager[uuid] ?: return
        databaseToExport = descriptor
        gotoCreateJsonUIEvent.call(Pair(FILENAME_JSON.format(descriptor.name), REQUEST_CODE_EXPORT))
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?):Boolean {
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
                            showMessageEvent.call(MyApp.context.getString(R.string.importSuccessful_databasemanage))
                        }
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    showMessageEvent.call(MyApp.context.getString(R.string.importError_databasemanage, e.message))
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
                            showMessageEvent.call(MyApp.context.getString(R.string.exportSuccessful_databasemanage))
                        }
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    showMessageEvent.call(MyApp.context.getString(R.string.exportError_databasemanage, e.message))
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