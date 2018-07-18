package com.ssttkkl.fgoplanningtool.ui.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.widget.Toast
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

class PreferencesFragment : PreferenceFragment() {
    private lateinit var resPackGroupPresenter: ResPackGroupPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        findPreference(KEY_VERSION).summary = MyApp.versionName

        resPackGroupPresenter = ResPackGroupPresenter(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            resPackGroupPresenter.onActivityResultOK(requestCode, data)
    }

    fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val KEY_VERSION = "version"
    }
}