package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ssttkkl.fgoplanningtool.ui.servantfilter.Order
import com.ssttkkl.fgoplanningtool.utils.Localizable

object ChipGroupBindingAdapter {
    @BindingAdapter(value = ["checkableEntities", "checkedChipAttrChanged"], requireAll = false)
    @JvmStatic
    fun setEntities(chipGroup: ChipGroup,
                    entities: List<Localizable>,
                    checkedChipAttrChanged: InverseBindingListener?) {
        TransitionManager.beginDelayedTransition(chipGroup)
        chipGroup.removeAllViews()
        entities.forEach { entity ->
            val chip = Chip(chipGroup.context).apply {
                text = entity.localizedName
                isCheckable = true
                isClickable = true
                transitionName = entity.toString()
            }
            chipGroup.addView(chip)
        }
        TransitionManager.endTransitions(chipGroup)

        if (checkedChipAttrChanged != null) {
            // chipGroup only call its listener when it is single selection
            if (chipGroup.isSingleSelection)
                chipGroup.setOnCheckedChangeListener { _, _ ->
                    checkedChipAttrChanged.onChange()
                }
            else {
                chipGroup.setOnCheckedChangeListener(null)
                chipGroup.forEach {
                    (it as Chip).setOnCheckedChangeListener { _, _ ->
                        checkedChipAttrChanged.onChange()
                    }
                }
            }
        }
    }

    @BindingAdapter("app:checkedChip")
    @JvmStatic
    fun <E : Enum<E>> setCheckedChip(chipGroup: ChipGroup, enum: Enum<E>) {
        val id = chipGroup.getChildAt(enum.ordinal).id
        if (chipGroup.checkedChipId != id)
            chipGroup.check(id)
    }

    @BindingAdapter("app:checkedChip")
    @JvmStatic
    fun <E : Enum<E>> setCheckedChip(chipGroup: ChipGroup, enums: Set<Enum<E>>) {
        chipGroup.forEachIndexed { idx, it ->
            val chip = it as Chip
            chip.isChecked = enums.any { it.ordinal == idx }
        }
    }
}