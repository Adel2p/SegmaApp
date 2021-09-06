package com.noob.apps.mvvmcountries.utils

import java.util.regex.Pattern
import android.text.TextUtils

class PasswordValidation {
    companion object {
        fun isValidPassword(s: String?): Boolean {
            val PASSWORD_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}"
            )
            return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches()
        }
    }
}