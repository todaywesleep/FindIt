package com.papayainc.findit.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.papayainc.findit.R
import com.papayainc.findit.adapter.QuestsAdapter
import com.papayainc.findit.constants.CommonConstants
import com.papayainc.findit.model.Quest
import com.papayainc.findit.utils.CommonUtils
import com.papayainc.findit.utils.FireBaseDataBaseWorker
import java.util.*


class QuestsFragment : Fragment(), View.OnClickListener, QuestsAdapter.Callback {
    companion object {
        val TAG = "[" + QuestsFragment::class.java.simpleName + "]"

        fun newInstance(): QuestsFragment {
            return QuestsFragment()
        }
    }

    interface Callback {}
    private var mCallback: Callback? = null

    private lateinit var mQuestsRecycler: RecyclerView
    private lateinit var mQuestsCountLabel: TextView
    private lateinit var mTimeToNewQuestLabel: TextView
    private lateinit var mQuestsRecyclerAdapter: QuestsAdapter
    private lateinit var mQuestsRecyclerLayoutManager: RecyclerView.LayoutManager

    private lateinit var mQuestsQueryListener: ChildEventListener
    private lateinit var mTimeToNewQuestListener: ValueEventListener

    private var isQuestsFilled = false

    private var mTimer: Handler? = null
    private var mTimerTask: Runnable? = null

    private var questList: ArrayList<Quest> = arrayListOf()

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    private fun clearCallback() {
        if (this.mCallback != null) {
            this.mCallback = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mQuestsQueryListener = getQuestQueryListener()
        mTimeToNewQuestListener = getOnTimeToNewQuestListener()
        FireBaseDataBaseWorker.setQuestsListener(mQuestsQueryListener)
        FireBaseDataBaseWorker.setLastCompletedQuestListener(mTimeToNewQuestListener)

        mQuestsCountLabel = view.findViewById(R.id.fragment_quests_quests_amount)
        mTimeToNewQuestLabel = view.findViewById(R.id.fragment_quests_time_to_new_quest)
        mQuestsRecycler = view.findViewById(R.id.fragment_quests_recycler)
        mQuestsRecyclerLayoutManager = LinearLayoutManager(context)
        mQuestsRecyclerAdapter = QuestsAdapter(questList)
        mQuestsRecyclerAdapter.setCallback(this)
        setQuestsCount(mQuestsRecyclerAdapter.itemCount)

        mQuestsRecycler.apply {
            setHasFixedSize(false)
            layoutManager = mQuestsRecyclerLayoutManager
            adapter = mQuestsRecyclerAdapter
        }
    }

    override fun onClick(v: View?) {
        if (v != null && mCallback != null) {
            when (v.id) {

            }
        }
    }

    override fun onItemsCountChange(newCount: Int) {
        setQuestsCount(newCount)

        if (newCount == CommonConstants.MAXIMUM_QUESTS_FOR_USER) {
            isQuestsFilled = true
            stopQuestsTimerAndSetLabel()
        } else {
            isQuestsFilled = false
        }
    }

    override fun onDestroy() {
        FireBaseDataBaseWorker.resetQuestsListener(mQuestsQueryListener)
        mQuestsRecyclerAdapter.clearCallback()
        clearCallback()

        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        mQuestsRecyclerAdapter.clearData()
    }

    private fun getQuestQueryListener(): ChildEventListener {
        return object : ChildEventListener {
            override fun onChildMoved(dataSnapshot: DataSnapshot, key: String?) {}
            override fun onChildChanged(dataSnapshot: DataSnapshot, key: String?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, key: String?) {
                if (dataSnapshot.exists()) {
                    mQuestsRecyclerAdapter.addItem(
                        Quest(
                            dataSnapshot.key!!,
                            dataSnapshot.value.toString().toInt()
                        )
                    )
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    mQuestsRecyclerAdapter.removeItem(
                        Quest(
                            dataSnapshot.key!!,
                            dataSnapshot.value.toString().toInt()
                        )
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "${databaseError.message} getQuestQueryListener")
            }
        }
    }

    private fun setQuestsCount(count: Int) {
        if (isVisible) {
            mQuestsCountLabel.text = resources.getString(
                R.string.fragment_quests_available_quests,
                count,
                CommonConstants.MAXIMUM_QUESTS_FOR_USER
            )
        }
    }

    private fun getOnTimeToNewQuestListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lastRequestQuestDate = dataSnapshot.value as Long
                val minutesBetweenDates = CommonUtils.getMinutesBetweenDates(Date().time, lastRequestQuestDate)
                val questsToRequest = minutesBetweenDates / 60

                if (questsToRequest > 0) {
                    FireBaseDataBaseWorker.requestQuest(questsToRequest.toInt())
                }

                if (!isQuestsFilled) {
                    runQuestTimeHandler(dataSnapshot.value.toString().toLong())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "${databaseError.message} getOnTimeToNewQuestListener")
            }
        }
    }

    private fun runQuestTimeHandler(lastQuestDate: Long) {
        val delay = 1000L //milliseconds

        mTimer = Handler()
        mTimerTask = object : Runnable {
            var dateToSubtract = CommonConstants.TIME_HOUR - (Date().time - lastQuestDate)

            override fun run() {
                val seconds = dateToSubtract / 1000
                val minutes = seconds / 60 % 60
                val hours = minutes / 60 % 60

                dateToSubtract -= 1000
                setDateToQuest(hours, minutes, seconds % 60)

                if (dateToSubtract <= 0){
                    FireBaseDataBaseWorker.requestQuest(1)
                }

                mTimer!!.postDelayed(this, delay)
            }
        }

        mTimer!!.postDelayed(mTimerTask, delay)
    }

    private fun setDateToQuest(hours: Long, minutes: Long, seconds: Long) {
        if (isVisible) {
            mTimeToNewQuestLabel.text =
                    resources.getString(
                        R.string.fragment_quests_time_to_new_quest,
                        hours,
                        minutes,
                        seconds
                    )
        }
    }

    private fun stopQuestsTimerAndSetLabel() {
        if (mTimer != null) {
            mTimer!!.removeCallbacks(mTimerTask)
            mTimer = null
        }

        if (isVisible) {
            mTimeToNewQuestLabel.text = getString(R.string.fragment_quests_you_have_max_quests)
        }
    }
}