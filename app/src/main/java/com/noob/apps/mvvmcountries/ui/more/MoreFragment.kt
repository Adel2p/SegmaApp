package com.noob.apps.mvvmcountries.ui.more

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.FragmentMoreBinding
import com.noob.apps.mvvmcountries.ui.dialog.AboutSegmaDialog
import com.noob.apps.mvvmcountries.ui.dialog.LanguageBottomDialog
import com.noob.apps.mvvmcountries.ui.dialog.NotificationSettingDialog
import com.noob.apps.mvvmcountries.ui.login.LoginActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MoreFragment : Fragment() {
    private lateinit var mActivityBinding: FragmentMoreBinding
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
    ): View {

        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivityBinding.txtFavoriteLec.setOnClickListener {
            activity?.let {
                val intent = Intent(it, FavouriteLectureActivity::class.java)
                it.startActivity(intent)
            }

        }
        mActivityBinding.txtNotification.setOnClickListener {
            activity?.let {
                val intent = Intent(it, NotificationActivity::class.java)
                it.startActivity(intent)
            }

        }
        mActivityBinding.txtPreviousExams.setOnClickListener {
            activity?.let {
                val intent = Intent(it, PreviousExamActivity::class.java)
                it.startActivity(intent)
            }

        }

        mActivityBinding.txtChangeLanguage.setOnClickListener {
            val bottomSheetFragment = LanguageBottomDialog()
            activity?.let { it1 ->
                bottomSheetFragment.show(
                    it1.supportFragmentManager,
                    LanguageBottomDialog.TAG
                )
            }
        }
        mActivityBinding.txtAboutApp.setOnClickListener {
            val aboutDialog = AboutSegmaDialog()
            activity?.let { it1 ->
                aboutDialog.show(
                    it1.supportFragmentManager,
                    AboutSegmaDialog.TAG
                )
            }
        }
        mActivityBinding.txtLogOut.setOnClickListener {
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            activity?.finishAffinity()
        }
        mActivityBinding.txtNotificationSetting.setOnClickListener {
            val notificationsettingdialog = NotificationSettingDialog()
            activity?.let { it1 -> notificationsettingdialog.show(it1.supportFragmentManager, notificationsettingdialog.tag) }
        }

    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            MoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}