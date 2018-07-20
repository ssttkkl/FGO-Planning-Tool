package com.ssttkkl.fgoplanningtool.resources.migration

import com.google.gson.Gson
import com.ssttkkl.fgoplanningtool.resources.ResPackInfo
import java.io.File

object CommonMigration1 {
    fun migration(resDirFile: File) {
        val versionFile = File(resDirFile, "version")
        val resPackFile = File(resDirFile, "res_pack_info.json")
        if (versionFile.exists() && !resPackFile.exists()) {
            val versionText = versionFile.readText()
            try {
                val releaseDate = versionText.substring(0..7).toInt()
                val content = versionText.substring(8 until versionText.length)
                resPackFile.bufferedWriter().use {
                    Gson().toJson(ResPackInfo(releaseDate, content, 1), ResPackInfo::class.java, it)
                }
            } catch (exc: Exception) {
                resPackFile.bufferedWriter().use {
                    Gson().toJson(ResPackInfo(0, "", 1), ResPackInfo::class.java, it)
                }
            }

            if (resPackFile.exists())
                versionFile.delete()
        }
    }
}