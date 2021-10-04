package com.noob.apps.mvvmcountries.ui.details

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.*
import com.google.android.exoplayer2.util.Util
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.*
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.databinding.ActivityCourseDetailsBinding
import com.noob.apps.mvvmcountries.databinding.CallDialogBinding
import com.noob.apps.mvvmcountries.databinding.InvalidWatchDialogBinding
import com.noob.apps.mvvmcountries.models.*
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.dialog.LectureWatchDialog
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.CourseViewModel
import kotlinx.android.synthetic.main.activity_visitor.view.*
import kotlinx.android.synthetic.main.call_dialog.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class CourseDetailsActivity : BaseActivity(), RecyclerViewClickListener,
    PlayerControlView.VisibilityListener {
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
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var link = ""
    private var isFullScreen = false
    private lateinit var qualityAdapter: QualityAdapter
    private val playSpeeds: MutableList<String> =
        mutableListOf("0.75", "1", "1.25", "1.5", "1.75", "2")
    private var duration: Int = 0
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var user: User
    private var lastLecId = ""
    private var lastDuration: Long = 0
    private var isBack = false
    private var selectedPosition = 0

    companion object {
        var lastQualityPosition = 0
        var lastSpeedPosition = 0
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        //  mActivityBinding.aspect.setAspectRatio(16f / 9f)
        initViewModel()
        readValues()
        initView()
        initializeRecyclerView()
        mAdapter.setData(course.lectures!!)
        userPreferences.getUserId.asLiveData().observeOnce(this, {
            if (it != null) {
                userId = it
                courseViewModel.findUser(userId)
                    .observeOnce(this, { result ->
                        if (result != null && result.size > 0) {
                            user = result[0]
                            token = "Bearer " + result[0].access_token.toString()
                            mActivityBinding.mobileNumber.text = user.user_mobile_number
                        }
                    })
            }

        })
        initPlayerView()

    }

    @SuppressLint("CutPasteId")
    private fun initPlayerView() {
        val builder = DefaultTrackSelector.ParametersBuilder( /* context= */this)
        trackSelectorParameters = builder.build()
        mActivityBinding.playerView.setControllerVisibilityListener(this)
        mActivityBinding.playerView.requestFocus()

        val fullScreenButton: AppCompatImageView =
            mActivityBinding.playerView.findViewById(R.id.exo_fullscreen_icon)
        val normalScreenButton: AppCompatImageView =
            mActivityBinding.playerView.findViewById(R.id.exo_fullscreen_icon)
        fullScreenButton.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_fullscreen_open
            )
        )
        normalScreenButton.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_fullscreen_close
            )
        )
        fullScreenButton.setOnClickListener {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            supportActionBar?.show()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200 * resources.displayMetrics.density.toInt()
            )
            mActivityBinding.playerView.layoutParams = layoutParams
        }
        normalScreenButton.setOnClickListener {
            if (!isFullScreen) {
                isFullScreen = true
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                supportActionBar?.hide()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                mActivityBinding.playerView.layoutParams = layoutParams
                //  mActivityBinding.continueButton.visibility = View.INVISIBLE
            } else {
                isFullScreen = false
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                supportActionBar?.show()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    220 * resources.displayMetrics.density.toInt()
                )
                mActivityBinding.playerView.layoutParams = layoutParams
                //  mActivityBinding.continueButton.visibility = View.VISIBLE

            }
        }
        mActivityBinding.btnBack.setOnClickListener {
            finish()
        }
        mActivityBinding.btnSetting.setOnClickListener {
            mActivityBinding.qualityCard.visibility = View.VISIBLE
        }
        initializeQualityAdapter()
    }

    private fun initView() {
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
        mActivityBinding.txtGroup.setOnClickListener {
            callWinnerDialog()
        }
    }

    private fun readValues() {
        val i = intent
        course = i.getSerializableExtra(Constant.SELECTED_COURSE) as Course
        eligibleToWatch = i.getBooleanExtra(Constant.ELIGIBLE_TO_WATCH, false)
        mActivityBinding.txtLecId.text = course.name
        mActivityBinding.txtLecNum.text = course.lectures!!.size.toString()
        mActivityBinding.price.text = course.price.toString() + " " + getString(R.string.pound)
    }

    private fun initViewModel() {
        courseViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(CourseViewModel::class.java)
        roomViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(RoomViewModel::class.java)
    }

    private fun initializeRecyclerView() {
        mAdapter = CourseLectureAdapter(this, this)
        mActivityBinding.lectureRv.apply {
            setHasFixedSize(true)
            mActivityBinding.lectureRv.isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(this@CourseDetailsActivity, 1)
            adapter = mAdapter
        }
    }

    private fun initializePlayer() {
        trackSelector = DefaultTrackSelector( /* context= */this)
        trackSelector!!.parameters = trackSelectorParameters!!
        player = SimpleExoPlayer.Builder(this).build()
        mActivityBinding.playerView.player = player
        player!!.playWhenReady = true
        player!!.seekTo(currentWindow, playbackPosition)

    }

    private fun createMediaItem(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        if (resolutions.isEmpty()) {
            mActivityBinding.btnSetting.visibility = View.INVISIBLE
            mActivityBinding.mobileNumber.visibility = View.INVISIBLE
        } else {
            mActivityBinding.btnSetting.visibility = View.VISIBLE
            startAnimation()
        }

    }

    private fun startAnimation() {
        mActivityBinding.mobileNumber.visibility = View.VISIBLE
        mActivityBinding.mobileNumber.clearAnimation()
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val R = Random()
                    val dx = R.nextFloat() * displaymetrics.widthPixels
                    val dy = R.nextFloat() * 650
                    val timer = Timer()
                    mActivityBinding.mobileNumber.animate()
                        .x(dx)
                        .y(dy)
                        .setDuration(0)
                        .start()
                }
            }
        }, 0, 6000)
