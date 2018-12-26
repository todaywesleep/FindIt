package com.papayainc.findit.utils

import android.annotation.SuppressLint
import com.papayainc.findit.constants.CommonConstants
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getFormattedDate(date: Date): String{
            return SimpleDateFormat(CommonConstants.TIME_FORMAT).format(date)
        }
    }
}