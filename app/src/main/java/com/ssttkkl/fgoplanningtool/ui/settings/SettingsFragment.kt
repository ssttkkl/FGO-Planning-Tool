package com.ssttkkl.fgoplanningtool.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import java.io.File
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsFragmentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this)[SettingsFragmentViewModel::class.java]
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        subscribeUI()
    }

    override fun onPreferenceChange(pref: Preference, newValue: Any): Boolean {
        if (pref is ListPreference) {
            val entries = pref.entries
            val values = pref.entryValues
            pref.summary = entries[values.indexOf(newValue)]
        }
        return true
    }

    private fun setupListPreferenceListener(pref: ListPreference) {
        pref.onPreferenceChangeListener = this
        pref.summary = pref.entry
    }

    private fun subscribeUI() {
        findPreference(KEY_VERSION).summary = viewModel.versionName

        viewModel.resPackSummary.observe(this, Observer {
            findPreference(KEY_CUR_VERSION).summary = it
        })

        findPreference(KEY_AUTO_UPDATE).setOnPreferenceClickListener {
            gotoAutoUpdateResPackUI()
            true
        }

        findPreference(KEY_MANUALLY_UPDATE).setOnPreferenceClickListener {
            gotoOpenResPackUI()
            true
        }
        setupListPreferenceListener(findPreference(PreferenceKeys.KEY_NAME_LANGUAGE) as ListPreference)
    }

    private fun gotoAutoUpdateResPackUI() {
        findNavController().navigate(R.id.action_settingsFragment_to_updateResPackFragment)
    }

    private fun gotoManuallyUpdateResPackUI(file: File) {
        findNavController().navigate(R.id.action_settingsFragment_to_updateResPackFragment, Bundle().apply {
            putBoolean("manually", true)
            putString("filePath", file.path)
        })
    }

    private fun gotoOpenResPackUI() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")
            addCategory(Intent.CATEGORY_OPENABLE)
        }, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            GlobalScope.launch(Dispatchers.IO) {
                val uri = data?.data ?: return@launch
                activity?.contentResolver?.openInputStream(uri)?.use { inputStream ->
                    val tempFile = File(MyApp.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
                    tempFile.createNewFile()
                    tempFile.outputStream().use { output ->
                        IOUtils.copy(inputStream, output)
                    }
                    launch(Dispatchers.Main) {
                        gotoManuallyUpdateResPackUI(tempFile)
                    }
                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val KEY_VERSION = "version"
        private const val KEY_CUR_VERSION = "curResPackVersion"
        private const val KEY_AUTO_UPDATE = "autoUpdateRes"
        private const val KEY_MANUALLY_UPDATE = "manuallyUpdateRes"
    }
}