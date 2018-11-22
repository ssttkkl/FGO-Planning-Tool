package com.ssttkkl.fgoplanningtool.ui.servantbaselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantbaselistBinding
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment

class ServantBaseListFragment : Fragment(), ServantListFragment.OnClickServantListener {
    private lateinit var binding: FragmentServantbaselistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServantbaselistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            setupDrawerToggle(binding.toolbar)
        }
    }

    override fun onClickServant(servantId: Int) {
        ServantInfoDialogFragment.newInstance(servantId)
                .show(childFragmentManager, ServantInfoDialogFragment::class.qualifiedName)
    }
}