package com.alazar.authfire.util

import android.text.TextUtils
import java.util.regex.Pattern

object Validator {

    private const val EMAIL_PATTERN =
        "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
    private const val PHONE_PATTERN = "^\\+(?:[0-9] ?){6,14}[0-9]$"
    private const val PASS_PATTERN = "^(?=.*[0-9]).{6,20}$"

    fun validateEmail(email: String): Boolean {
        return isMatch(EMAIL_PATTERN, email)
    }

    fun validatePhone(number: String): Boolean {
        return isMatch(PHONE_PATTERN, number)
    }

    fun validatePass(password: String): Boolean {
        return isMatch(PASS_PATTERN, password)
    }

    fun validateRequired(string: String?): Boolean {
        return !TextUtils.isEmpty(string)
    }

    private fun isMatch(inputPattern: String, checkedString: String): Boolean {
        val pattern = Pattern.compile(inputPattern)
        val matcher = pattern.matcher(checkedString)
        return matcher.matches()
    }
}