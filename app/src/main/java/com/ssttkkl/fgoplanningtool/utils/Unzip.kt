package com.ssttkkl.fgoplanningtool.utils

import org.apache.commons.io.IOUtils
import java.io.File
import java.util.zip.ZipFile

fun unzip(file: File, targetDir: File) {
    ZipFile(file).use { zipFile ->
        zipFile.entries().iterator().forEach { entry ->
            val cur = File(targetDir.path, entry.name)
            if (cur.exists())
                cur.deleteRecursively()

            if (entry.isDirectory)
                cur.mkdirs()
            else {
                val input = zipFile.getInputStream(entry)
                val output = cur.outputStream()
                IOUtils.copy(input, output)
                input.close()
                output.close()
            }
        }
    }
}