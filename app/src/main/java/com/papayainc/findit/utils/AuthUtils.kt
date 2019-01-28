package com.papayainc.findit.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
                    mCallback!!.isLoginSuccessful(false, null)
                }else{
                    if (mCallback != null){
                        FireBaseDataBaseWorker.createUserWrite(auth.currentUser!!)
                        mCallback!!.isLoginSuccessful(true, null)
                    }
                }
            }
        }

        fun getCurrentUser(): FirebaseUser? {
            return authObj.currentUser
        }

        fun setCallback(callback: Callback) {
            this.mCallback = callback
        }

        fun clearCallback() {
            if (mCallback != null) mCallback = null
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