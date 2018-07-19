package com.ssttkkl.fgoplanningtool.resources

import com.google.gson.Gson
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.utils.unzip
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.InputStream
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
            throw Exception("$dir isn't a directory.")

        val resPackInfoFile = File(dir, ResourcesProvider.FILENAME_RES_PACK_INFO)
        if (resPackInfoFile.exists()) {
            resPackInfoFile.bufferedReader().use {
                val resPackInfo = Gson().fromJson<ResPackInfo>(it, ResPackInfo::class.java)
                if (resPackInfo.targetVersion < ResourcesProvider.TARGET_VERSION)
                    throw Exception("Resource Pack's target version(${resPackInfo.targetVersion}) is lower than APP's(${ResourcesProvider.TARGET_VERSION}).")
                if (resPackInfo.targetVersion > ResourcesProvider.TARGET_VERSION)
                    throw Exception("APP's target version(${ResourcesProvider.TARGET_VERSION}) is lower than Resource Pack's(${resPackInfo.targetVersion}).")
            }
        }

        listOf(ResourcesProvider.FILENAME_SERVANT_INFO,
                ResourcesProvider.FILENAME_ITEM_INFO,
                ResourcesProvider.FILENAME_QP_INFO,
                ResourcesProvider.FILENAME_RES_PACK_INFO).forEach { req ->
            val reqFile = File(dir, req)
            if (!reqFile.exists())
                throw Exception("$dir doesn't contain file $req.")
            else if (!reqFile.isFile)
                throw Exception("$reqFile isn't a file.")
        }

        listOf(ResourcesProvider.DIRECTORYNAME_AVATAR,
                ResourcesProvider.DIRECTORYNAME_ITEM).forEach { req ->
            val reqFile = File(dir, req)
            if (!reqFile.exists())
                throw Exception("$dir doesn't contain file $req.")
            else if (!reqFile.isDirectory)
                throw Exception("$reqFile isn't a directory.")
        }
    }
}