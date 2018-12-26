package com.papayainc.findit.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireBaseDatabase {
    companion object {
        val TAG = "[" + FireBaseDatabase::class.java.simpleName + "]"
        private const val POSSIBLE_LABELS = "possible_labels"

        private val database = FirebaseDatabase.getInstance().reference

        fun writeLabels(labels: HashMap<String, String>){
            val databaseReference = database.child(POSSIBLE_LABELS)

            labels.forEach { item ->
                databaseReference.child(item.key).
                    addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()){
                                databaseReference.child(item.key).setValue(item.value)
                                Log.d(TAG, "ADD NEW label: ${item.value}")
                            }else{
                                Log.d(TAG, "Exist")
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, "${databaseError.message} startLabelsAnalyze()")
                        }
                    })
            }
        }
    }
}