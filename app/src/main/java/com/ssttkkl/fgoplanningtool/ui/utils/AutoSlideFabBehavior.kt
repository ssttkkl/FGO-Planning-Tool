package com.ssttkkl.fgoplanningtool.ui.utils

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AutoSlideFabBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior(context, attrs) {
    private var slideOutAnimator: ValueAnimator? = null

    private var slideInAnimator: ValueAnimator? = null

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyConsumed > 0) {
            animateOut(child)
        } else if (dyConsumed < 0) {
            animateIn(child)
        }
    }

    private fun animateOut(button: FloatingActionButton) {
        slideInAnimator?.cancel()
        if (slideOutAnimator?.isRunning == true)
            return
        slideOutAnimator = ValueAnimator.ofFloat(button.translationY, 1f * button.height + button.marginBottom).apply {
            duration = 300
            addUpdateListener {
                button.translationY = animatedValue as Float
                button.postInvalidateOnAnimation()
            }
            start()
        }
    }

    private fun animateIn(button: FloatingActionButton) {
        slideOutAnimator?.cancel()
        if (slideInAnimator?.isRunning == true)
            return
        slideInAnimator = ValueAnimator.ofFloat(button.translationY, 0f).apply {
            duration = 300
            addUpdateListener {
                button.translationY = animatedValue as Float
                button.postInvalidateOnAnimation()
            }
            start()
        }
    }
}