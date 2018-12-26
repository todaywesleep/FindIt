package com.papayainc.findit.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class AuthUtils {
    companion object {
        interface Callback {
            fun isSessionResumed(b: Boolean)
            fun isLoginSuccessful(isSuccessful: Boolean, error: String?)
        }

        private val TAG = "[" + AuthUtils::class.java.simpleName + "]"

        var authObj: FirebaseAuth = FirebaseAuth.getInstance()!!
        private var mCallback: Callback? = null

        init {
            authObj.addAuthStateListener { auth ->
                if (auth.currentUser == null){

                }else{
                    if (mCallback != null){
                        Log.d("dbg", "callback is here")
                        mCallback!!.isLoginSuccessful(true, null)
                    }
                }
            }
        }

        fun setCallback(callback: Callback) {
            this.mCallback = callback
        }

        fun clearCallback() {
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
            //Set only failure listener, because we have change auth obj state listener (login - there)
            authObj.signInWithEmailAndPassword(email, password).apply {
                if (mCallback != null) {
                    addOnFailureListener {
                        mCallback!!.isLoginSuccessful(false, it.message)
                    }
                }
            }
        }
    }
}