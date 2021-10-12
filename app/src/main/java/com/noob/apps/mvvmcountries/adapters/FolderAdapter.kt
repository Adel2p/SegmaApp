package com.noob.apps.mvvmcountries.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.FolderCellBinding
import com.noob.apps.mvvmcountries.models.Attachments
import kotlinx.android.extensions.LayoutContainer

class FolderAdapter(
    private val listener: RecyclerViewClickListener

) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    private var mList: List<Attachments>? = listOf()

    fun setData(list: List<Attachments>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: FolderCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.folder_cell,
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
        holder.itemBinding.container.setOnClickListener {
            listener.openFile(
                mList!![position].url
            )

        }
    }

    class ViewHolder(var itemBinding: FolderCellBinding) :
        RecyclerView.ViewHolder(itemBinding.root), LayoutContainer {
        override val containerView: View
            get() = itemBinding.root
    }
}