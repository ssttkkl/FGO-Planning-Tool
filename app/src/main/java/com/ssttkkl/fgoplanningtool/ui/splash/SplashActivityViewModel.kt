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
import com.ssttkkl.fgoplanningtool.services.CheckUpdateService
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

        val pref = PreferenceManager.getDefaultSharedPreferences(context)

        // start CheckUpdateService
        if (pref.getBoolean(PreferenceKeys.KEY_CHECK_UPDATE_ON_START, true))
            context.startService(Intent(context, CheckUpdateService::class.java))

        // switch to default database
        val uuid = pref.getString(PreferenceKeys.KEY_DEFAULT_DB_UUID, "") ?: ""
        val dbDescriptor = if (DatabaseDescriptorManager[uuid] != null)
            uuid
        else
            DatabaseDescriptorManager.firstOrCreate.uuid
        Repo.switchDatabase(dbDescriptor)

        // wait for loading ResourcesPack
        GlobalScope.launch(Dispatchers.Main) {
            loadResTask.join()
            if (!ResourcesProvider.instance.isAbsent && !ResourcesProvider.instance.isBroken)
                enterAppEvent.call()
            else {
                showProgress.value = false
                message.value = when {
                    ResourcesProvider.instance.isAbsent -> context.getString(R.string.resAbsent_splash)
                    ResourcesProvider.instance.isBroken -> context.getString(R.string.resBroken_splash)
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