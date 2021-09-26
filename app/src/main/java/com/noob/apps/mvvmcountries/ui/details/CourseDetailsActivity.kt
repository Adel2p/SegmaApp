package com.noob.apps.mvvmcountries.ui.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.CourseLectureAdapter
import com.noob.apps.mvvmcountries.adapters.RecyclerViewClickListener
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.databinding.ActivityCourseDetailsBinding
import com.noob.apps.mvvmcountries.models.Course
import com.noob.apps.mvvmcountries.models.Files
import com.noob.apps.mvvmcountries.models.LectureDetails
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.dialog.LectureWatchDialog
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.CourseViewModel
import com.noob.apps.mvvmcountries.viewmodels.SharedViewModel
import org.json.JSONArray
import org.json.JSONObject

class CourseDetailsActivity : BaseActivity(), RecyclerViewClickListener {
    private lateinit var mActivityBinding: ActivityCourseDetailsBinding
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = false
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var mAdapter: CourseLectureAdapter
    private lateinit var course: Course
    private var eligibleToWatch = false
    private val resolutions: MutableList<Files> = mutableListOf()
    private lateinit var lectureResponse: LectureDetails
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var userId = ""
    private var token = ""
    private var selectedLectureId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        courseViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(CourseViewModel::class.java)
        val i = intent
        course = i.getSerializableExtra(Constant.SELECTED_COURSE) as Course
        eligibleToWatch = i.getBooleanExtra(Constant.ELIGIBLE_TO_WATCH, false)
        mActivityBinding.txtLecId.text = course.name
        mActivityBinding.txtLecNum.text = course.lectures!!.size.toString()
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
        mAdapter.setData(course.lectures!!)
        userPreferences.getUserId.asLiveData().observeOnce(this, {
            if (it != null) {
                userId = it
                courseViewModel.findUser(userId)
                    .observeOnce(this, { result ->
                        if (result != null && result.size > 0) {
                            token = "Bearer " + result[0].access_token.toString()
                        }
                    })
            }
        })
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.isStarted.observe(this, {
            if (it) {
                initAddSession(selectedLectureId)
            }

        })

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
        createMediaItem(course.introUrl)

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
        resolutions.clear()
        if (!eligibleToWatch) {
            course.lectures?.get(position)?.let { initLectureInfo(it?.uuid) }
        } else
            Toast.makeText(this, "pay first", Toast.LENGTH_LONG).show()


    }

    private fun initLectureInfo(lecId: String) {
        selectedLectureId = lecId
        courseViewModel.getLectureInfo(token, lecId)
        courseViewModel.lectureResponse.observeOnce(this, { kt ->
            if (kt != null) {
                resolutions.clear()
                lectureResponse = kt.data
                val jsonObject: JSONObject?
                jsonObject = JSONObject(kt.data.resolutions)
                jsonObject.getString("duration")
                val files: JSONArray = jsonObject.getJSONArray("files")
                for (i in 0 until files.length()) {
                    val winspeed = files.getString(i)
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(winspeed)
                    resolutions.add(
                        i,
                        Files(jsonObject.getString("quality"), jsonObject.getString("link"))
                    )
                }
                if (lectureResponse.studentSessions.isEmpty())
                    LectureWatchDialog.newInstance(lectureResponse)
                        .show(
                            supportFragmentManager,
                            LectureWatchDialog.TAG
                        )
                else
                    createMediaItem(resolutions[0].link)
            }
        })
        courseViewModel.mShowResponseError.observeOnce(this, {
        })
        courseViewModel.mShowProgressBar.observe(this, { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }

        })
        courseViewModel.mShowNetworkError.observeOnce(this, {
            if (it != null) {
                ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                    .show(
                        supportFragmentManager,
                        ConnectionDialogFragment.TAG
                    )
            }

        })
    }

    private fun initAddSession(lecId: String) {
        courseViewModel.addSession(token, lecId)
        courseViewModel.sessionResponse.observeOnce(this, { kt ->
            if (kt != null) {
                createMediaItem(resolutions[0].link)
            }
        })
        courseViewModel.mShowResponseError.observeOnce(this, {
        })
        courseViewModel.mShowProgressBar.observe(this, { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }

        })
        courseViewModel.mShowNetworkError.observeOnce(this, {
            if (it != null) {
                ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                    .show(
                        supportFragmentManager,
                        ConnectionDialogFragment.TAG
                    )
            }

        })
    }

}