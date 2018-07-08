package com.ssttkkl.fgoplanningtool.data

import com.ssttkkl.fgoplanningtool.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

enum class HowToPerform { RunBlocking, Launch }

fun perform(howToPerform: HowToPerform, action: () -> Unit) {
    when (howToPerform) {
        HowToPerform.RunBlocking -> runBlocking(Dispatchers.db) { action.invoke() }
        HowToPerform.Launch -> launch(Dispatchers.db) { action.invoke() }
    }
}