package com.ssttkkl.fgoplanningtool.ui.preferences

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.webkit.MimeTypeMap
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import com.ssttkkl.fgoplanningtool.ui.updaterespack.UpdateResPackDialogFragment
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ResPackGroupPresenter(val view: PreferencesFragment) {
    init {
        val curVersion = view.findPreference(KEY_CUR_VERSION)
        curVersion.summary = when {
            ResourcesProvider.instance.isAbsent -> view.getString(R.string.summary_curResPackVersion_absent_pref)
            ResourcesProvider.instance.isNotTargeted -> {
                if (ResourcesProvider.instance.resPackInfo.targetVersion < ResourcesProvider.TARGET_VERSION)
                    view.getString(R.string.summary_curResPackVersion_lowTargetVersion_pref,
                            ResourcesProvider.instance.resPackInfo.content,
                            ResourcesProvider.instance.resPackInfo.releaseDate)
                else
                    view.getString(R.string.summary_curResPackVersion_highTargetVersion_pref,
                            ResourcesProvider.instance.resPackInfo.content,
                            ResourcesProvider.instance.resPackInfo.releaseDate)
            }
            ResourcesProvider.instance.isBroken -> view.getString(R.string.summary_curResPackVersion_broken_pref,
                    ResourcesProvider.instance.resPackInfo.content,
                    ResourcesProvider.instance.resPackInfo.releaseDate)
            else -> view.getString(R.string.summary_curResPackVersion_pref,
                    ResourcesProvider.instance.resPackInfo.content,
                    ResourcesProvider.instance.resPackInfo.releaseDate)
        }

        view.findPreference(KEY_AUTO_UPDATE).setOnPreferenceClickListener {
            val tag = UpdateResPackDialogFragment::class.qualifiedName
            val fm = (view.activity as AppCompatActivity).supportFragmentManager
            if (fm.findFragmentByTag(tag) == null)
                UpdateResPackDialogFragment().show(fm, tag)
            true
        }

        view.findPreference(KEY_MANUALLY_UPDATE).setOnPreferenceClickListener {
            gotoOpenZipUi(REQUEST_CODE_UPDATE_RES)
            true
        }
    }

    private fun gotoOpenZipUi(requestCode: Int) {
        view.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")
            addCategory(Intent.CATEGORY_OPENABLE)
        }, requestCode)
    }

    fun onActivityResultOK(requestCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_UPDATE_RES) {
            launch(Dispatchers.file) {
                try {
                    view.activity.contentResolver.openInputStream(data!!.data).use { stream ->
                        ResourcesUpdater.update(stream)
                        launch(UI) {
                            view.showMessage(view.getString(R.string.updateResSuccessful_pref))
                            MyApp.restart()
                        }
                    }
                } catch (e: Exception) {
                    launch(UI) {
                        view.showMessage(view.getString(R.string.updateResFailed_pref, e.localizedMessage))
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_UPDATE_RES = 1
        private const val KEY_CUR_VERSION = "curResPackVersion"
        private const val KEY_AUTO_UPDATE = "autoUpdateRes"
        private const val KEY_MANUALLY_UPDATE = "manuallyUpdateRes"
    }
}