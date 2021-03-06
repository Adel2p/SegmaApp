package com.noob.apps.mvvmcountries.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.LecturesCourseCellBinding
import com.noob.apps.mvvmcountries.models.Lectures
import kotlinx.android.extensions.LayoutContainer
import org.json.JSONObject

class CourseLectureAdapter(
    context: Context,
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<CourseLectureAdapter.ViewHolder>() {
    private var mList: List<Lectures>? = listOf()
    private var lastPosition = -1
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.itemBinding.lecture = mList!![position]
        val number = position + 1
        holder.itemBinding.container.setOnClickListener {
            lastPosition = position
            notifyDataSetChanged()
            listener.onRecyclerViewItemClick(
                position
            )
        }
        if (position == mList!!.size - 1)
            holder.itemBinding.sep.visibility = View.INVISIBLE
        if (position == lastPosition) {
            holder.itemBinding.container.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.cell_bg_color
                )
            )

            holder.itemBinding.number.typeface = (ResourcesCompat.getFont(mContext, R.font.bold))
            holder.itemBinding.name.typeface = (ResourcesCompat.getFont(mContext, R.font.bold))
            holder.itemBinding.play.visibility = View.VISIBLE


        } else {
            holder.itemBinding.play.visibility = View.INVISIBLE
            holder.itemBinding.number.typeface = (ResourcesCompat.getFont(mContext, R.font.regular))
            holder.itemBinding.name.typeface = (ResourcesCompat.getFont(mContext, R.font.regular))
            holder.itemBinding.container.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )

        }
        //   holder.itemBinding.number.text = number.toString() + "_"
        //  holder.itemBinding.number.visibility = View.INVISIBLE
        val jsonObject: JSONObject?
        jsonObject = JSONObject(mList!![position].resolutions)
        val duration = jsonObject.getString("duration")
        val minutes: Long = (duration.toLong() / 60)
        holder.itemBinding.duration.text =
            minutes.toString() + " " + mContext.resources.getString(R.string.mintes)


    }

    class ViewHolder(var itemBinding: LecturesCourseCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View
            get() = itemBinding.root
    }
}