package com.ssttkkl.fgoplanningtool.ui.planlist

import android.util.Log
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

// using an Edit Distance algorithm based-on Dynamic Programming
object PlanListRecViewAdapterDataSetChanger {
    fun perform(adapter: PlanListRecViewAdapter, old: ArrayList<Plan>, new: List<Plan>) {
        try {
            val f = runBlocking(CommonPool) { dp(old, new) }
            var cur = f[Pair(old.size - 1, new.size - 1)]
            while (cur != null) {
                when (cur.decision) {
                    0 -> {
                        if (old[cur.i] == new[cur.j])
                            Log.d("PlanList", "(${cur.i}, ${cur.j}) do nothing")
                        else {
                            old[cur.i] = new[cur.j]
                            adapter.notifyItemChanged(cur.i)
                            Log.d("PlanList", "(${cur.i}, ${cur.j}) changed ${cur.i} to new[${cur.j}]")
                        }
                    }
                    1 -> {
                        old.removeAt(cur.i)
                        adapter.notifyItemRemoved(cur.i)
                        Log.d("PlanList", "(${cur.i}, ${cur.j}) remove ${cur.i} in old sequence")
                    }
                    2 -> {
                        old.add(cur.i + 1, new[cur.j])
                        adapter.notifyItemInserted(cur.i + 1)
                        Log.d("PlanList", "(${cur.i}, ${cur.j}) insert new[${cur.j}] in old sequence at position ${cur.i + 1}")
                    }
                }
                cur = cur.previous
            }
            if (old.size != new.size || (old.indices.any { old[it] != new[it] }))
                throw Exception("Unknown error.")
        } catch (exc: Exception) {
            Log.e("PlanList", exc.message)
            old.clear()
            old.addAll(new)
            adapter.notifyDataSetChanged()
        }
    }

    private fun dp(a: List<Plan>, b: List<Plan>): Map<Pair<Int, Int>, DPState> {
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
                if (a[i].servantId == b[j].servantId) {
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
}