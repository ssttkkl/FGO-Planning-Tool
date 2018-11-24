package com.ssttkkl.fgoplanningtool.ui.settings.updaterespack.updater

import com.ssttkkl.fgoplanningtool.resources.ResPackInfo

data class ResPackLatestInfo(val targetVersion: Int,
                             val releaseDate: Int,
                             val content: String,
                             val downloadLink: String)