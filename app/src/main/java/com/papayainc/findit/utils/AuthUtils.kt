package com.papayainc.findit.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.papayainc.findit.modal.User
import com.papayainc.findit.modal.UserSettings

class AuthUtils {
    companion object {
        interface Callback {
            fun isSessionResumed(b: Boolean)
            fun isLoginSuccessful(isSuccessful: Boolean, error: String?)
        }

        private val TAG = "[" + AuthUtils::class.java.simpleName + "]"

        var authObj: FirebaseAuth
        var currentUserObj: User? = null
        private var mCallback: Callback? = null

        init {
            authObj = FirebaseAuth.getInstance()!!
            unpackUserData(authObj.currentUser)
        }

        private fun unpackUserData(user: FirebaseUser?){
            currentUserObj = User()
            currentUserObj!!.apply {
                if (user != null) {
                    email = user.email.toString()
                    userSettings = UserSettings()
                } else {
                    email = ""
                    userSettings = UserSettings()
                }
            }
        }

        fun setCallback(callback: Callback) {
            this.mCallback = callback
        }

        fun clearListener() {
            if (mCallback != null) mCallback = null
        }

        fun renewSession() {
            val currentUser = authObj.currentUser

            if (mCallback != null) {
                if (currentUser != null) {
                    currentUser.getIdToken(true).addOnSuccessListener {
                        Log.d(TAG, "Session resumed")
                        mCallback!!.isSessionResumed(true)
                    }.addOnFailureListener {
                        Log.d(TAG, "Session resume error " + it.message)
                        mCallback!!.isSessionResumed(false)
                    }
                } else {
                    Log.d(TAG, "Current user is absent")
                    mCallback!!.isSessionResumed(false)
                }
            }
        }

        fun login(email: String, password: String) {
            authObj.signInWithEmailAndPassword(email, password).apply {
                if (mCallback != null) {
                    addOnSuccessListener {
                        mCallback!!.isLoginSuccessful(true, null)
                    }
                    addOnFailureListener {
                        mCallback!!.isLoginSuccessful(false, it.message)
                    }
                }
            }
        }
    }
}