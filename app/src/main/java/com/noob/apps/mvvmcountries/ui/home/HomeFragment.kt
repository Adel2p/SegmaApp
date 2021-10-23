package com.noob.apps.mvvmcountries.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
import com.noob.apps.mvvmcountries.databinding.FragmentHomeBinding
import com.noob.apps.mvvmcountries.models.Course
import com.noob.apps.mvvmcountries.models.RefreshTokenModel
import com.noob.apps.mvvmcountries.ui.base.BaseFragment
import com.noob.apps.mvvmcountries.ui.details.CourseDetailsActivity
import com.noob.apps.mvvmcountries.ui.dialog.BlockUserDialog
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.CourseViewModel
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : BaseFragment(), RecyclerViewClickListener {
    private lateinit var mActivityBinding: FragmentHomeBinding
    private val courses: MutableList<Course> = mutableListOf()
    private lateinit var mAdapter: CourseAdapter
    private var param1: String? = null
    private var param2: String? = null
    private var userId = ""
    private var token = ""
    private var fcmToken = ""
    private var refreshToken = ""
    private lateinit var courseViewModel: CourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
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
                                userPreferences.saveUserToken(token)
                            }
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

    private fun initializeRecyclerView() {
        mAdapter = CourseAdapter(this)
        mActivityBinding.rvLectures.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = mAdapter
        }
    }

    private fun initializeObservers() {
        courseViewModel.getDepartmentCourses(token)
        courseViewModel.depResponse.observeOnce(viewLifecycleOwner, { kt ->
            if (kt != null) {
                courses.clear()
                courses.addAll(kt.data.toMutableList())
                mAdapter.setData(courses)
                initInfoObservers()
            }
        })
        courseViewModel.mShowResponseError.observeOnce(viewLifecycleOwner, {
            initTokenObservers()
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

    private fun initInfoObservers() {
        courseViewModel.getStudentInfo(token)
        courseViewModel.infoResponse.observeOnce(viewLifecycleOwner, { kt ->
            if (kt != null) {
                mActivityBinding.txtFaculty.text = kt.data.studyFieldName
                mActivityBinding.txtDepartment.text =
                    kt.data.levelName + " " + kt.data.departmentName
                if (!kt.data.enabled)
                    BlockUserDialog.newInstance("")
                        .show(requireActivity().supportFragmentManager, BlockUserDialog.TAG)
                if (fcmToken.isNotEmpty())
                    initFCMTokenObservers()
                if (kt.data.deviceId != null && kt.data.deviceId != deviceId)
                    BlockUserDialog.newInstance("App installed on other device")
                        .show(requireActivity().supportFragmentManager, BlockUserDialog.TAG)
            }
        })
        courseViewModel.mShowResponseError.observeOnce(viewLifecycleOwner, {
            AlertDialog.Builder(requireActivity()).setMessage(it).show()
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

    private fun initTokenObservers() {
        courseViewModel.updateToken(RefreshTokenModel(Constant.REFRESH_TOKEN, refreshToken))
        courseViewModel.updateTokenResponse.observeOnce(viewLifecycleOwner, { kt ->
            if (kt != null) {
                getData()
            }
        })
        courseViewModel.mShowResponseError.observeOnce(viewLifecycleOwner, {
            logOut()
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

    private fun initFCMTokenObservers() {
        courseViewModel.updateFCMToken(token, fcmToken)
        courseViewModel.fcmResponse.observeOnce(viewLifecycleOwner, { kt ->

        })
        courseViewModel.mShowResponseError.observeOnce(viewLifecycleOwner, {
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

    companion object {
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRecyclerViewItemClick(position: Int) {
        val intent = Intent(requireContext(), CourseDetailsActivity::class.java)
        intent.putExtra(Constant.SELECTED_COURSE, courses[position])
        intent.putExtra(Constant.ELIGIBLE_TO_WATCH, courses[position].eligibleToWatch)
        startActivity(intent)
    }

    override fun onQualitySelected(position: Int) {
    }

    override fun openFile(url: String) {
    }
}