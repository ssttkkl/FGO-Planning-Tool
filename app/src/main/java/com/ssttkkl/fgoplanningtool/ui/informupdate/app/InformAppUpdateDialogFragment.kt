package com.ssttkkl.fgoplanningtool.ui.informupdate.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ssttkkl.fgoplanningtool.databinding.FragmentInformupdateAppBinding
import com.ssttkkl.fgoplanningtool.utils.AppUpdateInfo

class InformAppUpdateDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentInformupdateAppBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInformupdateAppBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[InformAppUpdateViewModel::class.java].apply {
            appUpdateInfo.value = arguments?.getParcelable(ARG_APP_INFO)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel?.apply {
            gotoDownloadPageEvent.observe(this@InformAppUpdateDialogFragment, Observer {
                gotoDownloadPage(it ?: return@Observer)
            })
            finishEvent.observe(this@InformAppUpdateDialogFragment, Observer {
                dismiss()
            })
        }
    }

    private fun gotoDownloadPage(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    companion object {
        private const val ARG_APP_INFO = "appUpdateInfo"

        fun newInstance(appInfo: AppUpdateInfo) = InformAppUpdateDialogFragment().apply {
            arguments = bundleOf(ARG_APP_INFO to appInfo)
        }
    }
}