package com.ssttkkl.fgoplanningtool.ui.utils

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*

// using an Edit Distance algorithm based-on Dynamic Programming
fun <T> androidx.recyclerview.widget.RecyclerView.Adapter<out androidx.recyclerview.widget.RecyclerView.ViewHolder>.changeDataSetSmoothly(old: MutableList<T>,
                                                                                                                                          new: List<T>,
                                                                                                                                          judgeSameItemBy: (T) -> Any) {
    synchronized(old) {
        try {
            val f = runBlocking(Dispatchers.Default) { dp(old, new, judgeSameItemBy) }
            var cur = f[Pair(old.size - 1, new.size - 1)]
            while (cur != null) {
                when (cur.decision) {
                    0 -> {
                        if (old[cur.i] != new[cur.j]) {
                            old[cur.i] = new[cur.j]
                            notifyItemChanged(cur.i)
                        }
                    }
                    1 -> {
                        old.removeAt(cur.i)
                        notifyItemRemoved(cur.i)
                    }
                    2 -> {
                        old.add(cur.i + 1, new[cur.j])
                        notifyItemInserted(cur.i + 1)
                    }
                }
                cur = cur.previous
            }
            if (old.size != new.size || (old.indices.any { old[it] != new[it] }))
                throw Exception("Unknown error.")
        } catch (exc: Exception) {
            Log.e("DataSetChanger", exc.message)
            old.clear()
            old.addAll(new)
            notifyDataSetChanged()
        }
    }
}

private fun <T> dp(a: List<T>,
                   b: List<T>,
                   judgeSameItemBy: (T) -> Any): Map<Pair<Int, Int>, DPState> {
    /*
    f(i, j) = {
        f(i - 1, j - 1), if a[i] == b[j]
        min(f(i - 1, j), f(i, j - 1)) + 1, if a[i] != b[j]
    }
    */
    val f = HashMap<Pair<Int, Int>, DPState>()
    f[Pair(-1, -1)] = DPState(-1, -1, 0, null, -1)
    for (i in a.indices) {
        val prev = f[Pair(i - 1, -1)]!!
        f[Pair(i, -1)] = DPState(i, -1, prev.distance + 1, prev, 1)
    }
    for (j in b.indices) {
        val prev = f[Pair(-1, j - 1)]!!
        f[Pair(-1, j)] = DPState(-1, j, prev.distance + 1, prev, 2)
    }
    for (i in a.indices) {
        for (j in b.indices) {
            if (judgeSameItemBy(a[i]) == judgeSameItemBy(b[j])) {
                val prev = f[Pair(i - 1, j - 1)]!!
                f[Pair(i, j)] = DPState(i, j, prev.distance, prev, 0)
            } else {
                val prev1 = f[Pair(i - 1, j)]!!
                val prev2 = f[Pair(i, j - 1)]!!
                f[Pair(i, j)] = if (prev1.distance <= prev2.distance)
                    DPState(i, j, prev1.distance + 1, prev1, 1)
                else
                    DPState(i, j, prev2.distance + 1, prev2, 2)
            }
        }
    }
    return f
}

data class DPState(val i: Int,
                   val j: Int,
                   val distance: Int,
                   val previous: DPState?,
                   val decision: Int)