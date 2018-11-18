package com.ssttkkl.fgoplanningtool.ui.settings

import androidx.lifecycle.*
import android.content.Intent
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import java.io.File
import java.util.*

class SettingsFragmentViewModel : ViewModel() {
    val showRemoveResPackMessageEvent = SingleLiveEvent<Void>()
    val showAutoUpdateResPackEvent = SingleLiveEvent<Void>()
    val showChooseResPackFileEvent = SingleLiveEvent<Void>()
    val showManuallyUpdateResPackEvent = SingleLiveEvent<File>()

    val versionName = MyApp.versionName

    val resPackSummary = object : LiveData<String>() {
        private fun generator(): String {
            return when {
                ResourcesProvider.instance.isAbsent -> MyApp.context.getString(R.string.summary_curResPackVersion_absent_pref)
                ResourcesProvider.instance.isNotTargeted -> {
                    if (ResourcesProvider.instance.resPackInfo.targetVersion < ResourcesProvider.TARGET_VERSION)
                        MyApp.context.getString(R.string.summary_curResPackVersion_lowTargetVersion_pref,
                                ResourcesProvider.instance.resPackInfo.content,
                                ResourcesProvider.instance.resPackInfo.releaseDate)
                    else
                        MyApp.context.getString(R.string.summary_curResPackVersion_highTargetVersion_pref,
                                ResourcesProvider.instance.resPackInfo.content,
                                ResourcesProvider.instance.resPackInfo.releaseDate)
                }
                ResourcesProvider.instance.isBroken -> MyApp.context.getString(R.string.summary_curResPackVersion_broken_pref,
                        ResourcesProvider.instance.resPackInfo.content,
                        ResourcesProvider.instance.resPackInfo.releaseDate)
                else -> MyApp.context.getString(R.string.summary_curResPackVersion_pref,
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

    val resPackClickTimes = object : MutableLiveData<Int>() {
        override fun setValue(newValue: Int) {
            super.setValue(newValue)
            if (newValue >= 5) {
                super.setValue(0)
                showRemoveResPackMessageEvent.call()
            }
        }
    }

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
        showChooseResPackFileEvent.call()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        GlobalScope.launch(Dispatchers.IO) {
            val uri = data?.data ?: return@launch
            MyApp.context.contentResolver.openInputStream(uri).use { inputStream ->
                val tempFile = File(MyApp.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
                tempFile.createNewFile()
                tempFile.outputStream().use { output ->
                    IOUtils.copy(inputStream, output)
                }
                launch(Dispatchers.Main) {
                    showManuallyUpdateResPackEvent.call  (tempFile)
                }
            }
        }
    }
}