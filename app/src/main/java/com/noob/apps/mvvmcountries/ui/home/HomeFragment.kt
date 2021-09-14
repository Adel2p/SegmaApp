package com.noob.apps.mvvmcountries.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.LecturesAdapter
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.databinding.FragmentHomeBinding
import com.noob.apps.mvvmcountries.models.Course
import com.noob.apps.mvvmcountries.models.Lecture
import com.noob.apps.mvvmcountries.ui.base.BaseFragment
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.HomeViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : BaseFragment() {
    private lateinit var mActivityBinding: FragmentHomeBinding
    private val listOfLectures: MutableList<Lecture> = mutableListOf()
    private val courses: MutableList<Course> = mutableListOf()

    private lateinit var mAdapter: LecturesAdapter
    private lateinit var mViewModel: HomeViewModel
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var roomViewModel: RoomViewModel
    private var userId = ""
    private var token = ""

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
    ): View? {
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        roomViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(
                requireActivity().application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireActivity()))
            )
        ).get(RoomViewModel::class.java)
        initializeRecyclerView()
        userPreferences.getUserId.asLiveData().observe(viewLifecycleOwner, {
            userId = it
            roomViewModel.findUser(userId)
                .observe(viewLifecycleOwner, { result ->
                    token = "Bearer " + result[0].access_token.toString()
                    initializeObservers()
                })
        })
    }

    private fun initializeRecyclerView() {
        mAdapter = LecturesAdapter()
        mActivityBinding.rvLectures.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)

            adapter = mAdapter
        }
    }

    private fun initializeObservers() {
        mViewModel.getDepartmentCourses(
            token
        ).observe(viewLifecycleOwner, { kt ->
            courses.addAll(kt.data.toMutableList())
            mAdapter.setData(courses)
        })
        mViewModel.mShowResponseError.observe(viewLifecycleOwner, {
            AlertDialog.Builder(requireActivity()).setMessage(it).show()
        })
        mViewModel.mShowProgressBar.observe(viewLifecycleOwner, { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        mViewModel.mShowNetworkError.observe(viewLifecycleOwner, {
            ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                .show(requireActivity().supportFragmentManager, ConnectionDialogFragment.TAG)

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
}