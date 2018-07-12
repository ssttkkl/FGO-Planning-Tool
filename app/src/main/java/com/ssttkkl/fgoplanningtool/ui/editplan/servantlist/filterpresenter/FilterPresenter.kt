package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProviders
import android.view.View
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListViewModel
import net.cachapa.expandablelayout.ExpandableLayout

abstract class FilterPresenter(val view: ServantListFragment) {
    protected val viewModel = ViewModelProviders.of(view).get(ServantListViewModel::class.java)

    protected fun expandLayout(arrowView: View, expLayout: ExpandableLayout) {
        view.apply {
            (AnimatorInflater.loadAnimator(context!!,
                    if (expLayout.isExpanded) R.animator.rotate_180_0 else R.animator.rotate_0_180) as ValueAnimator).apply {
                addUpdateListener {
                    arrowView.rotation = it.animatedValue as Float
                    arrowView.postInvalidateOnAnimation()
                }
                start()
            }
            expLayout.isExpanded = !expLayout.isExpanded
        }
    }
}