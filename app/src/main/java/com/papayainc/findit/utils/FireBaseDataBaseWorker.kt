package com.papayainc.findit.utils

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.papayainc.findit.constants.CommonConstants
import com.papayainc.findit.model.Quest
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class FireBaseDataBaseWorker {
    companion object {
        val TAG = "[" + FireBaseDataBaseWorker::class.java.simpleName + "]"

        private const val TABLE_POSSIBLE_LABELS = "possible_labels"
        private const val TABLE_USERS = "users"
        private const val TABLE_USER_QUESTS = "quests"
        private const val TABLE_COMPLETED_QUESTS = "completed_quests"

        private const val FIELD_USER_EXPERIENCE = "experience"
        private const val FIELD_USER_CREDITS = "credits"
        private const val FIELD_LEVEL = "level"
        private const val FIELD_LAST_REQUEST_QUEST_TIME = "last_request_quest_time"

        private val database = FirebaseDatabase.getInstance().reference

        interface IsQuestCompletedCallback{
            fun isQuestCompleted(isCompleted: Boolean, quests: ArrayList<Quest>?)
        }

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
                        userDatabaseReference.child(FIELD_USER_EXPERIENCE).setValue(0)
                        userDatabaseReference.child(FIELD_USER_CREDITS).setValue(0)
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
                val completedQuestsReference = database.child(TABLE_USERS).child(currentUser.uid)
                    .child(TABLE_COMPLETED_QUESTS)

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

                            //And also get completed quests collection
                            completedQuestsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val completedQuestsMap = hashMapOf<String, String>()

                                    if (!dataSnapshot.exists()){
                                        completedQuestsReference.setValue(completedQuestsMap)
                                    }else{
                                        val completedQuestsIterator = dataSnapshot.children.iterator()
                                        while (completedQuestsIterator.hasNext()){
                                            val child = completedQuestsIterator.next()
                                            completedQuestsMap[child.key!!] = child.value.toString()
                                        }
                                    }
                                    //Set new completed quests counter
                                    SharedPrefsUtils.setCompletedQuestsAmount(completedQuestsMap.size)
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

                                            var userQuestsSize = userQuestsList.size
                                            while (requestedQuests < amount && userQuestsSize + requestedQuests < CommonConstants.MAXIMUM_QUESTS_FOR_USER) {
                                                getRandomQuest()
                                                requestedQuests++
                                                userQuestsSize++
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

        fun resetLastCompletedQuestListener(listener: ValueEventListener){
            val activeUser = AuthUtils.getCurrentUser()

            if (activeUser != null){
                database.child(TABLE_USERS).child(activeUser.uid).child(FIELD_LAST_REQUEST_QUEST_TIME)
                    .removeEventListener(listener)
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

        fun setRequestQuestTime(date: Long) {
            val currentUser = AuthUtils.getCurrentUser()

            if (currentUser != null) {
                database.child(TABLE_USERS).child(currentUser.uid).child(FIELD_LAST_REQUEST_QUEST_TIME)
                    .setValue(date)
            }
        }

        //Here we take scanned item name and compare it with actual quests list
        fun getCompletedQuest(items: ArrayList<String>, callback: IsQuestCompletedCallback) {
            val currentUser = AuthUtils.getCurrentUser()

            if (currentUser != null){
                database.child(TABLE_USERS).child(currentUser.uid).child(TABLE_USER_QUESTS)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()){
                                val completedQuests = arrayListOf<Quest>()
                                var creditsToAdd = 0
                                val iterator = dataSnapshot.children.iterator()

                                while (iterator.hasNext()){
                                    val currentItem = iterator.next()

                                    if (items.contains(currentItem.key)){
                                        if (currentItem.exists() && currentItem.key != null){
                                            val creditsForQuest = currentItem.value.toString().toInt()
                                            creditsToAdd += creditsForQuest
                                            completedQuests.add(Quest(currentItem.key.toString(), creditsForQuest))

                                            database.child(TABLE_USERS).child(currentUser.uid).child(TABLE_USER_QUESTS)
                                                .child(currentItem.key.toString()).removeValue()
                                        }
                                    }
                                }

                                if (creditsToAdd > 0){
                                    database.child(TABLE_USERS).child(currentUser.uid).child(FIELD_USER_CREDITS)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                val oldValue = dataSnapshot.value?.toString()?.toInt() ?: 0

                                                database.child(TABLE_USERS).child(currentUser.uid).child(FIELD_USER_CREDITS).setValue(oldValue + creditsToAdd)
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                Log.e(TAG, "${databaseError.message} getCompletedQuest $FIELD_USER_CREDITS")
                                            }
                                        })


                                }
                            }else{
                                callback.isQuestCompleted(false, null)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, "${databaseError.message} getCompletedQuest")
                        }
                    })
            }
        }
    }
}