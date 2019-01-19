package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ssttkkl.fgoplanningtool.utils.Localizable

object ChipGroupBindingAdapter {
    private fun ChipGroup.setEntities(entities: List<Localizable>,
                                      checkedChipAttrChanged: InverseBindingListener?) {
        var notDirty = childCount == entities.size
        forEachIndexed { idx, v ->
            notDirty = notDirty && (v as? Chip)?.text == entities[idx].localizedName
        }
        if (notDirty)
            return

        TransitionManager.beginDelayedTransition(this)
        removeAllViews()
        entities.forEach { entity ->
            val chip = Chip(context).apply {
                text = entity.localizedName
                isCheckable = true
                isClickable = true
                transitionName = entity.toString()
            }
            addView(chip)
        }
        TransitionManager.endTransitions(this)

        // chipGroup only call its listener when it is single selection
        if (isSingleSelection)
            setOnCheckedChangeListener { _, _ ->
                checkedChipAttrChanged?.onChange()
            }
        else {
            setOnCheckedChangeListener(null)
            forEach {
                (it as Chip).setOnCheckedChangeListener { _, _ ->
                    checkedChipAttrChanged?.onChange()
                }
            }
        }
    }

    @BindingAdapter(value = ["checkableEntities", "app:checkedChip", "checkedChipAttrChanged"], requireAll = false)
    @JvmStatic
    fun <E : Enum<E>> ChipGroup.setEntities(entities: List<Localizable>?,
                                            checked: Enum<E>?,
                                            checkedChipAttrChanged: InverseBindingListener?) {
        setEntities(entities ?: listOf(), checkedChipAttrChanged)

        val id = getChildAt(checked?.ordinal ?: -1)?.id ?: -1
        if (checkedChipId != id)
            check(id)
    }

    @BindingAdapter(value = ["checkableEntities", "app:checkedChip", "checkedChipAttrChanged"], requireAll = false)
    @JvmStatic
    fun <E : Enum<E>> ChipGroup.setEntities(entities: List<Localizable>?,
                                            checked: Set<Enum<E>>?,
                                            checkedChipAttrChanged: InverseBindingListener?) {
        setEntities(entities ?: listOf(), checkedChipAttrChanged)

        forEachIndexed { idx, it ->
            val chip = it as Chip
            chip.isChecked = checked?.any { it.ordinal == idx } == true
        }
    }
}