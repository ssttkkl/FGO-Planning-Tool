package com.ssttkkl.fgoplanningtool.utils

fun Int.toStringWithSplitter(splitter: String, groupLength: Int): String {
    return toLong().toStringWithSplitter(splitter, groupLength)
}

fun Long.toStringWithSplitter(splitter: String, groupLength: Int): String {
    if (this < 0)
        return '-' + (-this).toStringWithSplitter(splitter, groupLength)
    val sb = StringBuilder()
    this.toString().reversed().forEachIndexed { idx, c ->
        if (idx % groupLength == 0 && idx > 0)
            sb.append(splitter)
        sb.append(c)
    }
    return sb.toString().reversed()
}

fun Int.toStringWithSplitter() = this.toStringWithSplitter(",", 3)
fun Long.toStringWithSplitter() = this.toStringWithSplitter(",", 3)