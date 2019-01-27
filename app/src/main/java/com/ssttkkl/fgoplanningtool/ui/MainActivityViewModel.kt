package com.ssttkkl.fgoplanningtool.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import com.ssttkkl.fgoplanningtool.utils.AppUpdateInfo
import com.ssttkkl.fgoplanningtool.utils.ConstantLinks
import com.ssttkkl.fgoplanningtool.utils.ResPackUpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class MainActivityViewModel : ViewModel() {
    val drawerState = MutableLiveData<Boolean>()
    val title = MutableLiveData<String>()
    val databaseDescriptor get() = Repo.databaseDescriptor

    val gotoDatabaseManageUIEvent = SingleLiveEvent<Unit>()
    val showInformAppUpdateUIEvent = SingleLiveEvent<AppUpdateInfo>()
    val showInformResPackUpdateUIEvent = SingleLiveEvent<ResPackUpdateInfo>()

    fun start() {
        try {
            val gson = GsonBuilder().registerTypeAdapter(ResPackUpdateInfo::class.java, ResPackUpdateInfo.GsonTypeAdapter())
                    .registerTypeAdapter(AppUpdateInfo::class.java, AppUpdateInfo.GsonTypeAdapter())
                    .create()
            checkResPackUpdate(gson)
            checkAppUpdate(gson)
        } catch (_: Exception) {
        }
    }

    private fun checkResPackUpdate(gson: Gson) {
        GlobalScope.launch(Dispatchers.Default) {
            val url = URL(ConstantLinks.urlPattern.format(ConstantLinks.resPackLatestInfoFilename))
            val resPackUpdateInfo = gson.fromJson<ResPackUpdateInfo>(url.readText(), ResPackUpdateInfo::class.java)
            Log.d("CheckUpdate", resPackUpdateInfo.toString())
            if (resPackUpdateInfo.releaseDate > ResourcesProvider.instance.resPackInfo.releaseDate) {
                launch(Dispatchers.Main) { showInformResPackUpdateUIEvent.call(resPackUpdateInfo) }
            }
        }
    }

    private fun checkAppUpdate(gson: Gson) {
        GlobalScope.launch(Dispatchers.Default) {
            val url = URL(ConstantLinks.urlPattern.format(ConstantLinks.appUpdateInfoFilename))
            val appUpdateInfo = gson.fromJson<AppUpdateInfo>(url.readText(), AppUpdateInfo::class.java)
            Log.d("CheckUpdate", appUpdateInfo.toString())
            if (appUpdateInfo.versionCode > MyApp.versionCode) {
                launch(Dispatchers.Main) { showInformAppUpdateUIEvent.call(appUpdateInfo) }
            }
        }
    }

    fun onClickManageDatabases() {
        gotoDatabaseManageUIEvent.call()
    }
}