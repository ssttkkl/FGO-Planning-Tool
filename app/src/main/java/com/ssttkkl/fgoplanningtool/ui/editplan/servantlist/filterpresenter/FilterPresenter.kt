package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.RotateDrawable
import android.widget.TextView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListViewModel
import net.cachapa.expandablelayout.ExpandableLayout

abstract class FilterPresenter(val view: ServantListFragment) {
    protected val viewModel = ViewModelProviders.of(view).get(ServantListViewModel::class.java)

    protected fun expandLayout(textView: TextView, expLayout: ExpandableLayout) {
        view.apply {
            val drawable = textView.compoundDrawables.first { it is RotateDrawable } as RotateDrawable
            val animator = AnimatorInflater.loadAnimator(context, if (!expLayout.isExpanded) R.animator.rotate else R.animator.rotate_reversed) as ValueAnimator
            animator.addUpdateListener {
                drawable.level = it.animatedValue as Int
            }
            animator.start()
            expLayout.isExpanded = !expLayout.isExpanded
        }
    }
}