package com.noob.apps.mvvmcountries.ui.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.LibraryAdapter
import com.noob.apps.mvvmcountries.databinding.FragmentLibraryBinding
import com.noob.apps.mvvmcountries.models.Library

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LibraryFragment : Fragment() {
    private lateinit var mActivityBinding: FragmentLibraryBinding
    private val listOfLectures: MutableList<Library> = mutableListOf()
    private lateinit var mAdapter: LibraryAdapter

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
        // Inflate the layout for this fragment
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false)
        return mActivityBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lec = Library("computer science", "DR mohamed adel")
        val lec2 = Library("computer science", "DR mohamed adel")
        val lec3 = Library("computer science", "DR mohamed adel")
        val lec4 = Library("computer science", "DR mohamed adel")
        val lec5 = Library("computer science", "DR mohamed adel")
        val lec6 = Library("computer science", "DR mohamed adel")
        val lec7 = Library("computer science", "DR mohamed adel")

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
        mAdapter = LibraryAdapter()
        mActivityBinding.libRec.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)

            adapter = mAdapter
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}