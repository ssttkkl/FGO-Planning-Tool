package com.ssttkkl.fgoplanningtool.ui.utils

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip


class ChipViewTarget(chip: Chip) : CustomViewTarget<Chip, Drawable>(chip) {
    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        view.chipIcon = resource
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        view.chipIcon = errorDrawable
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        view.chipIcon = placeholder
    }
}