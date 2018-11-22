package com.ssttkkl.fgoplanningtool.ui.servantfilter.additem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantfilterAdditemItemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException

class ItemListFragment : Fragment() {
    interface OnItemClickListener {
        fun onItemClick(codename: String)
    }

    private var listener: OnItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = when {
            parentFragment is OnItemClickListener -> parentFragment as OnItemClickListener
            activity is OnItemClickListener -> activity as OnItemClickListener
            else -> throw NoInterfaceImplException(OnItemClickListener::class)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private lateinit var binding: FragmentServantfilterAdditemItemlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServantfilterAdditemItemlistBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[ItemListFragmentViewModel::class.java].apply {
            itemType.value = arguments?.getSerializable(ARG_TYPE) as ItemType
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recView.apply {
            adapter = ItemListRecViewAdapter(context!!, this@ItemListFragment, binding.viewModel!!)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
            setHasFixedSize(true)
        }
        binding.viewModel?.returnValueEvent?.observe(this, Observer {
            listener?.onItemClick(it ?: return@Observer)
        })
    }

    companion object {
        fun newInstance(type: ItemType) = ItemListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TYPE, type)
            }
        }

        private const val ARG_TYPE = "type"
    }
}