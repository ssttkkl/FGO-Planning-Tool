package com.ssttkkl.fgoplanningtool.ui.splash

import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import kotlinx.coroutines.experimental.launch

class SplashViewModel : ViewModel() {
    val loadResTask = launch(Dispatchers.file) {
        ResourcesProvider.renewInstance()
    }

    override fun onCleared() {
        super.onCleared()
        loadResTask.cancel()
    }
}