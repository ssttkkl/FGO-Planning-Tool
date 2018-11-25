package com.ssttkkl.fgoplanningtool.ui.servantbaselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment

class ServantBaseListFragment : Fragment(), ServantListFragment.OnClickServantListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_servantbaselist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = true
            title = getString(R.string.title_servantbaselist)
        }
    }

    override fun onClickServant(servantId: Int) {
        ServantInfoDialogFragment.newInstance(servantId)
                .show(childFragmentManager, ServantInfoDialogFragment::class.qualifiedName)
    }
}