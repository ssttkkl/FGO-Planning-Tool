package com.ssttkkl.fgoplanningtool.ui.splash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        launch(Dispatchers.file) {
            ResourcesProvider.renewInstance()
            launch(UI) {
                if (!ResourcesProvider.instance.isAbsent && !ResourcesProvider.instance.isNotTargeted && !ResourcesProvider.instance.isBroken)
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
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
    }
}