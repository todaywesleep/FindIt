package com.papayainc.findit.utils

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth

class AuthUtils {
    companion object {
        val REQUEST_SIGN_IN = 0

        val authObj = FirebaseAuth.getInstance()


    }
}