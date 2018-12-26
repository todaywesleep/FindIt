package com.papayainc.findit.utils

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireBaseDataBaseWorker {
    companion object {
        val TAG = "[" + FireBaseDataBaseWorker::class.java.simpleName + "]"

        private const val TABLE_POSSIBLE_LABELS = "possible_labels"
        private const val TABLE_USERS = "users"
        private const val TABLE_QUESTS = "quests"
        private const val FIELD_EXPERIENCE = "experience"
        private const val FIELD_LEVEL = "level"

        private val database = FirebaseDatabase.getInstance().reference

        fun writeLabels(labels: HashMap<String, String>) {
            val databaseReference = database.child(TABLE_POSSIBLE_LABELS)

            labels.forEach { item ->
                databaseReference.child(item.key).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            databaseReference.child(item.key).setValue(item.value)
                            Log.d(TAG, "ADD NEW label: ${item.value}")
                        } else {
                            Log.d(TAG, "Exist")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "${databaseError.message} startLabelsAnalyze")
                    }
                })
            }
        }

        fun createUserWrite(user: FirebaseUser) {
            val databaseReference = database.child(TABLE_USERS)

            val uid = user.uid

            databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        databaseReference.child(uid).setValue(null)
                        Log.d(TAG, "User is not exist, create user $uid")

                        val userDatabaseReference = databaseReference.child(uid)

                        userDatabaseReference.child(TABLE_QUESTS).setValue(null)
                        userDatabaseReference.child(FIELD_EXPERIENCE).setValue(0)
                        userDatabaseReference.child(FIELD_LEVEL).setValue(0)
                    }else{
                        Log.d(TAG, "User exist, skip init process")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "${databaseError.message} createUserWrite")
                }
            })
        }
    }
}