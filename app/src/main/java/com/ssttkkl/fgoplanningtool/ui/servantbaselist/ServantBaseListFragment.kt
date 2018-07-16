package com.ssttkkl.fgoplanningtool.ui.servantbaselist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment

class ServantBaseListFragment : Fragment(), ServantListFragment.OnServantSelectedListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_servantbaselist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ServantListFragment(), ServantListFragment::class.qualifiedName)
                .commit()
    }

    override fun onServantSelected(servantId: Int) {
        ServantInfoDialogFragment.newInstance(servantId)
                .show(childFragmentManager, ServantInfoDialogFragment::class.qualifiedName)
    }
}