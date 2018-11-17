package com.ssttkkl.fgoplanningtool.ui.updaterespack.updater

import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ResPackManuallyUpdater(val file: File) {
    interface Callback {
        fun onStartUpdating()
        fun onCompleteUpdating()
        fun onFailOnUpdating(message: String)
    }

    private var callback: Callback? = null

    fun setCallback(newCallback: Callback) {
        callback = newCallback
    }

    private val work = GlobalScope.launch(Dispatchers.Default, CoroutineStart.LAZY) {
        launch(Main) { callback?.onStartUpdating() }
        launch(Dispatchers.IO) {
            try {
                ResourcesUpdater.update(file)
                launch(Main) { callback?.onCompleteUpdating() }
            } catch (exc: Exception) {
                launch(Main) { callback?.onFailOnUpdating(exc.toString()) }
            }
        }
    }

    fun start(): Boolean = work.start()

    fun cancel() {
        work.cancel()
    }
}