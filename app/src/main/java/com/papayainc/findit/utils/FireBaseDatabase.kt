package com.papayainc.findit.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireBaseDatabase {
    companion object {
        private val TAG = "[" + FireBaseDatabase::class.java.simpleName + "]"

        interface Callback {
            fun isUserExistInDatabase(isExist: Boolean)
        }

        private var mCallback: Callback? = null

        fun setCallback(callback: Callback){
            this.mCallback = callback
        }

        fun isUserExistInDatabase(userEmail: String) {
            val ref = FirebaseDatabase.getInstance().getReference("users")
            ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    if (mCallback != null){
                        mCallback!!.isUserExistInDatabase(false)
                    }
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Log.d(TAG, "User exist in FireBaseDatabase")
                        if (mCallback != null){
                            mCallback!!.isUserExistInDatabase(false)
                        }
                    }else{
                        Log.d(TAG, "User does not exist in FireBaseDatabase")
                        if (mCallback != null){
                            mCallback!!.isUserExistInDatabase(true)
                        }
                    }
                }
            })
        }

        fun clearListener(){
            if (mCallback != null)
                mCallback = null
        }
    }
}