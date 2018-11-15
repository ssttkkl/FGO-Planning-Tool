package com.ssttkkl.fgoplanningtool.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class HowToPerform { RunBlocking, Launch }

fun perform(howToPerform: HowToPerform, action: () -> Unit) {
    when (howToPerform) {
        HowToPerform.RunBlocking -> runBlocking(Dispatchers.IO) { action.invoke() }
        HowToPerform.Launch -> GlobalScope.launch(Dispatchers.IO) { action.invoke() }
    }
}