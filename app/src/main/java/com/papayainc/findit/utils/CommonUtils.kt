package com.papayainc.findit.utils

import android.annotation.SuppressLint
import com.papayainc.findit.constants.CommonConstants
import java.text.SimpleDateFormat
import java.util.*


class CommonUtils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getFormattedDate(date: Date): String {
            return SimpleDateFormat(CommonConstants.TIME_FORMAT).format(date)
        }

        fun getMinutesBetweenDates(d1: Long, d2: Long): Long {
            val diff = d1 - d2
            val seconds = diff / 1000
            val minutes = seconds / 60

            return Math.abs(minutes)
        }
    }
}