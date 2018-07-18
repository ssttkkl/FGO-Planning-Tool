package com.ssttkkl.fgoplanningtool

import kotlinx.coroutines.experimental.newSingleThreadContext

object Dispatchers {
    val db = newSingleThreadContext("db")
    val file = newSingleThreadContext("file")
    val net = newSingleThreadContext("net")
}