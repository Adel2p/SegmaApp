package com.noob.apps.mvvmcountries.adapters


interface RecyclerViewClickListener {

    fun onRecyclerViewItemClick(position: Int)

    fun onQualitySelected(position: Int)
}