//        val animation = TranslateAnimation(
//            0.0f,
//            1500.0f,
//            0.0f,
//            0.0f
//        ) // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)
//
//
//        animation.duration = 30000
//
//        animation.repeatCount = duration / 30
//        animation.repeatMode
//
//        animation.fillAfter = false
//        mActivityBinding.mobileNumber.startAnimation(animation)
    }


    override fun onRecyclerViewItemClick(position: Int) {
        lastQualityPosition = 0
        selectedPosition = position
        resolutions.clear()
        lastDuration = (player!!.currentPosition / 1000) % 60
        val minutes = (player!!.currentPosition / (1000 * 60) % 60)
        val hours = (player!!.currentPosition / (1000 * 60 * 60) % 24)
        val total = hours * 60 * 60 + minutes * 60
        if (lastLecId.isEmpty()) {
            lastLecId = course.lectures?.get(position)?.uuid.toString()
        } else {
            val progress = (duration * 10) / 100
            if (total >= progress) {
                if (course.lectures?.get(position)?.studentSessions!!.isEmpty()
                )
                    initAddSession(lastLecId)
            }
        }
        if (!eligibleToWatch) {
            course.lectures?.get(position)?.let { initLectureInfo(it.uuid) }
        } else
            invalidWatchDialog(getString(R.string.pay_first))


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onQualitySelected(position: Int) {
        lastQualityPosition = position
        qualityAdapter.notifyDataSetChanged()
        mActivityBinding.qualityCard.visibility = View.INVISIBLE
        link = resolutions[position].link
        val time = player!!.currentPosition
        createMediaItem(link)
        player!!.seekTo(0, time)
    }

    fun onStartWatchClicked() {
        startTime = getStartDate()
        endTime = getStartDate(sessionTimeout)
        initializePlayer()
        printDifferenceDateForHours(startTime, endTime)
        createMediaItem(resolutions[0].link)
        //  initAddSession(selectedLectureId)
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
        qualityAdapter.setData(resolutions)
        if (lectureResponse.studentSessions.isEmpty())
            LectureWatchDialog.newInstance(lectureResponse)
                .show(
                    supportFragmentManager,
                    LectureWatchDialog.TAG
                )
        else if (
            lectureResponse.actualSessions < lectureResponse.allowedSessions
        ) {
            releasePlayer()
            initializePlayer()
            clearTimer()
            startTime = getStartDate()
            endTime = lectureResponse.studentSessions[0].expiredAt
            printDifferenceDateForHours(startTime, endTime)
            createMediaItem(resolutions[0].link)
        } else {
            //   Toast.makeText(this, "You exceed number of watches", Toast.LENGTH_LONG).show()
            invalidWatchDialog(getString(R.string.excced_watch))

        }
    }

    private fun getResolutions(kt: LectureDetailsResponse) {
        resolutions.clear()
        lectureResponse = kt.data
        val jsonObject: JSONObject?
        jsonObject = JSONObject(kt.data.resolutions)
        duration = jsonObject.getString("duration").toInt()
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
                if (isBack)
                    finish()
                //       initializePlayer()
                //     printDifferenceDateForHours(startTime, endTime)
                //      createMediaItem(resolutions[0].link)
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
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
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

    private fun printDifferenceDateForHours(strTime: String, endTime: String) {
        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = format1.parse(strTime)
        val endDate = format1.parse(endTime)
        val different = endDate.time - currentTime.time
        countDownTimer = object : CountDownTimer(different, 1000) {
            override fun onTick(millisUntilFinished: Long) {
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

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        mActivityBinding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
            createMediaItem(course.introUrl)
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

    private fun callWinnerDialog() {
        var firstNumber = ""
        var secondNumber = ""
        if (!course.firstPhone.isNullOrEmpty())
            firstNumber = course.firstPhone
        else if (!course.secondPhone.isNullOrEmpty())
            secondNumber = course.secondPhone
        lateinit var dialog: AlertDialog
        val inflater = LayoutInflater.from(this)
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            this,
            R.style.CustomDialog
        )
        val customLayout: CallDialogBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.call_dialog,
            null,
            false
        )
        builder.setCancelable(true)
        builder.setView(customLayout.root)
        dialog = builder.create()
        if (firstNumber.isEmpty())
            customLayout.firstNumber.visibility = View.GONE
        if (secondNumber.isEmpty())
            customLayout.secondNumber.visibility = View.GONE
        customLayout.firstNumber.text = firstNumber
        customLayout.secondNumber.text = secondNumber
        customLayout.callButton.setOnClickListener {
            dialog.dismiss()
        }
        customLayout.firstNumber.setOnClickListener {
            try {
                val url = "https://api.whatsapp.com/send?phone=$firstNumber"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: Exception) {

            }
            dialog.dismiss()
        }
        customLayout.secondNumber.setOnClickListener {
            try {
                val url = "https://api.whatsapp.com/send?phone=$secondNumber"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: Exception) {

            }
        }
        dialog.show()
    }

    private fun invalidWatchDialog(title: String) {
        lateinit var dialog: AlertDialog
        val inflater = LayoutInflater.from(this)
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            this,
            R.style.CustomDialog
        )
        val customLayout: InvalidWatchDialogBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.invalid_watch_dialog,
            null,
            false
        )
        customLayout.txtTitle.text = title
        builder.setCancelable(true)
        builder.setView(customLayout.root)
        dialog = builder.create()
        customLayout.callButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun onVisibilityChange(visibility: Int) {
        if (visibility == 8) {
            mActivityBinding.playerView.hideController()
            mActivityBinding.btnBack.visibility = View.INVISIBLE
            mActivityBinding.btnSetting.visibility = View.INVISIBLE
            mActivityBinding.qualityCard.visibility = View.INVISIBLE
        } else {
            mActivityBinding.playerView.showController()
            mActivityBinding.btnBack.visibility = View.VISIBLE
            if (resolutions.isNotEmpty())
                mActivityBinding.btnSetting.visibility = View.VISIBLE
            else
                mActivityBinding.btnSetting.visibility = View.INVISIBLE

        }

    }

    private fun initializeQualityAdapter() {
        qualityAdapter = QualityAdapter(this, this)
        mActivityBinding.rvQuality.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@CourseDetailsActivity, 1)
            adapter = qualityAdapter
        }
    }

    override fun onBackPressed() {
        lastDuration = (player!!.currentPosition / 1000) % 60
        val minutes = (player!!.currentPosition / (1000 * 60) % 60)
        val hours = (player!!.currentPosition / (1000 * 60 * 60) % 24)
        val total = hours * 60 * 60 + minutes * 60
        if (lastLecId.isNotEmpty() && duration != 0) {
            isBack = true
            val progress = (duration * 10) / 100
            if (total >= progress) {
                if (course.lectures?.get(selectedPosition)?.studentSessions!!.isEmpty()
                )
                    initAddSession(lastLecId)

            }
        }
        super.onBackPressed()
    }

}