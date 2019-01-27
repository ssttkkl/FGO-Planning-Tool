package com.ssttkkl.fgoplanningtool.ui.splash

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivityViewModel : ViewModel() {
    val loadResTask = GlobalScope.launch(Dispatchers.IO) {
        ResourcesProvider.renewInstance()
    }

    val enterAppEvent = SingleLiveEvent<Void>()

    val showProgress = MutableLiveData<Boolean>().apply { value = true }
    val message = MutableLiveData<String>()

    fun start(context: Context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)

        // wait for loading ResourcesPack
        GlobalScope.launch(Dispatchers.Main) {
            loadResTask.join()
            if (!ResourcesProvider.instance.isAbsent && !ResourcesProvider.instance.isBroken)
                enterAppEvent.call()
            else {
                showProgress.value = false
                message.value = when {
                    ResourcesProvider.instance.isAbsent -> context.getString(R.string.resPackAbsentHint)
                    ResourcesProvider.instance.isBroken -> context.getString(R.string.resPackBrokenHint)
                    else -> ""
                }
            }
        }
    }

    fun onClickEnterApp() {
        enterAppEvent.call()
    }

    override fun onCleared() {
        super.onCleared()
        loadResTask.cancel()
    }
}