package com.ssttkkl.fgoplanningtool.ui.utils.databinding.edittext

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import kotlin.reflect.KFunction

object EditTextBindingAdapter {
    @BindingAdapter("onEditorAction")
    @JvmStatic
    fun onEditorAction(editText: EditText, function: KFunction<Unit>?) {
        editText.setOnEditorActionListener(if (function == null)
            null
        else
            TextView.OnEditorActionListener { v, actionId, event ->
                val imeAction = when (actionId) {
                    EditorInfo.IME_ACTION_DONE,
                    EditorInfo.IME_ACTION_SEND,
                    EditorInfo.IME_ACTION_GO -> true
                    else -> false
                }

                val keydownEvent = event?.keyCode == KeyEvent.KEYCODE_ENTER
                        && event.action == KeyEvent.ACTION_DOWN

                if (imeAction or keydownEvent)
                    true.also { function.call(v?.tag) }
                else false
            })
    }
}