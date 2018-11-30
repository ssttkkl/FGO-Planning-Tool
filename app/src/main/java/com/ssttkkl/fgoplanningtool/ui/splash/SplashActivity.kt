package com.ssttkkl.fgoplanningtool.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.ActivitySplashBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[SplashActivityViewModel::class.java]

        binding.viewModel?.apply {
            enterAppEvent.observe(this@SplashActivity, Observer {
                enterApp()
            })
            start(this@SplashActivity)
        }
    }

    private fun enterApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}