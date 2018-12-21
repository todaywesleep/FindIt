package com.papayainc.findit.utils

import com.google.firebase.auth.UserProfileChangeRequest

class FireBaseDatabase {
    companion object {
        private val TAG = "[" + FireBaseDatabase::class.java.simpleName + "]"

        interface Callback {
            fun onUserProfileUpdated()
        }

        private var mCallback: Callback? = null

        fun setCallback(callback: Callback){
            this.mCallback = callback
        }

        fun clearListener(){
            if (mCallback != null)
                mCallback = null
        }

        fun updateUserProfile(param: String, value: String){
            val currentUser = AuthUtils.authObj.currentUser
        }
    }
}