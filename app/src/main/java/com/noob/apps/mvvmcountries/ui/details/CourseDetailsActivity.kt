package com.noob.apps.mvvmcountries.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.CourseAdapter
import com.noob.apps.mvvmcountries.adapters.CourseLectureAdapter
import com.noob.apps.mvvmcountries.adapters.RecyclerViewClickListener
import com.noob.apps.mvvmcountries.databinding.ActivityCourseDetailsBinding
import com.noob.apps.mvvmcountries.models.Course
import com.noob.apps.mvvmcountries.utils.Constant


class CourseDetailsActivity : AppCompatActivity(), RecyclerViewClickListener {
    private lateinit var mActivityBinding: ActivityCourseDetailsBinding
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = false
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var mAdapter: CourseLectureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        val i = intent
        val course: Course = i.getSerializableExtra(Constant.SELECTED_COURSE) as Course
        mActivityBinding.txtLecId.text = course.name
        mActivityBinding.txtLecNum.text = course.lectures.size.toString()
        mActivityBinding.txtLectures.setTextColor(
            ContextCompat.getColor(this, R.color.blue)
        )
        mActivityBinding.txtLectures.setOnClickListener {
            mActivityBinding.txtLectures.setTextColor(
                ContextCompat.getColor(this, R.color.blue)
            )
            mActivityBinding.txtInfo.setTextColor(
                ContextCompat.getColor(this, R.color.gray_purple)
            )
            mActivityBinding.lectureLay.visibility = View.VISIBLE
            mActivityBinding.infoLay.visibility = View.GONE


        }
        mActivityBinding.txtInfo.setOnClickListener {
            mActivityBinding.txtLectures.setTextColor(
                ContextCompat.getColor(this, R.color.gray_purple)
            )
            mActivityBinding.txtInfo.setTextColor(
                ContextCompat.getColor(this, R.color.blue)
            )
            mActivityBinding.lectureLay.visibility = View.GONE
            mActivityBinding.infoLay.visibility = View.VISIBLE


        }
        initializeRecyclerView()
        mAdapter.setData(course.lectures)
        // player!!.playWhenReady = true
    }

    private fun initializeRecyclerView() {
        mAdapter = CourseLectureAdapter(this, this)
        mActivityBinding.lectureRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@CourseDetailsActivity, 1)
            adapter = mAdapter
        }
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        mActivityBinding.lecVv.player = player
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare()
        createMediaItem("https://player.vimeo.com/external/598764719.hd.mp4?s=bf05bc1ed2dac6ecb9625f837e95dac531b2a6b3&profile_id=174&oauth2_token_id=1538788482")

    }

    private fun createMediaItem(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player!!.setMediaItem(mediaItem)
    }

    private fun hideSystemUi() {
        mActivityBinding.lecVv.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }


    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }

    override fun onRecyclerViewItemClick(position: Int) {
    }
}