package com.ssttkkl.fgoplanningtool.ui.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R

class PreferencesFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {
    private lateinit var resPackGroupPresenter: ResPackGroupPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        findPreference(KEY_VERSION).summary = MyApp.versionName
        setListPreferenceListener(findPreference(PreferenceKeys.KEY_NAME_LANGUAGE) as ListPreference)

        resPackGroupPresenter = ResPackGroupPresenter(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            resPackGroupPresenter.onActivityResultOK(requestCode, data)
    }

    override fun onPreferenceChange(pref: Preference, newValue: Any): Boolean {
        if (pref is ListPreference) {
            val entries = pref.entries
            val values = pref.entryValues
            pref.summary = entries[values.indexOf(newValue)]
        }
        return true
    }

    private fun setListPreferenceListener(pref: ListPreference) {
        pref.onPreferenceChangeListener = this
        pref.summary = pref.entry
    }

    companion object {
        private const val KEY_VERSION = "version"
    }
}