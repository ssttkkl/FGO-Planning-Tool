package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditplanEditlevelBinding
import com.ssttkkl.fgoplanningtool.resources.LevelValues
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException

class EditLevelDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog)
        isCancelable = true
    }

    interface OnSaveListener {
        fun onSave(exp: Int, ascendedOnStage: Boolean, extraTag: String?)
    }

    private var listener: OnSaveListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            parentFragment is OnSaveListener -> parentFragment as OnSaveListener
            activity is OnSaveListener -> activity as OnSaveListener
            else -> throw NoInterfaceImplException(OnSaveListener::class)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private lateinit var binding: FragmentEditplanEditlevelBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditplanEditlevelBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[EditLevelDialogFragmentViewModel::class.java].apply {
            servantID.value = arguments?.getInt(KEY_SERVANT_ID)
            extraTag = arguments?.getString(KEY_EXTRA_TAG)
            ascendedOnStage.value = arguments?.getBoolean(KEY_ASCENDED_ON_STAGE, false) ?: false

            val exp = arguments?.getInt(KEY_EXP, 0) ?: 0
            level.value = LevelValues.expToLevel(exp)
            remainedExp.value = LevelValues.expToCurLevelRemainingExp(servant.value?.star!!, exp, ascendedOnStage.value == true)
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel?.apply {
            saveEvent.observe(this@EditLevelDialogFragment, Observer {
                if (it != null)
                    save(it.first, it.second, it.third)
            })
            messageEvent.observe(this@EditLevelDialogFragment, Observer {
                showMessage(it ?: return@Observer)
            })
            cancelEvent.observe(this@EditLevelDialogFragment, Observer {
                dialog?.cancel()
            })
        }
    }

    private fun save(exp: Int, ascendedOnStage: Boolean, extraTag: String?) {
        listener?.onSave(exp, ascendedOnStage, extraTag)
        dismiss()
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(servantID: Int, exp: Int, ascendedOnStage: Boolean, extraTag: String? = null) =
                EditLevelDialogFragment().apply {
                    arguments = bundleOf(KEY_SERVANT_ID to servantID,
                            KEY_EXP to exp,
                            KEY_ASCENDED_ON_STAGE to ascendedOnStage,
                            KEY_EXTRA_TAG to extraTag)
                }

        private const val KEY_SERVANT_ID = "servantID"
        private const val KEY_EXP = "exp"
        private const val KEY_ASCENDED_ON_STAGE = "ascendedOnStage"
        private const val KEY_EXTRA_TAG = "extraTag"
    }
}