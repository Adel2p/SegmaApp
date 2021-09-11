package com.noob.apps.mvvmcountries.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityCourseDetailsBinding

class CourseDetailsActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityCourseDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        mActivityBinding.txtLectures.setTextColor(
            resources.getColor(R.color.blue)
        )
        mActivityBinding.txtLectures.setOnClickListener{
            mActivityBinding.txtLectures.setTextColor(
                resources.getColor(R.color.blue)
            )
            mActivityBinding.txtInfo.setTextColor(
                resources.getColor(R.color.gray_purple)
            )
            mActivityBinding.lectureLay.visibility=View.VISIBLE
            mActivityBinding.infoLay.visibility=View.GONE


        }
        mActivityBinding.txtInfo.setOnClickListener{
            mActivityBinding.txtLectures.setTextColor(
                resources.getColor(R.color.gray_purple)
            )
            mActivityBinding.txtInfo.setTextColor(
                resources.getColor(R.color.blue)
            )
            mActivityBinding.lectureLay.visibility=View.GONE
            mActivityBinding.infoLay.visibility=View.VISIBLE


        }
    }
}