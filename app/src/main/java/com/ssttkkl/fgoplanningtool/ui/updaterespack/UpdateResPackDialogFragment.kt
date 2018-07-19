package com.ssttkkl.fgoplanningtool.ui.updaterespack

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.downloader.request.DownloadRequest
import com.ssttkkl.fgoplanningtool.Dispatchers
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.net.LatestInfo
import com.ssttkkl.fgoplanningtool.net.ResPackDownloader
import com.ssttkkl.fgoplanningtool.resources.ResourcesUpdater
import kotlinx.android.synthetic.main.fragment_updaterespack.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File

class UpdateResPackDialogFragment : DialogFragment(), ResPackDownloader.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
    }

    private lateinit var viewModel: UpdateResPackViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(UpdateResPackViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_updaterespack, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.status.observe(this, Observer {
            onStatusChanged(it)
        })

        viewModel.latestInfo.observe(this, Observer {
            onLatestInfoChanged(it)
        })

        viewModel.size.observe(this, Observer {
            onSizeChanged(it ?: 0)
        })

        viewModel.progress.observe(this, Observer {
            onProgressChanged(it ?: 0)
        })

        viewModel.updater.setCallback(this)
        viewModel.updater.start()
    }

    private var isProgressIndeterminate: Boolean = false
        set(value) {
            if (field == value)
                return
            field = value
            if (value) { // determinate -> indeterminate
                progressBar?.isIndeterminate = true
                progress_textView?.visibility = View.GONE
            } else { // indeterminate -> determinate
                progressBar?.isIndeterminate = false
                if (isProgressVisible)
                    progress_textView?.visibility = View.VISIBLE
            }
        }

    private var isProgressVisible: Boolean = true
        set(value) {
            if (field == value)
                return
            field = value
            if (value) { // invisible -> visible
                progressBar?.visibility = View.VISIBLE
                if (isProgressIndeterminate)
                    progress_textView?.visibility = View.GONE
                else
                    progress_textView?.visibility = View.VISIBLE
            } else { // visible -> invisible
                progressBar?.visibility = View.INVISIBLE
                progress_textView?.visibility = View.GONE
            }
        }

    private fun onProgressChanged(progress: Int) {
        Log.d("UpdateResPack", "Download Progress: $progress")
        progressBar?.progress = progress
        progress_textView?.text = progress.toString()
    }

    private fun onSizeChanged(size: Long) {
        Log.d("UpdateResPack", "Size Changed: $size")
        size_textView?.text = getString(R.string.size_pattern_updaterespack, size.toDouble() / 1024 / 1024)
    }

    private fun onLatestInfoChanged(latestInfo: LatestInfo?) {
        Log.d("UpdateResPack", "LatestInfo Loaded: $latestInfo")
        content_textView?.text = if (latestInfo != null) latestInfo.content else ""
        releaseDate_textView?.text = if (latestInfo != null) getString(R.string.releaseDate_pattern_updaterespack,
                latestInfo.releaseDate / 10000,
                latestInfo.releaseDate % 10000 / 100,
                latestInfo.releaseDate % 100) else ""
    }

    private fun onStatusChanged(status: Status?) {
        Log.d("UpdateResPack", "Status Changed: $status")
        when (status) {
            Status.LoadingLatestInfo -> {
                status_textView?.text = getString(R.string.status_loading_updaterespack)
                isProgressIndeterminate = true
                isProgressVisible = true
            }
            Status.CompleteLoadingLatestInfo -> {
                status_textView?.text = getString(R.string.status_loadSuccessful_updaterespack)
                isProgressIndeterminate = true
                isProgressVisible = true
            }
            Status.FailedOnLoadingLatestInfo -> {
                status_textView?.text = getString(R.string.status_loadFailed_updaterespack)
                isProgressVisible = false
            }
            Status.Downloading -> {
                status_textView?.text = getString(R.string.status_downloading_updaterespack)
                isProgressIndeterminate = false
                isProgressVisible = true
            }
            Status.FailedOnDownloading -> {
                status_textView?.text = getString(R.string.status_downloadFailed_updaterespack)
                isProgressVisible = false
            }
            Status.Updating -> {
                status_textView?.text = getString(R.string.status_updating_updaterespack)
                isProgressIndeterminate = true
                isProgressVisible = true
            }
            Status.CompleteUpdating -> {
                isProgressIndeterminate = true
                isProgressVisible = true
            }
            Status.FailedOnUpdating -> {
                status_textView?.text = getString(R.string.status_updateFailed_updaterespack)
                isProgressVisible = false
            }
        }
    }

    override fun onStartLoadingLatestInfo() {
        viewModel.status.value = Status.LoadingLatestInfo
    }

    override fun onCompleteLoadingLatestInfo(latestInfo: LatestInfo) {
        viewModel.status.value = Status.CompleteLoadingLatestInfo
        viewModel.latestInfo.value = latestInfo
    }

    override fun onFailOnLoadingLatestInfo(message: String) {
        viewModel.status.value = Status.FailedOnLoadingLatestInfo
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDownloadProgress(progress: Int, downloadRequest: DownloadRequest) {
        viewModel.status.value = Status.Downloading
        viewModel.size.value = downloadRequest.totalBytes
        viewModel.progress.value = progress
    }

    override fun onCompleteDownloading(file: File) {
        viewModel.status.value = Status.Updating
        launch(Dispatchers.file) {
            try {
                ResourcesUpdater.update(file)
                launch(UI) {
                    viewModel.status.value = Status.CompleteUpdating
                    Toast.makeText(MyApp.context, R.string.updateResSuccessful_pref, Toast.LENGTH_SHORT).show()
                }
                MyApp.restart()
            } catch (exc: Exception) {
                launch(UI) {
                    viewModel.status.value = Status.FailedOnUpdating
                    Toast.makeText(MyApp.context, exc.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onFailOnDownloading(message: String) {
        viewModel.status.value = Status.FailedOnDownloading
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}