package com.noob.apps.mvvmcountries.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.NotificationCellBinding
import com.noob.apps.mvvmcountries.models.Notification
import kotlinx.android.extensions.LayoutContainer

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var mList: List<Notification>? = listOf()

    fun setData(list: List<Notification>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: NotificationCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.notification_cell,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.notification = mList!![position]
    }

    class ViewHolder(var itemBinding: NotificationCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View
            get() = itemBinding.root
    }

}