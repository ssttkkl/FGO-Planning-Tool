package com.ssttkkl.fgoplanningtool.ui.servantbaselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment

class ServantBaseListFragment : Fragment(), ServantListFragment.OnClickServantListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_servantbaselist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = true
            title = getString(R.string.servantBaseList)
        }
    }

    override fun onClickServant(servantID: Int) {
        findNavController().navigate(ServantBaseListFragmentDirections.actionServantBaseListFragmentToServantInfoFragment(servantID))
    }
}