package com.ssttkkl.fgoplanningtool.ui.splash

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptorManager
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.services.CheckUpdateService
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getBoolean(PreferenceKeys.KEY_CHECK_UPDATE_ON_START, true))
            startService(Intent(this, CheckUpdateService::class.java))

        val uuid = pref.getString(PreferenceKeys.KEY_DEFAULT_DB_UUID, "")
        val dbDescriptor = if (DatabaseDescriptorManager[uuid] != null)
            uuid
        else
            DatabaseDescriptorManager.firstOrCreate.uuid
        Repo.switchDatabase(dbDescriptor)

        GlobalScope.launch {
            viewModel.loadResTask.join()
            if (!ResourcesProvider.instance.isAbsent && !ResourcesProvider.instance.isBroken)
                gotoMainActivity()
            else {
                progressBar.visibility = View.GONE
                message_textView.visibility = View.VISIBLE
                enterApp_button.visibility = View.VISIBLE
                enterApp_button.setOnClickListener { gotoMainActivity() }
                when {
                    ResourcesProvider.instance.isAbsent ->
                        message_textView.text = getString(R.string.resAbsent_splash)
                    ResourcesProvider.instance.isBroken ->
                        message_textView.text = getString(R.string.resBroken_splash)
                }
            }
        }
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}