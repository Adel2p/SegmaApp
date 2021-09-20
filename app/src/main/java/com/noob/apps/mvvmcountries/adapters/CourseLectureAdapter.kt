package com.noob.apps.mvvmcountries.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.LecturesCourseCellBinding
import com.noob.apps.mvvmcountries.models.Lectures
import kotlinx.android.extensions.LayoutContainer

class CourseLectureAdapter(
    context: Context,
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<CourseLectureAdapter.ViewHolder>() {
    private var mList: List<Lectures>? = listOf()
    private var lastPosition = 0
    private var mContext: Context = context

    fun setData(list: List<Lectures>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: LecturesCourseCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.lectures_course_cell,
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
        val number = position + 1
        holder.itemBinding.container.setOnClickListener {
            listener.onRecyclerViewItemClick(
                position
            )
        }
        if (position == mList!!.size - 1)
            holder.itemBinding.sep.visibility = View.VISIBLE
        if (position == lastPosition) {
            holder.itemBinding.container.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.cell_bg_color
                )
            )

        }
        holder.itemBinding.number.text = number.toString() + "_"

    }

    class ViewHolder(var itemBinding: LecturesCourseCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View
            get() = itemBinding.root
    }
}