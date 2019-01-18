package com.ssttkkl.fgoplanningtool.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import java.io.File
import java.util.*

class SettingsFragmentViewModel : ViewModel() {
    val versionName = MyApp.versionName

    val resPackSummary = object : LiveData<String>() {
        private fun generator(): String {
            return when {
                ResourcesProvider.instance.isAbsent -> MyApp.context.getString(R.string.curResPackVersionAbsent)
                ResourcesProvider.instance.isNotTargeted -> {
                    if (ResourcesProvider.instance.resPackInfo.targetVersion < ResourcesProvider.TARGET_VERSION)
                        MyApp.context.getString(R.string.curResPackVersionLower,
                                ResourcesProvider.instance.resPackInfo.content,
                                ResourcesProvider.instance.resPackInfo.releaseDate)
                    else
                        MyApp.context.getString(R.string.curResPackVersionHigher,
                                ResourcesProvider.instance.resPackInfo.content,
                                ResourcesProvider.instance.resPackInfo.releaseDate)
                }
                ResourcesProvider.instance.isBroken -> MyApp.context.getString(R.string.curResPackVersionBroken,
                        ResourcesProvider.instance.resPackInfo.content,
                        ResourcesProvider.instance.resPackInfo.releaseDate)
                else -> MyApp.context.getString(R.string.curResPackVersionPattern,
                        ResourcesProvider.instance.resPackInfo.content,
                        ResourcesProvider.instance.resPackInfo.releaseDate)
            }
        }

        init {
            value = generator()
            ResourcesProvider.addOnRenewListener(this) {
                value = generator()
            }
        }
    }

    private val resPackClickTimes = MutableLiveData<Int>()

    fun onClickResPack() {
        resPackClickTimes.value = resPackClickTimes.value?.plus(1) ?: 1
    }

    fun onClickRemoveResPack() {
        ResourcesUpdater.remove()
    }

    fun onClickAutoUpdateResPack() {
        showAutoUpdateResPackEvent.call()
    }

    fun onClickManuallyUpdateResPack() {
        showChooseResPackFileEvent.call(REQUEST_CODE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadFileThenGotoUpdate(data?.data!!)
        }
    }

    private fun loadFileThenGotoUpdate(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            MyApp.context.contentResolver.openInputStream(uri).use { inputStream ->
                val tempFile = File(MyApp.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
                tempFile.createNewFile()
                tempFile.outputStream().use { output ->
                    IOUtils.copy(inputStream, output)
                }
                launch(Dispatchers.Main) {
                    showManuallyUpdateResPackEvent.call(tempFile)
                }
            }
        }
    }

    val showAutoUpdateResPackEvent = SingleLiveEvent<Void>()
    val showChooseResPackFileEvent = SingleLiveEvent<Int>()
    val showManuallyUpdateResPackEvent = SingleLiveEvent<File>()
    val showRemoveResPackMessageEvent = SingleLiveEvent<Void>().apply {
        resPackClickTimes.observeForever {
            if (it >= 5)
                call()
        }
    }

    companion object {
        private const val REQUEST_CODE = 1
    }
}