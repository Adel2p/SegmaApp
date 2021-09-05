package com.noob.apps.mvvmcountries.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.CountriesListItemBinding
import com.noob.apps.mvvmcountries.databinding.LectureItemCellBinding
import com.noob.apps.mvvmcountries.models.Country
import com.noob.apps.mvvmcountries.models.Lecture
import kotlinx.android.extensions.LayoutContainer

class LecturesAdapter : RecyclerView.Adapter<LecturesAdapter.ViewHolder>() {

    private var mList: List<Lecture>? = listOf()

    fun setData(list: List<Lecture>) {
        mList = list
        notifyItemRangeChanged(0, mList!!.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: LectureItemCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.lecture_item_cell,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.lecture = mList!![position]
    }

    class ViewHolder(var itemBinding: LectureItemCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View?
            get() = itemBinding.root
    }

}