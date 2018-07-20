package com.ssttkkl.fgoplanningtool.ui.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.utils.setStatusBarColor
import kotlinx.android.synthetic.main.fragment_ownitemlist.*

class PreferencesActivity : AppCompatActivity() {
    var requestRestart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_preferences)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState != null)
            requestRestart = savedInstanceState.getBoolean(KEY_REQUEST_RESTART, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(KEY_REQUEST_RESTART, requestRestart)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_REQUEST_RESTART, requestRestart)
        })
        super.onBackPressed()
    }

    companion object {
        const val KEY_REQUEST_RESTART = "request_restart"
    }
}