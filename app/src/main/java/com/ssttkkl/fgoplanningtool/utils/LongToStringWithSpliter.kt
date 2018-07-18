package com.ssttkkl.fgoplanningtool.utils

fun Long.toStringWithSplitter(splitter: String = ",", groupLength: Int = 3): String {
    val sb = StringBuilder()
    this.toString().reversed().forEachIndexed { idx, c ->
        if (idx % groupLength == 0 && idx > 0)
            sb.append(splitter)
        sb.append(c)
    }
    return sb.toString().reversed()
}