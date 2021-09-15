package com.noob.apps.mvvmcountries.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityCourseDetailsBinding
import com.noob.apps.mvvmcountries.models.Course
import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.utils.Constant

class CourseDetailsActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityCourseDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        val i = intent
        val course: Course = i.getSerializableExtra(Constant.SELECTED_COURSE) as Course
        mActivityBinding.txtLecId.text = course.name
        mActivityBinding.txtLecNum.text = course.lectures.size.toString()
        mActivityBinding.txtLectures.setTextColor(
            resources.getColor(R.color.blue)
        )
        mActivityBinding.txtLectures.setOnClickListener {
            mActivityBinding.txtLectures.setTextColor(
                resources.getColor(R.color.blue)
            )
            mActivityBinding.txtInfo.setTextColor(
                resources.getColor(R.color.gray_purple)
            )
            mActivityBinding.lectureLay.visibility = View.VISIBLE
            mActivityBinding.infoLay.visibility = View.GONE


        }
        mActivityBinding.txtInfo.setOnClickListener {
            mActivityBinding.txtLectures.setTextColor(
                resources.getColor(R.color.gray_purple)
            )
            mActivityBinding.txtInfo.setTextColor(
                resources.getColor(R.color.blue)
            )
            mActivityBinding.lectureLay.visibility = View.GONE
            mActivityBinding.infoLay.visibility = View.VISIBLE


        }
        lifecycle.addObserver(mActivityBinding.lecVv)
        mActivityBinding.lecVv.initialize(603909245, course.introUrl)
        //mActivityBinding.lecVv.initialize(true, {YourPrivateVideoId},"VideoHashKey", "SettingsEmbeddedUrl")


    }
}