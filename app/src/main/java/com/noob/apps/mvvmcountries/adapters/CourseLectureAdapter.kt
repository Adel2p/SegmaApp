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
import com.noob.apps.mvvmcountries.models.LectureDetails
import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.utils.AESUtils
import kotlinx.android.extensions.LayoutContainer
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CourseLectureAdapter(user: User,
    context: Context,
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<CourseLectureAdapter.ViewHolder>() {
    private var mList: List<LectureDetails>? = listOf()
    private var lastPosition = -1
    private var mContext: Context = context
    private var mUser: User = user

    fun setData(list: List<LectureDetails>) {
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
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val x = AESUtils.decrypt(
            mList!![position].resolutions,
            mUser.user_mobile_number,
            mUser.user_email,
            mUser.user_uuid, currentDate
        )
        jsonObject = JSONObject(x)
        try {
            val duration = jsonObject.getString("duration")
            val minutes: Long = (duration.toLong() / 60)
            holder.itemBinding.duration.text =
                minutes.toString() + " " + mContext.resources.getString(R.string.mintes)
        } catch (e: Exception) {
            e.message
        }


    }

    class ViewHolder(var itemBinding: LecturesCourseCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View
            get() = itemBinding.root
    }
}