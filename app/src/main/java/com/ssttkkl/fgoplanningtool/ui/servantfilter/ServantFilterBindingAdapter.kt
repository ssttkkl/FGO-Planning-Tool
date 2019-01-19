package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.graphics.drawable.RotateDrawable
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet

object ServantFilterBindingAdapter {
    @BindingAdapter("rotateDrawableEnd")
    @JvmStatic
    fun setRotateDrawableEnd(textView: TextView, oldValue: Boolean, newValue: Boolean) {
        if (oldValue != newValue) {
            val drawable = textView.compoundDrawablesRelative[2] as? RotateDrawable ?: return
            drawable.mutate()

            (AnimatorInflater.loadAnimator(MyApp.context, if (newValue) R.animator.rotate else R.animator.rotate_reversed) as ValueAnimator).apply {
                addUpdateListener { drawable.level = it.animatedValue as Int }
                start()
            }
        }
    }

    // adapters for Order
    @InverseBindingAdapter(attribute = "app:checkedChip", event = "checkedChipAttrChanged")
    @JvmStatic
    fun getOrderCheckedChip(chipGroup: ChipGroup): Order? {
        return try {
            val idx = chipGroup.indexOfChild(chipGroup.findViewById(chipGroup.checkedChipId))
            Order.values()[idx]
        } catch (_: Exception) {
            null
        }
    }

    // adapters for OrderBy
    @InverseBindingAdapter(attribute = "app:checkedChip", event = "checkedChipAttrChanged")
    @JvmStatic
    fun getOrderByCheckedChip(chipGroup: ChipGroup): OrderBy? {
        return try {
            val idx = chipGroup.indexOfChild(chipGroup.findViewById(chipGroup.checkedChipId))
            OrderBy.values()[idx]
        } catch (_: Exception) {
            null
        }
    }

    // adapters for Star
    @InverseBindingAdapter(attribute = "app:checkedChip", event = "checkedChipAttrChanged")
    @JvmStatic
    fun getStarCheckedChip(chipGroup: ChipGroup): Set<Star> {
        val set = HashSet<Star>()
        chipGroup.forEachIndexed { idx, it ->
            val chip = it as Chip
            if (chip.isChecked) {
                try {
                    set.add(Star.values()[idx])
                } catch (_: Exception) { }
            }
        }
        return set
    }

    // adapters for ServantClass
    @InverseBindingAdapter(attribute = "app:checkedChip", event = "checkedChipAttrChanged")
    @JvmStatic
    fun getServantClassCheckedChip(chipGroup: ChipGroup): Set<ServantClass> {
        val set = HashSet<ServantClass>()
        chipGroup.forEachIndexed { idx, it ->
            val chip = it as Chip
            if (chip.isChecked) {
                try {
                    set.add(ServantClass.values()[idx])
                } catch (_: Exception) { }
            }
        }
        return set
    }

    // adapters for WayToGet
    @InverseBindingAdapter(attribute = "app:checkedChip", event = "checkedChipAttrChanged")
    @JvmStatic
    fun getWayToGetCheckedChip(chipGroup: ChipGroup): Set<WayToGet> {
        val set = HashSet<WayToGet>()
        chipGroup.forEachIndexed { idx, it ->
            val chip = it as Chip
            if (chip.isChecked) {
                try {
                    set.add(WayToGet.values()[idx])
                } catch (_: Exception) { }
            }
        }
        return set
    }

    // adapters for ItemFilterMode
    @BindingAdapter("selection")
    @JvmStatic
    fun setSelection(spinner: Spinner, selection: ItemFilterMode?) {
        spinner.setSelection(selection?.ordinal ?: -1)
    }

    @InverseBindingAdapter(attribute = "selection", event = "onItemSelected")
    @JvmStatic
    fun getSelection(spinner: Spinner): ItemFilterMode? {
        return try {
            ItemFilterMode.values()[spinner.selectedItemPosition]
        } catch (_: Exception) {
            null
        }
    }
}