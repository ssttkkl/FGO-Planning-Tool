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
            throw Exception(MyApp.context.getString(R.string.exc_isNotDir_resUpdater, dir.name))

        val resPackInfoFile = File(dir, ResourcesProvider.FILENAME_RES_PACK_INFO)
        if (resPackInfoFile.exists()) {
            resPackInfoFile.bufferedReader().use {
                val resPackInfo = Gson().fromJson<ResPackInfo>(it, ResPackInfo::class.java)
                if (resPackInfo.targetVersion < ResourcesProvider.TARGET_VERSION)
                    throw Exception(MyApp.context.getString(R.string.exc_lowTargetVersion_resUpdater))
                if (resPackInfo.targetVersion > ResourcesProvider.TARGET_VERSION)
                    throw Exception(MyApp.context.getString(R.string.exc_highTargetVersion_resUpdater))
            }
        }

        listOf(ResourcesProvider.FILENAME_SERVANT_INFO,
                ResourcesProvider.FILENAME_ITEM_INFO,
                ResourcesProvider.FILENAME_QP_INFO,
                ResourcesProvider.FILENAME_RES_PACK_INFO).forEach { req ->
            val reqFile = File(dir, req)
            if (!reqFile.exists())
                throw Exception(MyApp.context.getString(R.string.exc_fileNotExists_resUpdater, reqFile.name))
            else if (!reqFile.isFile)
                throw Exception(MyApp.context.getString(R.string.exc_isNotFile_resUpdater, reqFile.name))
        }

        listOf(ResourcesProvider.DIRECTORYNAME_AVATAR,
                ResourcesProvider.DIRECTORYNAME_ITEM).forEach { req ->
            val reqFile = File(dir, req)
            if (!reqFile.exists())
                throw Exception(MyApp.context.getString(R.string.exc_dirNotExists_resUpdater, reqFile.name))
            else if (!reqFile.isDirectory)
                throw Exception(MyApp.context.getString(R.string.exc_isNotDir_resUpdater, reqFile.name))
        }
    }
}