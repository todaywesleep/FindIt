package com.papayainc.findit.utils

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
    }
}