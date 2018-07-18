package com.ssttkkl.fgoplanningtool.ui.preferences

import android.content.Intent
import android.webkit.MimeTypeMap
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.utils.unzip
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.InputStream
import java.util.*

class ResourcesUpdatePresenter(val view: PreferencesFragment) {
    fun process(input: InputStream) {
        val tempFile = File(MyApp.context.cacheDir, "${UUID.randomUUID()}.zip")
        tempFile.createNewFile()
        tempFile.outputStream().use { output ->
            IOUtils.copy(input, output)
        }
        process(tempFile)
        tempFile.delete()
    }

    fun process(file: File) {
        val cacheDir = File(MyApp.context.cacheDir.path, UUID.randomUUID().toString())
        cacheDir.mkdirs()
        unzip(file, cacheDir)
        check(cacheDir)

        val localResDir = ResourcesProvider.instance.resourcesDir
        localResDir.deleteRecursively()
        cacheDir.copyRecursively(localResDir, true)
        cacheDir.deleteRecursively()
    }

    private fun check(dir: File) {
        if (!dir.isDirectory)
            throw Exception("$dir isn't a directory.")

        val sub = dir.listFiles()
        listOf(ResourcesProvider.FILENAME_SERVANT_INFO,
                ResourcesProvider.FILENAME_ITEM_INFO,
                ResourcesProvider.FILENAME_QP_INFO,
                ResourcesProvider.FILENAME_VERSION).forEach { req ->
            val reqFile = sub.firstOrNull { it.name == req }
            if (reqFile == null)
                throw Exception("$dir doesn't contain file $req.")
            else if (!reqFile.isFile)
                throw Exception("$reqFile isn't a file.")
        }

        listOf(ResourcesProvider.DIRECTORYNAME_AVATAR,
                ResourcesProvider.DIRECTORYNAME_ITEM).forEach { req ->
            val reqFile = sub.firstOrNull { it.name == req }
            if (reqFile == null)
                throw Exception("$dir doesn't contain file $req.")
            else if (!reqFile.isDirectory)
                throw Exception("$reqFile isn't a directory.")
        }
    }

    init {
        val pref = view.findPreference(KEY_UPDATE_RES)
        launch(Dispatchers.file) {
            pref.summary = if (ResourcesProvider.instance.isAbsent)
                view.getString(R.string.noRes_pref)
            else if (ResourcesProvider.instance.isBroken)
                view.getString(R.string.brokenCurResVersion_pref, ResourcesProvider.instance.version)
            else
                view.getString(R.string.curResVersion_pref, ResourcesProvider.instance.version)
        }
        pref.setOnPreferenceClickListener {
            gotoOpenZipUi(REQUEST_CODE_UPDATE_RES)
            true
        }
    }

    private fun gotoOpenZipUi(requestCode: Int) {
        view.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")
            addCategory(Intent.CATEGORY_OPENABLE)
        }, requestCode)
    }

    fun onActivityResultOK(requestCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_UPDATE_RES) {
            runBlocking(Dispatchers.file) {
                try {
                    view.activity.contentResolver.openInputStream(data!!.data).use { stream ->
                        process(stream)
                        launch(UI) {
                            view.showMessage(view.getString(R.string.updateResSuccessful_pref))
                            ResourcesProvider.renewInstance()
                            view.activity.startActivity(view.activity.packageManager.getLaunchIntentForPackage(view.activity.packageName).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) // restart all activity
                        }
                    }
                } catch (e: Exception) {
                    launch(UI) {
                        view.showMessage(view.getString(R.string.updateResFailed_pref, e.localizedMessage))
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_UPDATE_RES = 1
        private const val KEY_UPDATE_RES = "updateRes"
    }
}