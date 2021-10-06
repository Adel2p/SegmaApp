package com.noob.apps.mvvmcountries.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.mediarouter.media.MediaRouter
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.CollageDropDownAdapter
import com.noob.apps.mvvmcountries.adapters.DapartmentAdapter
import com.noob.apps.mvvmcountries.adapters.TermAdapter
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.databinding.ActivityHomeBinding
import com.noob.apps.mvvmcountries.databinding.ActivityUniversityBinding
import com.noob.apps.mvvmcountries.models.BoardingRequest
import com.noob.apps.mvvmcountries.models.Collage
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.BlockUserDialog
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.UniversityViewModel
import kotlinx.coroutines.launch

class UniversityActivity : BaseActivity() {
    private lateinit var mActivityBinding: ActivityUniversityBinding
    private lateinit var mViewModel: UniversityViewModel
    private lateinit var colleges: MutableList<Collage>
    private lateinit var levels: MutableList<Collage>
    private lateinit var departments: MutableList<Collage>
    private lateinit var roomViewModel: RoomViewModel
    private var collageId = ""
    private var levelId = ""
    private var depId = ""
    private var token = ""
    private var userId = ""
    private val TAG = "HomeActivity"
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
            if (route.name != "Phone")
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
            if (route.name != "Phone")
                showBlockDialog("You Cannot run App on Screen Mirroring")

        }

        override fun onProviderAdded(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderRemoved(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderChanged(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_university)
        mViewModel = ViewModelProvider(this).get(UniversityViewModel::class.java)

        roomViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(RoomViewModel::class.java)

        mActivityBinding.collageSp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    collageId = colleges[position].uuid
                    initLevelsObservers()
                } else
                    collageId = ""
                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                collageId = ""
            }
        }
        mActivityBinding.termSp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    levelId = levels[position].uuid
                    initDepartmentObservers()
                } else
                    levelId = ""
                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                levelId = ""
            }
        }
        mActivityBinding.depSp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                depId = if (position != 0)
                    departments[position].uuid
                else
                    ""
                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                depId = ""
            }
        }
        mActivityBinding.saveButton.setOnClickListener {

            initBoardingObservers()
        }
        userPreferences.getUserId.asLiveData().observeOnce(this, Observer {
            userId = it
            roomViewModel.findUser(userId)
                .observe(this, Observer { result ->
                    token = "Bearer " + result[0].access_token.toString()
                    initCollegesObservers()
                })
        })

    }


    private fun initCollegesObservers() {
        mViewModel.getUniversity(token).observe(this, Observer { response ->
            if (response != null) {
                colleges = response.data.toMutableList()
                colleges.add(0, Collage("0", "please select"))
                val customDropDownAdapter = CollageDropDownAdapter(this, colleges)
                mActivityBinding.collageSp.adapter = customDropDownAdapter
            }
        })
        mViewModel.mShowResponseError.observe(this, Observer {
            AlertDialog.Builder(this).setMessage(it).show()
        })
        mViewModel.mShowProgressBar.observe(this, Observer { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        mViewModel.mShowNetworkError.observe(this, Observer {
            ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                .show(supportFragmentManager, ConnectionDialogFragment.TAG)

        })
    }

    private fun initLevelsObservers() {
        mViewModel.getLevels(token, collageId).observe(this, Observer { collage ->
            if (collage != null) {
                levels = collage.data.toMutableList()
                levels.add(0, Collage("0", "please select"))
                val termAdapter = TermAdapter(this, levels)
                mActivityBinding.termSp.adapter = termAdapter
            }
        })
        mViewModel.mShowResponseError.observe(this, Observer {
            AlertDialog.Builder(this).setMessage(it).show()
        })
        mViewModel.mShowProgressBar.observe(this, Observer { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        mViewModel.mShowNetworkError.observe(this, Observer {
            ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                .show(supportFragmentManager, ConnectionDialogFragment.TAG)

        })
    }

    private fun initDepartmentObservers() {
        mViewModel.getDepartments(token, levelId).observe(this, Observer { collage ->
            if (collage != null) {
                departments = collage.data.toMutableList()
                departments.add(0, Collage("0", "please select"))
                val departmentAdapter = DapartmentAdapter(this, departments)
                mActivityBinding.depSp.adapter = departmentAdapter
            }
        })
        mViewModel.mShowResponseError.observe(this, Observer {
            AlertDialog.Builder(this).setMessage(it).show()
        })
        mViewModel.mShowProgressBar.observe(this, Observer { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        mViewModel.mShowNetworkError.observe(this, Observer {
            ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                .show(supportFragmentManager, ConnectionDialogFragment.TAG)

        })
    }

    private fun initBoardingObservers() {
        mViewModel.postUserUniversity(token, BoardingRequest(collageId, levelId, depId, userId))
            .observe(this, Observer { collage ->
                if (collage != null) {
                    lifecycleScope.launch {
                        userPreferences.saveUniversityData(true)
                    }
                    startActivity(Intent(this@UniversityActivity, WelcomeActivity::class.java))
                }
            })
        mViewModel.mShowResponseError.observe(this, Observer {
            if (it.isNotEmpty())
                AlertDialog.Builder(this).setMessage(it).show()
            else
                AlertDialog.Builder(this).setMessage(getString(R.string.un_expected_error)).show()
        })
        mViewModel.mShowProgressBar.observe(this, Observer { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        mViewModel.mShowNetworkError.observe(this, Observer {
            ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                .show(supportFragmentManager, ConnectionDialogFragment.TAG)

        })
    }

    private fun checkValidation() {
        if (collageId.isNotEmpty() && levelId.isNotEmpty() && depId.isNotEmpty()) {
            mActivityBinding.saveButton.setBackgroundResource(R.drawable.curved_button_blue)
            mActivityBinding.saveButton.isEnabled = true
        } else {
            mActivityBinding.saveButton.setBackgroundResource(R.drawable.curved_butoon_gray)
            mActivityBinding.saveButton.isEnabled = false
        }

    }

    override fun onResume() {
        super.onResume()
        mMediaRouter = MediaRouter.getInstance(this)
        val fm = supportFragmentManager
        val fragment: DiscoveryFragment?
        fragment = DiscoveryFragment()
        fragment.setCallback(mMediaRouterCB)
        fm.beginTransaction().add(fragment, DISCOVERY_FRAGMENT_TAG).commit()
    }
}


