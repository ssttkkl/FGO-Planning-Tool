package com.ssttkkl.fgoplanningtool.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.utils.setStatusBarColor
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent())
        super.onBackPressed()
    }
}