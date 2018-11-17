package com.ssttkkl.fgoplanningtool.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

class SettingsFragmentViewModel : ViewModel() {
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
}