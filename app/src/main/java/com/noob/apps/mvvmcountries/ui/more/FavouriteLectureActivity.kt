package com.noob.apps.mvvmcountries.ui.more

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.FavouriteLectureAdapter
import com.noob.apps.mvvmcountries.adapters.LecturesAdapter
import com.noob.apps.mvvmcountries.databinding.ActivityFavouriteLectureBinding
import com.noob.apps.mvvmcountries.models.Lecture

class FavouriteLectureActivity : AppCompatActivity() {
    private lateinit var mAdapter: FavouriteLectureAdapter
    private val listOfLectures: MutableList<Lecture> = mutableListOf()

    private lateinit var mActivityBinding:ActivityFavouriteLectureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_favourite_lecture)
        val lec = Lecture("computer science", "DR mohamed adel", "2")
        val lec2 = Lecture("computer science", "DR mohamed adel", "2")
        val lec3 = Lecture("computer science", "DR mohamed adel", "2")
        val lec4 = Lecture("computer science", "DR mohamed adel", "2")
        val lec5 = Lecture("computer science", "DR mohamed adel", "2")
        val lec6 = Lecture("computer science", "DR mohamed adel", "2")
        val lec7 = Lecture("computer science", "DR mohamed adel", "2")

        listOfLectures.add(lec)
        listOfLectures.add(lec2)
        listOfLectures.add(lec3)
        listOfLectures.add(lec4)
        listOfLectures.add(lec5)
        listOfLectures.add(lec6)
        listOfLectures.add(lec7)
        initializeRecyclerView()
        mAdapter.setData(listOfLectures)
        mActivityBinding.back.setOnClickListener{
            finish()
        }



    }
    private fun initializeRecyclerView() {
        mAdapter = FavouriteLectureAdapter()
        mActivityBinding.rvFavLec.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@FavouriteLectureActivity, 2)

            adapter = mAdapter
        }
    }
}
