package com.ssttkkl.fgoplanningtool.ui.informupdate.respack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentInformupdateRespackBinding
import com.ssttkkl.fgoplanningtool.utils.ResPackUpdateInfo

class InformResPackUpdateDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentInformupdateRespackBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInformupdateRespackBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[InformResPackUpdateViewModel::class.java].apply {
            resPackUpdateInfo.value = arguments?.getParcelable(ARG_RES_PACK_UPDATE_INFO)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel?.apply {
            gotoAutoUpdateUIEvent.observe(this@InformResPackUpdateDialogFragment, Observer {
                gotoAutoUpdateUI()
            })
            finishEvent.observe(this@InformResPackUpdateDialogFragment, Observer {
                dismiss()
            })
        }
    }

    private fun gotoAutoUpdateUI() {
        findNavController().navigate(R.id.action_global_updateResPackFragment)
    }

    companion object {
        private const val ARG_RES_PACK_UPDATE_INFO = "resPackUpdateInfo"

        fun newInstance(resPackUpdateInfo: ResPackUpdateInfo) = InformResPackUpdateDialogFragment().apply {
            arguments = bundleOf(ARG_RES_PACK_UPDATE_INFO to resPackUpdateInfo)
        }
    }
}