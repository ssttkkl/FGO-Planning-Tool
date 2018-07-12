package com.ssttkkl.fgoplanningtool.ui.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.widget.Toast
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

class PreferencesFragment : PreferenceFragment() {
    private lateinit var resourcesUpdatePresenter: ResourcesUpdatePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        findPreference(KEY_VERSION).summary = MyApp.versionName

        resourcesUpdatePresenter = ResourcesUpdatePresenter(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            resourcesUpdatePresenter.onActivityResultOK(requestCode, data)
    }

    fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val KEY_VERSION = "version"
    }
}