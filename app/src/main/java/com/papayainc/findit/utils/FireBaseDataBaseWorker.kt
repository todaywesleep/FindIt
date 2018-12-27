package com.papayainc.findit.utils

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.papayainc.findit.constants.CommonConstants
import com.papayainc.findit.model.Quest
import java.util.*
import kotlin.random.Random

class FireBaseDataBaseWorker {
    companion object {
        val TAG = "[" + FireBaseDataBaseWorker::class.java.simpleName + "]"

        private const val TABLE_POSSIBLE_LABELS = "possible_labels"
        private const val TABLE_USERS = "users"
        private const val TABLE_USER_QUESTS = "quests"
        private const val TABLE_COMPLETED_QUESTS = "completed_quests"

        private const val FIELD_EXPERIENCE = "experience"
        private const val FIELD_LEVEL = "level"
        private const val FIELD_LAST_REQUEST_QUEST_TIME = "last_request_quest_time"

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

                        userDatabaseReference.child(TABLE_USER_QUESTS).setValue(null)
                        userDatabaseReference.child(FIELD_EXPERIENCE).setValue(0)
                        userDatabaseReference.child(FIELD_LEVEL).setValue(0)
                        requestQuest(CommonConstants.INITIAL_QUESTS_AMOUNT)
                    } else {
                        Log.d(TAG, "User exist, skip init process")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "${databaseError.message} createUserWrite")
                }
            })
        }

        fun requestQuest(amount: Int) {
            val currentUser = AuthUtils.getCurrentUser()

            if (currentUser != null) {
                val userReference = database.child(TABLE_USERS)
                val possibleQuestsReference = database.child(TABLE_POSSIBLE_LABELS)

                //Request current user quests table
                userReference.child(currentUser.uid).child(TABLE_USER_QUESTS)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            //Preparing collection of current quests (for avoid quest duplications in questsList)
                            val userQuestsList: ArrayList<String> = arrayListOf()

                            //Compilation userQuestsCollection
                            val userQuestsIterator = dataSnapshot.children.iterator()
                            while (userQuestsIterator.hasNext()) {
                                val itemKey = userQuestsIterator.next().key
                                if (itemKey != null) {
                                    userQuestsList.add(itemKey)
                                }
                            }

                            //Later go to possibleLabels to create new quest for User
                            possibleQuestsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    var requestedQuests = 0

                                    fun getRandomQuest() {
                                        val labelsCount = dataSnapshot.childrenCount
                                        val randomQuestIdx = Random.nextInt(0, labelsCount.toInt() - 1)

                                        val iterator = dataSnapshot.children.iterator()
                                        for (i in 0..randomQuestIdx) {
                                            if (i == randomQuestIdx) {
                                                break
                                            }

                                            iterator.next()
                                        }

                                        val childSnap = iterator.next() as DataSnapshot
                                        if (!userQuestsList.contains(childSnap.key)) {
                                            val questAward = Random.nextInt(
                                                CommonConstants.MINIMUM_QUEST_REWARD,
                                                CommonConstants.MAXIMUM_QUEST_REWARD
                                            )

                                            writeQuestToUserQuests(
                                                Quest(childSnap.key!!, questAward),
                                                currentUser
                                            )
                                        } else {
                                            getRandomQuest()
                                        }
                                    }

                                    while (requestedQuests < amount && userQuestsList.size + requestedQuests < CommonConstants.MAXIMUM_QUESTS_FOR_USER){
                                        getRandomQuest()
                                        requestedQuests++
                                    }

                                    setRequestQuestTime(Date().time)
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e(TAG, "${databaseError.message} requestQuest")
                                }
                            })
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, "${databaseError.message} requestQuest")
                        }
                    })
            }
        }

        fun writeQuestToUserQuests(quest: Quest, user: FirebaseUser) {
            database.child(TABLE_USERS).child(user.uid).child(TABLE_USER_QUESTS)
                .child(quest.item_to_search).setValue(quest.reward)
        }

        fun setLastCompletedQuestListener(listener: ValueEventListener) {
            val activeUser = AuthUtils.getCurrentUser()

            if (activeUser != null) {
                database.child(TABLE_USERS).child(activeUser.uid).child(FIELD_LAST_REQUEST_QUEST_TIME)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                database.child(TABLE_USERS).child(activeUser.uid).child(FIELD_LAST_REQUEST_QUEST_TIME)
                                    .setValue(Date().time)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d(TAG, "${databaseError.message} writeQuestToUserQuests")
                        }
                    })

                database.child(TABLE_USERS).child(activeUser.uid).child(FIELD_LAST_REQUEST_QUEST_TIME)
                    .addValueEventListener(listener)
            }
        }

        //Return type says is user exist & listener sets successfully
        fun setQuestsListener(listener: ChildEventListener): Boolean {
            val currentUser = AuthUtils.getCurrentUser()

            if (currentUser != null) {
                database.child(TABLE_USERS).child(currentUser.uid).child(TABLE_USER_QUESTS)
                    .addChildEventListener(listener)

                return true
            }

            return false
        }

        fun resetQuestsListener(listener: ChildEventListener) {
            val currentUser = AuthUtils.getCurrentUser()

            if (currentUser != null) {
                database.child(TABLE_USERS).child(currentUser.uid).child(TABLE_USER_QUESTS)
                    .removeEventListener(listener)
            }
        }

        fun setRequestQuestTime(date: Long){
            val currentUser = AuthUtils.getCurrentUser()

            if (currentUser != null) {
                database.child(TABLE_USERS).child(currentUser.uid).child(FIELD_LAST_REQUEST_QUEST_TIME)
                    .setValue(date)
            }
        }
    }
}