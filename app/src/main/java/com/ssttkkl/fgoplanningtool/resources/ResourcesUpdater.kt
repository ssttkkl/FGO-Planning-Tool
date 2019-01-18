package com.ssttkkl.fgoplanningtool.resources

import com.google.gson.Gson
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.utils.unzip
import java.io.File
import java.util.*

object ResourcesUpdater {
    fun update(file: File) {
        val cacheDir = File(MyApp.context.cacheDir.path, UUID.randomUUID().toString())
        cacheDir.mkdirs()
        unzip(file, cacheDir)
        check(cacheDir)

        val localResDir = ResourcesProvider.instance.resourcesDir
        localResDir.deleteRecursively()
        cacheDir.copyRecursively(localResDir, true)
        cacheDir.deleteRecursively()

        ResourcesProvider.renewInstance()
    }

    private fun check(dir: File) {
        if (!dir.isDirectory)
            throw Exception(MyApp.context.getString(R.string.isNotDir, dir.name))

        val resPackInfoFile = File(dir, ResourcesProvider.FILENAME_RES_PACK_INFO)
        if (resPackInfoFile.exists()) {
            resPackInfoFile.bufferedReader().use {
                val resPackInfo = Gson().fromJson<ResPackInfo>(it, ResPackInfo::class.java)
                if (resPackInfo.targetVersion < ResourcesProvider.TARGET_VERSION)
                    throw Exception(MyApp.context.getString(R.string.lowerTargetVersion))
                if (resPackInfo.targetVersion > ResourcesProvider.TARGET_VERSION)
                    throw Exception(MyApp.context.getString(R.string.higherTargetVersion))
            }
        }

        ResourcesProvider.FILENAMES.forEach { req ->
            val reqFile = File(dir, req)
            if (!reqFile.exists())
                throw Exception(MyApp.context.getString(R.string.fileNotExists, reqFile.name))
            else if (!reqFile.isFile)
                throw Exception(MyApp.context.getString(R.string.isNotFile, reqFile.name))
        }

        ResourcesProvider.DIRECTORY_NAMES.forEach { req ->
            val reqFile = File(dir, req)
            if (!reqFile.exists())
                throw Exception(MyApp.context.getString(R.string.dirNotExists, reqFile.name))
            else if (!reqFile.isDirectory)
                throw Exception(MyApp.context.getString(R.string.isNotDir, reqFile.name))
        }
    }

    fun remove() {
        val localResDir = ResourcesProvider.instance.resourcesDir
        localResDir.deleteRecursively()
        ResourcesProvider.renewInstance()
    }
}