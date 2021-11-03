package com.noob.apps.mvvmcountries.ui.details

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.mediarouter.media.MediaRouter
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
import com.noob.apps.mvvmcountries.ui.base.BaseActivity2
import com.noob.apps.mvvmcountries.ui.dialog.BlockUserDialog
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


class CourseDetailsActivity : BaseActivity2(), RecyclerViewClickListener,
    PlayerControlView.VisibilityListener {
    private val TAG = "CourseDetailsActivity"
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
    private var lecturesDB = mutableListOf<WatchedLectures>()
    private var currentSpeed = "1"

    companion object {
        var lastQualityPosition = 0
        var lastSpeedPosition = 0
    }

    private var mMediaRouter: MediaRouter? = null
    private val DISCOVERY_FRAGMENT_TAG = "DiscoveryFragment"
    private val mMediaRouterCB: MediaRouter.Callback = object : MediaRouter.Callback() {
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteAdded: route=$route"
            )

        }

        override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteChanged: route=$route"
            )

        }


        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteRemoved: route=$route"
            )

        }

        override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteSelected: route=$route"
            )
            if (route.connectionState == 2)
                showBlockDialog("You Cannot run App on Screen Mirroring")


        }

        override fun onRouteUnselected(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteUnselected: route=$route"
            )

            hideBlockDialog()
        }

        override fun onRouteVolumeChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
        }

        override fun onRoutePresentationDisplayChanged(
            router: MediaRouter,
            route: MediaRouter.RouteInfo
        ) {
            Log.d(
                TAG,
                "onRoutePresentationDisplayChanged: route=$route"
            )
            if (route.connectionState == 2)
                showBlockDialog("You Cannot run App on Screen Mirroring")

        }

        override fun onProviderAdded(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderRemoved(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderChanged(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        mActivityBinding.aspect.setAspectRatio(16f / 9f)
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
        val prevButton: AppCompatImageButton =
            mActivityBinding.playerView.findViewById(R.id.player_rewind)
        val forwardButton: AppCompatImageButton =
            mActivityBinding.playerView.findViewById(R.id.player_forward)
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
        prevButton.setOnClickListener {
            player?.seekTo(player!!.currentPosition - 10000)

        }
        forwardButton.setOnClickListener {
            player?.seekTo(player!!.currentPosition + 10000)

        }
        normalScreenButton.setOnClickListener {
            if (!isFullScreen) {
                isFullScreen = true
                supportActionBar?.hide()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val height = displayMetrics.heightPixels / 2
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                )
                mActivityBinding.aspect.layoutParams = layoutParams
                //  mActivityBinding.continueButton.visibility = View.INVISIBLE
            } else {
                isFullScreen = false
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    600
                )
                mActivityBinding.aspect.layoutParams = layoutParams
                //  mActivityBinding.continueButton.visibility = View.VISIBLE

            }
        }
        mActivityBinding.btnBack.setOnClickListener {
            if (isFullScreen) {
                isFullScreen = false
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    600
                )
                mActivityBinding.aspect.layoutParams = layoutParams
            } else {
                lastDuration = (player!!.currentPosition / 1000) % 60
                val minutes = (player!!.currentPosition / (1000 * 60) % 60)
                val hours = (player!!.currentPosition / (1000 * 60 * 60) % 24)
                val total = hours * 60 * 60 + minutes * 60
                if (lastLecId.isNotEmpty() && duration != 0) {
                    isBack = true
                    val lecture = WatchedLectures(selectedLectureId, player!!.currentPosition)
                    roomViewModel.updateLecture(lecture)
                    val progress = (duration * 10) / 100
                    if (total >= progress) {
                        if (course.lectures?.get(selectedPosition)?.studentSessions!!.isEmpty()
                        )

                            initAddSession(lastLecId)

                    } else
                        finish()
                } else
                    finish()
            }

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
            if (eligibleToWatch)
                callWinnerDialog()
            else
                invalidWatchDialog(getString(R.string.enrrol_first))

        }
        mActivityBinding.txtFolder.setOnClickListener {
            if (eligibleToWatch) {
                val intent = Intent(this, LectureFolderActivity::class.java)
                intent.putExtra(Constant.SELECTED_COURSE, course)
                startActivity(intent)
            } else
                invalidWatchDialog(getString(R.string.enrrol_first))
        }
        mActivityBinding.txtAboutCourse.setOnClickListener {
            if (eligibleToWatch)
                openGroupDialog()
            else
                invalidWatchDialog(getString(R.string.enrrol_first))
        }
        mActivityBinding.speedTxt.setOnClickListener {
            if (currentSpeed == "1") {
                mActivityBinding.speedTxt.text = "1.25X"
                currentSpeed = "1.25"
                player!!.setPlaybackSpeed(1.25f)
            } else if (currentSpeed == "1.25") {
                mActivityBinding.speedTxt.text = "1.5X"
                currentSpeed = "1.5"
                player!!.setPlaybackSpeed(1.5f)
            } else if (currentSpeed == "1.5") {
                mActivityBinding.speedTxt.text = "1.75X"
                currentSpeed = "1.75"
                player!!.setPlaybackSpeed(1.75f)
            } else if (currentSpeed == "1.75") {
                mActivityBinding.speedTxt.text = "2X"
                currentSpeed = "2"
                player!!.setPlaybackSpeed(2f)
            } else if (currentSpeed == "2") {
                mActivityBinding.speedTxt.text = "1X"
                currentSpeed = "1"
                player!!.setPlaybackSpeed(1f)
            }
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
        roomViewModel.getLectures()
            .observe(this, { result ->
                lecturesDB = result
            })

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
        roomViewModel.getLectures()
            .observe(this, { result ->
                lecturesDB = result
                if (resolutions.isNotEmpty() && lecturesDB.isNotEmpty()) {
                    //   Toast.makeText(this, lectureResponse.uuid, Toast.LENGTH_LONG).show()
                    val lecture = lecturesDB.filter { it.uuid == lectureResponse.uuid }
                    if (lecture.isNotEmpty())
                        player!!.seekTo(0, lecture[0].position)
                }
            })
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
        player!!.setPlaybackSpeed(1f)

    }

    private fun startAnimation() {
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x - 260
        mActivityBinding.mobileNumber.visibility = View.VISIBLE
        mActivityBinding.mobileNumber.clearAnimation()
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val R = Random()
                    val dx = R.nextFloat() * width
                    val dy = R.nextFloat() * 450
                    val timer = Timer()
                    mActivityBinding.mobileNumber.animate()
                        .x(dx)
                        .y(dy)
                        .setDuration(0)
                        .start()
                }
            }
        }, 0, 6000)
    }


    override fun onRecyclerViewItemClick(position: Int) {
        if (!eligibleToWatch) {
            invalidWatchDialog(getString(R.string.enrrol_first))
        } else {
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
                val lecture = WatchedLectures(selectedLectureId, player!!.currentPosition)
                roomViewModel.updateLecture(lecture)
                val progress = (duration * 10) / 100
                if (total >= progress) {
                    if (course.lectures?.get(position)?.studentSessions!!.isEmpty()
                    )
                        initAddSession(lastLecId)
                }
            }
            if (eligibleToWatch) {
                course.lectures?.get(position)?.let { initLectureInfo(it.uuid) }
            }
        }

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

    override fun openFile(url: String) {
    }

    fun onStartWatchClicked() {
        var watchedLectures = WatchedLectures(lectureResponse.uuid, 0)
        roomViewModel.addLecture(watchedLectures)
        startTime = getStartDate()
        endTime = getStartDate(sessionTimeout)
        releasePlayer()
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
        val lecture = lecturesDB.filter { it.uuid == lectureResponse.uuid }
        if (lectureResponse.allowedSessions - lectureResponse.actualSessions == 0) {
            invalidWatchDialog(getString(R.string.excced_watch))
        } else if (lecture.isNotEmpty() &&
            lectureResponse.actualSessions <= lectureResponse.allowedSessions
        ) {
            releasePlayer()
            initializePlayer()
            clearTimer()
            if (lectureResponse.studentSessions.isNotEmpty()) {
                startTime = getStartDate()
                endTime = getStartDate(sessionTimeout)
                printDifferenceDateForHours(startTime, endTime)
            }
            createMediaItem(resolutions[0].link)
        } else if (lectureResponse.studentSessions.isEmpty())
            LectureWatchDialog.newInstance(lectureResponse)
                .show(
                    supportFragmentManager,
                    LectureWatchDialog.TAG
                )
        else if (
            lectureResponse.actualSessions <= lectureResponse.allowedSessions
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
                invalidWatchDialog(getString(R.string.excced_watch))


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
        mMediaRouter = MediaRouter.getInstance(this)
        val fm = supportFragmentManager
        val fragment: BaseActivity.DiscoveryFragment?
        fragment = BaseActivity.DiscoveryFragment()
        fragment.setCallback(mMediaRouterCB)
        fm.beginTransaction().add(fragment, DISCOVERY_FRAGMENT_TAG).commit()
//        if (Settings.Secure.getInt(contentResolver, Settings.Secure.ADB_ENABLED, 0) == 1) {
//            return BlockUserDialog.newInstance("Please turn off usb debugging\n")
//                .show(supportFragmentManager, BlockUserDialog.TAG)
//        }
    }

    override fun onPause() {
        super.onPause()

        if (player != null) {
            lastDuration = (player!!.currentPosition / 1000) % 60
            val minutes = (player!!.currentPosition / (1000 * 60) % 60)
            val hours = (player!!.currentPosition / (1000 * 60 * 60) % 24)
            val total = hours * 60 * 60 + minutes * 60
            if (selectedLectureId.isNotEmpty()) {
                val lecture = WatchedLectures(selectedLectureId, player!!.currentPosition)
                roomViewModel.updateLecture(lecture)
            }
            if (lastLecId.isNotEmpty() && duration != 0) {
                isBack = true
                val progress = (duration * 10) / 100
                if (total >= progress) {
                    if (course.lectures?.get(selectedPosition)?.studentSessions!!.isEmpty()
                    )
                        initAddSession(lastLecId)

                }
            }
        }
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

    private fun openGroupDialog() {
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
        customLayout.firstNumber.text = getString(R.string.whatsApp)
        customLayout.secondNumber.text = getString(R.string.Facebook)
        customLayout.callButton.setOnClickListener {
            dialog.dismiss()
        }
        customLayout.firstNumber.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(Constant.WEB_URL, course.whatsapp)
            startActivity(intent)
        }
        customLayout.secondNumber.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(Constant.WEB_URL, course.facebook)
            startActivity(intent)
        }
        dialog.show()
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
            mActivityBinding.speedTxt.visibility = View.INVISIBLE
        } else {
            mActivityBinding.playerView.showController()
            mActivityBinding.btnBack.visibility = View.VISIBLE
            mActivityBinding.speedTxt.visibility = View.VISIBLE
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
        if (player != null) {
            lastDuration = (player!!.currentPosition / 1000) % 60
            val minutes = (player!!.currentPosition / (1000 * 60) % 60)
            val hours = (player!!.currentPosition / (1000 * 60 * 60) % 24)
            val total = hours * 60 * 60 + minutes * 60
            if (selectedLectureId.isNotEmpty()) {
                val lecture = WatchedLectures(selectedLectureId, player!!.currentPosition)
                roomViewModel.updateLecture(lecture)
            }
            if (lastLecId.isNotEmpty() && duration != 0) {
                isBack = true
                val progress = (duration * 10) / 100
                if (total >= progress) {
                    if (course.lectures?.get(selectedPosition)?.studentSessions!!.isEmpty()
                    )
                        initAddSession(lastLecId)

                }
            }
        }

        super.onBackPressed()
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI2()
        }
    }

    fun hideSystemUI2() {
        // Enables  "lean back" mode
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView
        decorView.systemUiVisibility = ( // Hide the nav bar and status bar
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }


}