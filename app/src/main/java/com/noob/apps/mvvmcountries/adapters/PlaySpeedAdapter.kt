package com.noob.apps.mvvmcountries.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.SpeedItemCellBinding
import com.noob.apps.mvvmcountries.ui.details.CourseDetailsActivity
import kotlinx.android.extensions.LayoutContainer

class PlaySpeedAdapter(
    context: Context,
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<PlaySpeedAdapter.ViewHolder>() {
    private var mList: List<String> = listOf()
    private var mContext: Context = context
    fun setData(list: List<String>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: SpeedItemCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.speed_item_cell,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.txtLecName.text = mList.get(position)
        holder.itemBinding.container.setOnClickListener {
            CourseDetailsActivity.lastSpeedPosition = position
            listener.onQualitySelected(
                CourseDetailsActivity.lastSpeedPosition
            )
        }
        if (CourseDetailsActivity.lastSpeedPosition == position) {
            holder.itemBinding.checkImage.background =
                AppCompatResources.getDrawable(mContext, R.drawable.ic_blue_circle)
        } else {
            holder.itemBinding.checkImage.background =
                AppCompatResources.getDrawable(mContext, R.drawable.ic_gray_circle)
        }

    }

    class ViewHolder(var itemBinding: SpeedItemCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View
            get() = itemBinding.root
    }
}