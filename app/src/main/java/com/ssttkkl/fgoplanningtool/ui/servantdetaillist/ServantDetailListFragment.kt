package com.ssttkkl.fgoplanningtool.ui.servantdetaillist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment

class ServantDetailListFragment : Fragment(), ServantListFragment.OnServantSelectedListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_servantdetaillist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ServantListFragment(), ServantListFragment::class.qualifiedName)
                .commit()
    }

    override fun onServantSelected(servantId: Int) {
        ServantDetailDialogFragment.newInstance(servantId)
                .show(childFragmentManager, ServantDetailDialogFragment::class.qualifiedName)
    }
}