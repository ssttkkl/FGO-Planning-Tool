package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.RotateDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.item_servantinfo_levellist.view.*
import net.cachapa.expandablelayout.ExpandableLayout

class ServantInfoLevelListRecViewAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ServantInfoLevelListRecViewAdapter.ViewHolder>() {
    private var recyclerView: androidx.recyclerview.widget.RecyclerView? = null

    override fun onAttachedToRecyclerView(recView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recView)
        recyclerView = recView
    }

    override fun onDetachedFromRecyclerView(recView: androidx.recyclerview.widget.RecyclerView) {
        super.onDetachedFromRecyclerView(recView)
        recyclerView = null
    }

    var data: List<ServantInfoLevelListEntity> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    private var listener: ((codename: String) -> Unit)? = null

    fun setOnItemClickListener(newListener: ((codename: String) -> Unit)?) {
        listener = newListener
    }

    var expandedPosition = -1
        set(value) {
            if (field >= 0) {
                (recyclerView?.findViewHolderForAdapterPosition(field) as? ViewHolder)?.apply {
                    itemView.expLayout.collapse()
                    startCollapseAnimate()
                }
            }
            if (value >= 0) {
                (recyclerView?.findViewHolderForAdapterPosition(value) as? ViewHolder)?.apply {
                    itemView.expLayout.expand()
                    startExpandAnimate()
                }
                recyclerView?.smoothScrollToPosition(value)
            }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_servantinfo_levellist, parent, false)).apply {
                itemView.constLayout.setOnClickListener {
                    expandedPosition = if (expandedPosition != adapterPosition) adapterPosition else -1
                }
                itemView.recView.apply {
                    adapter = ServantInfoItemListRecViewAdapter(context).apply {
                        setOnItemClickListener { _, item -> listener?.invoke(item.codename) }
                    }
                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
                    addItemDecoration(CommonRecViewItemDecoration(context))
                }
                itemView.expLayout.setOnExpansionUpdateListener { _, state ->
                    itemView.recView.visibility = if (state == ExpandableLayout.State.COLLAPSED)
                        View.GONE
                    else
                        View.VISIBLE
                }
                itemView.arrow_imageView.drawable.mutate()
            }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            start_textView.text = item.start
            to_textView.text = item.to

            horizontalArrow_imageView.visibility = if (item.isHorizontalArrowVisible)
                View.VISIBLE
            else
                View.GONE

            (recView.adapter as ServantInfoItemListRecViewAdapter).data = item.items
            if (expandedPosition == pos) {
                expLayout.expand(false)
                (arrow_imageView.drawable as RotateDrawable).level = 10000
            } else {
                expLayout.collapse(false)
                (arrow_imageView.drawable as RotateDrawable).level = 0
            }
        }
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun startExpandAnimate() {
            (AnimatorInflater.loadAnimator(context, R.animator.rotate) as ValueAnimator).apply {
                addUpdateListener {
                    (itemView.arrow_imageView.drawable as RotateDrawable).level = animatedValue as Int
                }
                start()
            }
        }

        fun startCollapseAnimate() {
            (AnimatorInflater.loadAnimator(context, R.animator.rotate_reversed) as ValueAnimator).apply {
                addUpdateListener {
                    (itemView.arrow_imageView.drawable as RotateDrawable).level = animatedValue as Int
                }
                start()
            }
        }
    }
}