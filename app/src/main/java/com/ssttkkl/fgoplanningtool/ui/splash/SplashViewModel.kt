package com.ssttkkl.fgoplanningtool.ui.splash

import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    val loadResTask = GlobalScope.launch(Dispatchers.IO) {
        ResourcesProvider.renewInstance()
    }

    override fun onCleared() {
        super.onCleared()
        loadResTask.cancel()
    }
}