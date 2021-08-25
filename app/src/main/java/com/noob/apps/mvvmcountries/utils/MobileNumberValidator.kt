package com.noob.apps.mvvmcountries.utils

class MobileNumberValidator {
    companion object {
        fun validCellPhone(number: String?): Boolean {
            if (number != null) {
                if (number.length == 11 && number.substring(0, 2) == "01")
                    return true

            }
            return false
        }
    }
}