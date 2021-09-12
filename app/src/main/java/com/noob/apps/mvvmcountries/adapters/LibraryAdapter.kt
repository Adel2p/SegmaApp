package com.noob.apps.mvvmcountries.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.LibraryCellBinding
import com.noob.apps.mvvmcountries.models.Library
import kotlinx.android.extensions.LayoutContainer

class LibraryAdapter: RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    private var mList: List<Library>? = listOf()

    fun setData(list: List<Library>) {
        mList = list
        notifyItemRangeChanged(0, mList!!.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: LibraryCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.library_cell,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.library = mList!![position]
    }

    class ViewHolder(var itemBinding: LibraryCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View?
            get() = itemBinding.root
    }

}
