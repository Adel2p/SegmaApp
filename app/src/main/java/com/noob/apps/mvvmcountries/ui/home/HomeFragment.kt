package com.noob.apps.mvvmcountries.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.CountriesListAdapter
import com.noob.apps.mvvmcountries.adapters.LecturesAdapter
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding
import com.noob.apps.mvvmcountries.databinding.FragmentHomeBinding
import com.noob.apps.mvvmcountries.models.Lecture


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private lateinit var mActivityBinding: FragmentHomeBinding
     private val listOfLectures: MutableList<Lecture> = mutableListOf()
    private lateinit var mAdapter: LecturesAdapter

    private var param1: String? = null
    private var param2: String? = null

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
        val lec = Lecture("computer science", "DR mohamed adel", "2")
        val lec2 = Lecture("computer science", "DR mohamed adel", "2")
        val lec3 = Lecture("computer science", "DR mohamed adel", "2")
        val lec4 = Lecture("computer science", "DR mohamed adel", "2")
        val lec5 = Lecture("computer science", "DR mohamed adel", "2")
        val lec6 = Lecture("computer science", "DR mohamed adel", "2")
        val lec7 = Lecture("computer science", "DR mohamed adel", "2")

        listOfLectures.add(lec)
        listOfLectures.add(lec2)
        listOfLectures.add(lec3)
        listOfLectures.add(lec4)
        listOfLectures.add(lec5)
        listOfLectures.add(lec6)
        listOfLectures.add(lec7)


        initializeRecyclerView()
        mAdapter.setData(listOfLectures)
    }

    private fun initializeRecyclerView() {
        mAdapter = LecturesAdapter()
        mActivityBinding.rvLectures.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)

            adapter = mAdapter
        }
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