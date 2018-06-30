package com.ssttkkl.fgoplanningtool.ui.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.widget.Toast
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

class PreferencesFragment : PreferenceFragment() {
    private lateinit var presenter: ImportAndExportPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        presenter = ImportAndExportPresenter(this)
        findPreference(ImportAndExportPresenter.KEY_VERSION).summary = MyApp.versionName
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            presenter.onActivityResultOK(requestCode, data)
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}