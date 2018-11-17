package com.ssttkkl.fgoplanningtool.ui.updaterespack

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssttkkl.fgoplanningtool.databinding.FragmentUpdaterespackBinding
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
        if (binding.viewModel?.manually != true) {
            binding.viewModel?.start()
        } else {
            binding.viewModel?.startManually(File(arguments?.getString("filePath")))
        }
    }
}