package com.noob.apps.mvvmcountries.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.CollageDropDownAdapter
import com.noob.apps.mvvmcountries.adapters.TermAdapter
import com.noob.apps.mvvmcountries.adapters.DapartmentAdapter
import com.noob.apps.mvvmcountries.adapters.yearAdapter
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.databinding.ActivityUniversityBinding
import com.noob.apps.mvvmcountries.models.BoardingRequest
import com.noob.apps.mvvmcountries.models.Collage
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.UserPreferences
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.UniversityViewModel
import kotlinx.coroutines.launch

class UniversityActivity : BaseActivity() {
    private lateinit var userPreferences: UserPreferences
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_university)
        userPreferences = UserPreferences(this)
        mViewModel = ViewModelProviders.of(this).get(UniversityViewModel::class.java)
        roomViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(RoomViewModel::class.java)
        userPreferences.getUserId.asLiveData().observe(this, Observer {
            userId = it
            roomViewModel.findUser(userId)
                .observe(this, Observer { result ->
                    token = "Bearer "+result[0].access_token.toString()
                    initCollegesObservers()
                })
        })

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
                if (position != 0)
                    depId = departments[position].uuid
                else
                    depId = ""
                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                depId = ""
            }
        }
        mActivityBinding.saveButton.setOnClickListener {

            initBoardingObservers()
        }

    }

    private fun initCollegesObservers() {
        mViewModel.getUniversity(token).observe(this,Observer { collage ->
            colleges = collage.data.toMutableList()
            colleges.add(0, Collage("0", "please select"))
            val customDropDownAdapter = CollageDropDownAdapter(this, colleges)
            mActivityBinding.collageSp.adapter = customDropDownAdapter
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
        mViewModel.getLevels(token, collageId).observe(this,Observer { collage ->
            levels = collage.data.toMutableList()
            levels.add(0, Collage("0", "please select"))
            val termAdapter = TermAdapter(this, levels)
            mActivityBinding.termSp.adapter = termAdapter
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
        mViewModel.getDepartments(token, levelId).observe(this,Observer { collage ->
            departments = collage.data.toMutableList()
            departments.add(0, Collage("0", "please select"))
            val departmentAdapter = DapartmentAdapter(this, departments)
            mActivityBinding.depSp.adapter = departmentAdapter
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
        mViewModel.postUserUniversity(token, BoardingRequest(collageId, levelId, depId))
            .observe(this, Observer{ collage ->
                lifecycleScope.launch {
                    userPreferences.saveUniversityData(true)
                }
                startActivity(Intent(this@UniversityActivity, WelcomeActivity::class.java))
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

    private fun checkValidation() {
        if (collageId.isNotEmpty() && levelId.isNotEmpty() && depId.isNotEmpty()) {
            mActivityBinding.saveButton.setBackgroundResource(R.drawable.curved_button_blue)
            mActivityBinding.saveButton.isEnabled = true
        } else {
            mActivityBinding.saveButton.setBackgroundResource(R.drawable.curved_butoon_gray)
            mActivityBinding.saveButton.isEnabled = false
        }

    }
}


