package com.ssttkkl.fgoplanningtool.ui.editplan.container

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_editplan_editlevel.*

class EditLevelDialogFragment : DialogFragment() {
    interface OnSaveListener {
        fun onSave(exp: Int, ascendedOnStage: Boolean, extraTag: String)
    }

    private var listener: OnSaveListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = when {
            parentFragment is OnSaveListener -> parentFragment as OnSaveListener
            activity is OnSaveListener -> activity as OnSaveListener
            else -> throw NoInterfaceImplException(OnSaveListener::class)
        }
    }

    var star: Int = 0
    var extraTag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        star = arguments?.getInt(KEY_STAR, 0) ?: 0
        extraTag = arguments?.getString(KEY_EXTRA_TAG, "") ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_editplan_editlevel, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        level_editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val level = level_editText.text.toString().toInt()
                    if (!ConstantValues.stageMapToMaxLevel[star].contains(level)) {
                        remainedExp_editText.setText(ConstantValues.nextLevelExp[level].toString())
                        remainedExp_editText.isEnabled = true
                        ascendedOnStage_checkBox.isChecked = false
                        ascendedOnStage_checkBox.isEnabled = false
                    } else {
                        if (!ascendedOnStage_checkBox.isChecked) {
                            remainedExp_editText.setText("0")
                            remainedExp_editText.isEnabled = false
                        } else {
                            remainedExp_editText.setText(ConstantValues.nextLevelExp[level].toString())
                            remainedExp_editText.isEnabled = true
                        }
                        ascendedOnStage_checkBox.isEnabled = true
                    }
                } catch (exc: Exception) {
                    Toast.makeText(context, exc.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
        ascendedOnStage_checkBox.setOnCheckedChangeListener { _, _ ->
            val level = level_editText.text.toString().toInt()
            if (!ascendedOnStage_checkBox.isChecked) {
                remainedExp_editText.setText("0")
                remainedExp_editText.isEnabled = false
            } else {
                remainedExp_editText.setText(ConstantValues.nextLevelExp[level].toString())
                remainedExp_editText.isEnabled = true
            }
        }

        val ascendedOnStage = arguments?.getBoolean(KEY_ASCENDED_ON_STAGE, false) ?: false
        val exp = arguments?.getInt(KEY_EXP, 0) ?: 0

        val level = ConstantValues.getLevel(exp)
        val remainedExp = if (!ConstantValues.stageMapToMaxLevel[star].contains(level) || ascendedOnStage)
            ConstantValues.getExp(level + 1) - exp
        else
            0

        level_editText.setText(level.toString())
        remainedExp_editText.setText(remainedExp.toString())
        ascendedOnStage_checkBox.isChecked = ascendedOnStage

        save_button.setOnClickListener {
            performOnText { level, remainedExp, ascendedOnStage ->
                var exp = ConstantValues.getExp(level)
                if (!ConstantValues.stageMapToMaxLevel[star].contains(level) || ascendedOnStage)
                    exp += ConstantValues.nextLevelExp[level] - remainedExp
                listener?.onSave(exp, ascendedOnStage, extraTag)
                dismiss()
            }
        }
        cancel_button.setOnClickListener { dialog.cancel() }
    }

    private fun performOnText(action: (level: Int, remainedExp: Int, ascendedOnStage: Boolean) -> Unit) {
        try {
            val level = level_editText.text.toString().toInt()
            val remainedExp = remainedExp_editText.text.toString().toInt()
            val ascendedOnStage = ascendedOnStage_checkBox.isChecked

            if (remainedExp > ConstantValues.nextLevelExp[level])
                throw Exception(context?.getString(R.string.illegalRemainedExp_editplan_editlevel))
            action.invoke(level, remainedExp, ascendedOnStage)
        } catch (exc: Exception) {
            Toast.makeText(context, exc.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(star: Int, exp: Int, ascendedOnStage: Boolean, extraTag: String = "") =
                EditLevelDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(KEY_STAR, star)
                        putInt(KEY_EXP, exp)
                        putBoolean(KEY_ASCENDED_ON_STAGE, ascendedOnStage)
                        putString(KEY_EXTRA_TAG, extraTag)
                    }
                }

        private const val KEY_STAR = "star"
        private const val KEY_EXP = "exp"
        private const val KEY_ASCENDED_ON_STAGE = "ascendedOnStage"
        private const val KEY_EXTRA_TAG = "extraTag"
    }
}