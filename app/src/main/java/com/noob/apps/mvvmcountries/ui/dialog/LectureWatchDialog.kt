package com.noob.apps.mvvmcountries.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.LectureWatchDialogBinding
import com.noob.apps.mvvmcountries.models.LectureDetails
import com.noob.apps.mvvmcountries.ui.details.CourseDetailsActivity
import java.text.SimpleDateFormat
import java.util.*


class LectureWatchDialog : DialogFragment() {
    private lateinit var mLectureDetails: LectureDetails
    private lateinit var mActivityBinding: LectureWatchDialogBinding

    companion object {

        const val TAG = "LectureWatchDialog"

        private const val KEY_OPERATION = "KEY_OPERATION"

        fun newInstance(lectureDetails: LectureDetails): LectureWatchDialog {
            val args = Bundle()
            args.putSerializable(KEY_OPERATION, lectureDetails)
            val fragment = LectureWatchDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLectureDetails =
                (it.getSerializable(KEY_OPERATION))!! as LectureDetails
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.lecture_watch_dialog, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            dialog.getWindow()
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivityBinding.availableWatches.text =
            mLectureDetails.allowedSessions.toString() + " " + context?.resources?.getString(R.string.watches)
        mActivityBinding.restWatches.text =
            mLectureDetails.actualSessions.toString() + " " + context?.resources?.getString(R.string.watches)
        mActivityBinding.startDate.text = getStartDate(0)
        mActivityBinding.endDate.text = getStartDate(mLectureDetails.sessionTimeout)
        mActivityBinding.watchLetter.setOnClickListener {
            dismiss()
        }
        mActivityBinding.startWatch.setOnClickListener {
            (activity as CourseDetailsActivity?)?.onStartWatchClicked()
            dismiss()
        }

    }


    private fun getStartDate(hours: Int): String {
        val c = Calendar.getInstance(Locale.ENGLISH).time
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.getDefault())
        val formattedDate: String = df.format(c)
        val d = df.parse(formattedDate)
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.time = d
        cal.add(Calendar.HOUR_OF_DAY, hours)
        return df.format(cal.time)
    }

}