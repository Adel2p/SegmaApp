package com.noob.apps.mvvmcountries.ui.details

import android.os.Bundle
import android.os.CountDownTimer
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
import com.noob.apps.mvvmcountries.models.LectureDetailsResponse
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.dialog.LectureWatchDialog
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.CourseViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

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
    private var userId = ""
    private var token = ""
    private var selectedLectureId = ""
    private var startTime = ""
    private var endTime = ""
    private var sessionTimeout = 0
    private var countDownTimer: CountDownTimer? = null

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

    }

    private fun createMediaItem(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player!!.setMediaItem(mediaItem)
    }


    override fun onRecyclerViewItemClick(position: Int) {
        resolutions.clear()
        if (!eligibleToWatch) {
            course.lectures?.get(position)?.let { initLectureInfo(it.uuid) }
        } else
            Toast.makeText(this, "pay first", Toast.LENGTH_LONG).show()


    }

    fun onStartWatchClicked() {
        startTime = getStartDate()
        endTime = getStartDate(sessionTimeout)
        initAddSession(selectedLectureId)
    }

    private fun initLectureInfo(lecId: String) {
        selectedLectureId = lecId
        courseViewModel.getLectureInfo(token, lecId)
        courseViewModel.lectureResponse.observeOnce(this, { kt ->
            if (kt != null) {
                getResolutions(kt)
                sessionTimeout = lectureResponse.sessionTimeout
                checkVideoSession()
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

    private fun checkVideoSession() {
        if (lectureResponse.studentSessions.isEmpty())
            LectureWatchDialog.newInstance(lectureResponse)
                .show(
                    supportFragmentManager,
                    LectureWatchDialog.TAG
                )
        else if (!lectureResponse.studentSessions[0].expired) {
            releasePlayer()
            initializePlayer()
            clearTimer()
            startTime = getStartDate()
            endTime = lectureResponse.studentSessions[0].expiredAt
            printDifferenceDateForHours(startTime, endTime)
            createMediaItem(resolutions[0].link)
        } else {
            Toast.makeText(this, "session end", Toast.LENGTH_LONG).show()
        }
    }

    private fun getResolutions(kt: LectureDetailsResponse) {
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
    }

    private fun initAddSession(lecId: String) {
        courseViewModel.addSession(token, lecId)
        courseViewModel.sessionResponse.observeOnce(this, { kt ->
            if (kt != null) {
                initializePlayer()
                printDifferenceDateForHours(startTime, endTime)
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

    private fun getStartDate(hours: Int): String {
        val c = Calendar.getInstance(Locale.ENGLISH).time
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.getDefault())
        val formattedDate: String = df.format(c)
        val d = df.parse(formattedDate)
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.time = d
        cal.add(Calendar.HOUR_OF_DAY, hours)
        return df.format(cal.time)
    }


    private fun getStartDate(): String {
        val c = Calendar.getInstance(Locale.ENGLISH).time
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate: String = df.format(c)
        val d = df.parse(formattedDate)
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.time = d
        return df.format(cal.time)
    }

    private fun printDifferenceDateForHours(strTime: String?, endTime: String?) {
        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = format1.parse(strTime)
        val endDate = format1.parse(endTime)
        var different = endDate.time - currentTime.time
        countDownTimer = object : CountDownTimer(different, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                minutesInMilli * 60

            }

            override fun onFinish() {
                releasePlayer()
                Toast.makeText(this@CourseDetailsActivity, "session end", Toast.LENGTH_LONG).show()

            }
        }.start()
    }

    private fun clearTimer() {
        countDownTimer?.cancel()
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
}