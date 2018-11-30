package com.ssttkkl.fgoplanningtool.ui.settings.updaterespack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentUpdaterespackBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import java.io.File

class UpdateResPackFragment : Fragment() {
    private lateinit var binding: FragmentUpdaterespackBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdaterespackBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[UpdateResPackViewModel::class.java].apply {
            manually = arguments?.getBoolean("manually") == true
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
            title = getString(R.string.title_updaterespack)
            invalidateOptionsMenu()
        }

        if (binding.viewModel?.manually != true) {
            binding.viewModel?.start()
        } else {
            binding.viewModel?.startManually(File(arguments?.getString("filePath")))
        }
    }
}