package com.ssttkkl.fgoplanningtool.net.respack.entity

data class LatestInfo(val targetVersion: Int,
                      val releaseDate: Int,
                      val filename: String) {
    companion object {
        val url = "https://raw.githubusercontent.com/ssttkkl/FGO-Planning-Tool/res-pack-test/latest.json"
    }
}