package com.noob.apps.mvvmcountries.ui.course

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.CourseAdapter
import com.noob.apps.mvvmcountries.adapters.RecyclerViewClickListener
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.databinding.FragmentCoursesBinding
import com.noob.apps.mvvmcountries.databinding.FragmentHomeBinding
import com.noob.apps.mvvmcountries.models.Course
import com.noob.apps.mvvmcountries.ui.base.BaseFragment
import com.noob.apps.mvvmcountries.ui.details.CourseDetailsActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.CourseViewModel
import kotlinx.coroutines.launch

class CoursesFragment : BaseFragment(), RecyclerViewClickListener {
    private lateinit var mActivityBinding: FragmentCoursesBinding
    private val courses: MutableList<Course> = mutableListOf()
    private lateinit var mAdapter: CourseAdapter
    private lateinit var courseViewModel: CourseViewModel
    private var userId = ""
    private var token = ""
    private var fcmToken = ""
    private var refreshToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_courses, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    companion object {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = ""
        courseViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                requireActivity().application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(CourseViewModel::class.java)
        initializeRecyclerView()
        getData()
        userPreferences.getFCMToken.asLiveData().observeOnce(viewLifecycleOwner, {
            fcmToken = it

        })
        mActivityBinding.swipeContainer.setOnRefreshListener {
            mActivityBinding.swipeContainer.isRefreshing = false
            getData()
        }
    }

    private fun initializeRecyclerView() {
        mAdapter = CourseAdapter(this)
        mActivityBinding.rvLectures.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = mAdapter
        }
    }

    private fun getData() {
        userPreferences.getUserId.asLiveData().observeOnce(viewLifecycleOwner, {
            if (it != null) {
                userId = it
                courseViewModel.findUser(userId)
                    .observeOnce(viewLifecycleOwner, { result ->
                        if (result != null && result.size > 0) {
                            token = "Bearer " + result[0].access_token.toString()
                            refreshToken = result[0].refresh_token.toString()
                            lifecycleScope.launch {
                                userPreferences.saveRefreshToken(refreshToken)
                            }
                            initializeObservers()
                        } else {
                            logOut()
                        }

                    })
            }
        })
    }

    private fun initializeObservers() {
        courseViewModel.getStudentCourses(token)
        courseViewModel.myCourseResponse.observeOnce(viewLifecycleOwner, { kt ->
            if (kt != null) {
                courses.clear()
                courses.addAll(kt.data.toMutableList())
                mAdapter.setData(courses)
                if (courses.isNotEmpty()) {
                    mActivityBinding.dataLayout.visibility = View.VISIBLE
                    mActivityBinding.emptyLayout.visibility = View.INVISIBLE
                }

            }
        })
        courseViewModel.mShowProgressBar.observe(viewLifecycleOwner, { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        courseViewModel.mShowNetworkError.observeOnce(viewLifecycleOwner, {
            if (it != null) {
                ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                    .show(requireActivity().supportFragmentManager, ConnectionDialogFragment.TAG)
            }

        })
    }

    private fun logOut() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    override fun onRecyclerViewItemClick(position: Int) {
        val intent = Intent(requireContext(), CourseDetailsActivity::class.java)
        intent.putExtra(Constant.SELECTED_COURSE, courses[position])
        intent.putExtra(Constant.ELIGIBLE_TO_WATCH, courses[position].eligibleToWatch)
        startActivity(intent)
    }

    override fun onQualitySelected(position: Int) {
        TODO("Not yet implemented")
    }
}