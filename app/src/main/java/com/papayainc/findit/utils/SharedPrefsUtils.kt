package com.papayainc.findit.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.papayainc.findit.constants.SharedPrefsConstants
import com.papayainc.findit.modal.UserSettings

class SharedPrefsUtils {
    companion object {
        lateinit var sharedPrefs: SharedPreferences

        fun initSharedPrefs(activity: Activity) {
            sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE)
        }

        fun unpackUserSettings(): UserSettings {
            val userObj = UserSettings()

            userObj.isAutoFlashEnabled = isAutoFlashEnabled()
            return userObj
        }

        fun setCameraFlashMode(newMode: Boolean) {
            val newFlashMode =
                if (newMode) SharedPrefsConstants.SETTINGS_AUTO_FLASH_ON else SharedPrefsConstants.SETTINGS_AUTO_FLASH_OFF

            val editor = sharedPrefs.edit()
            editor.putBoolean(SharedPrefsConstants.SETTINGS_AUTO_FLASH, newFlashMode)
            editor.apply()
        }

        fun isAutoFlashEnabled(): Boolean {
            return sharedPrefs.getBoolean(
                SharedPrefsConstants.SETTINGS_AUTO_FLASH,
                SharedPrefsConstants.SETTINGS_AUTO_FLASH_DEFAULT
            )
        }
    }
}