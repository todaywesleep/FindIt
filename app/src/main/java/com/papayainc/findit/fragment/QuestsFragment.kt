package com.papayainc.findit.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.papayainc.findit.R
import com.papayainc.findit.adapter.QuestsAdapter
import com.papayainc.findit.model.Quest
import com.papayainc.findit.utils.FireBaseDataBaseWorker

class QuestsFragment : Fragment(), View.OnClickListener {
    companion object {
        val TAG = "[" + QuestsFragment::class.java.simpleName + "]"

        fun newInstance(): QuestsFragment {
            return QuestsFragment()
        }
    }

    interface Callback {}

    private var mCallback: Callback? = null

    private lateinit var mQuestsRecycler: RecyclerView
    private lateinit var mQuestsRecyclerAdapter: QuestsAdapter
    private lateinit var mQuestsRecyclerLayoutManager: RecyclerView.LayoutManager
    private lateinit var mQuestsQueryListener: ChildEventListener

    private var questList: ArrayList<Quest> = arrayListOf()

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    fun clearCallback() {
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
        FireBaseDataBaseWorker.setQuestsListener(mQuestsQueryListener)

        mQuestsRecycler = view.findViewById(R.id.fragment_quests_recycler)
        mQuestsRecyclerLayoutManager = LinearLayoutManager(context)
        mQuestsRecyclerAdapter = QuestsAdapter(questList)

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

    private fun getQuestQueryListener(): ChildEventListener {
        return object : ChildEventListener {
            override fun onChildMoved(dataSnapshot: DataSnapshot, key: String?) {}
            override fun onChildChanged(dataSnapshot: DataSnapshot, key: String?) {}
            override fun onChildAdded(dataSnapshot: DataSnapshot, key: String?) {
                mQuestsRecyclerAdapter.addItem(
                    Quest(dataSnapshot.key!!, dataSnapshot.value.toString().toInt())
                )
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                mQuestsRecyclerAdapter.removeItem(
                    Quest(dataSnapshot.key!!, dataSnapshot.value.toString().toInt())
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "${databaseError.message} getQuestQueryListener")
            }
        }
    }

    override fun onDestroy() {
        FireBaseDataBaseWorker.resetQuestsListener(mQuestsQueryListener)

        super.onDestroy()
    }
}