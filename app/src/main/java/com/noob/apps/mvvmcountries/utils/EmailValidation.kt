package com.noob.apps.mvvmcountries.utils

import java.util.regex.Pattern

class EmailValidation {
    companion object {
        fun validMail(yourEmailString: String): Boolean {
            val emailPattern = Pattern.compile(".+@.+\\.[a-z]+")
            val emailMatcher = emailPattern.matcher(yourEmailString)
            return emailMatcher.matches()
        }
    }
}