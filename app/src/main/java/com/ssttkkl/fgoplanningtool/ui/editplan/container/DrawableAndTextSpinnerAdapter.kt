package com.ssttkkl.fgoplanningtool.ui.editplan.container

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ssttkkl.fgoplanningtool.R
import kotlinx.android.synthetic.main.item_editplan_spinner.view.*

class DrawableAndTextSpinnerAdapter(private val context: Context?,
                                    private val arr: Array<String>,
                                    private val drawableResId: Int,
                                    private val showDrawableFilter: (Int) -> Boolean) : BaseAdapter() {
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.item_editplan_spinner, parent, false)
        if (showDrawableFilter.invoke(pos)) {
            view.imageView.visibility = View.VISIBLE
            view.imageView.setImageResource(drawableResId)
        } else {
            view.imageView.visibility = View.GONE
        }
        view.textView.text = arr[pos]
        return view
    }

    override fun getDropDownView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.item_editplan_spinner_dropdown, parent, false)
        if (showDrawableFilter.invoke(pos)) {
            view.imageView.visibility = View.VISIBLE
            view.imageView.setImageResource(drawableResId)
        } else {
            view.imageView.visibility = View.GONE
        }
        view.textView.text = arr[pos]
        return view
    }

    override fun getItem(pos: Int) = arr[pos]
    override fun getItemId(pos: Int): Long = pos.toLong()
    override fun getCount() = arr.size
}