package com.noob.apps.mvvmcountries.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.CustomDropDownAdapter
import com.noob.apps.mvvmcountries.adapters.TermAdapter
import com.noob.apps.mvvmcountries.adapters.DapartmentAdapter
import com.noob.apps.mvvmcountries.adapters.yearAdapter
import com.noob.apps.mvvmcountries.databinding.ActivityUniversityBinding
import com.noob.apps.mvvmcountries.ui.main.MainActivity
import com.noob.apps.mvvmcountries.utils.UserPreferences
import kotlinx.coroutines.launch

class UniversityActivity : AppCompatActivity() {
    private lateinit var userPreferences: UserPreferences
    private lateinit var mActivityBinding: ActivityUniversityBinding
    private var collage = ""
    private var term = ""
    private var year = ""
    private var dep = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_university)
        val collageList = resources.getStringArray(R.array.Collages)
        val termList = resources.getStringArray(R.array.Term)
        val depList = resources.getStringArray(R.array.Department)
        val yearList = resources.getStringArray(R.array.Year)
        val customDropDownAdapter = CustomDropDownAdapter(this, collageList.toList())
        mActivityBinding.collageSp.adapter = customDropDownAdapter
        val termAdapter = TermAdapter(this, termList.toList())
        mActivityBinding.termSp.adapter = termAdapter
        val departmentAdapter = DapartmentAdapter(this, depList.toList())
        mActivityBinding.depSp.adapter = departmentAdapter
        val yearAdapter = yearAdapter(this, yearList.toList())
        mActivityBinding.yearSp.adapter = yearAdapter
        mActivityBinding.collageSp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0)
                    collage = collageList[position]
                else
                    collage=""
                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                collage = ""
            }
        }
        mActivityBinding.termSp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0)
                    term = termList[position]
                else
                    term=""
                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                term = ""
            }
        }
        mActivityBinding.yearSp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0)
                    year = yearList[position]
                else
                    year=""

                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                year = ""
            }
        }

        mActivityBinding.depSp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0)
                    dep = depList[position]
                else
                    dep=""
                checkValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                dep = ""
            }
        }
        mActivityBinding.saveButton.setOnClickListener{
            lifecycleScope.launch {
                userPreferences.saveUniversityData(true)
            }
            startActivity(Intent(this@UniversityActivity,MainActivity::class.java))

        }
        userPreferences = UserPreferences(this)

        val bookmark = "Hello"
        lifecycleScope.launch {
            userPreferences.incrementCounter(bookmark)
        }


        userPreferences.exampleCounterFlow.asLiveData().observe(this, {
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        })


//        val adapter = ArrayAdapter(this,
//            android.R.layout.simple_spinner_item, collages)
//        mActivityBinding.collageSp.adapter = adapter
//
//        mActivityBinding.collageSp.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>,
//                                        view: View, position: Int, id: Long) {
//               Toast.makeText(this@UniversityActivity,collages[position],Toast.LENGTH_LONG).show()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//           }
    }






    private fun checkValidation() {
        if (collage.isNotEmpty() && term.isNotEmpty() && year.isNotEmpty() && dep.isNotEmpty()) {
            mActivityBinding.saveButton.setBackgroundResource(R.drawable.curved_button_blue)
            mActivityBinding.saveButton.isEnabled=true
        } else {
            mActivityBinding.saveButton.setBackgroundResource(R.drawable.curved_butoon_gray)
            mActivityBinding.saveButton.isEnabled=false
        }

    }
}


