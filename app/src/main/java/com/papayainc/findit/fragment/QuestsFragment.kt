package com.papayainc.findit.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.papayainc.findit.R
import com.papayainc.findit.adapter.QuestsAdapter

class QuestsFragment : Fragment(), View.OnClickListener {
    companion object {
        val TAG = "[" + QuestsFragment::class.java.simpleName + "]"

        fun newInstance(): QuestsFragment {
            return QuestsFragment()
        }
    }

    interface Callback {

    }

    private var mCallback: Callback? = null

    private lateinit var mQuestsRecycler: RecyclerView
    private lateinit var mQuestsRecyclerAdapter: RecyclerView.Adapter<*>
    private lateinit var mQuestsRecyclerLayoutManager: RecyclerView.LayoutManager

    fun setCallback(callback: Callback){
        this.mCallback = callback
    }

    fun clearCallback(){
        if (this.mCallback != null){
            this.mCallback = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mQuestsRecycler = view.findViewById(R.id.fragment_quests_recycler)
        mQuestsRecyclerLayoutManager = LinearLayoutManager(context)
//        mQuestsRecyclerAdapter = QuestsAdapter(myDataset)

//        mQuestsRecycler.apply {
//            setHasFixedSize(false)
//            layoutManager = mQuestsRecyclerLayoutManager
//            adapter = mQuestsRecyclerAdapter
//        }
    }

    override fun onClick(v: View?) {
        if (v != null && mCallback != null){
            when (v.id){
                
            }
        }
    }

    private fun getQuestQueryListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "${databaseError.message} getQuestQueryListener")
            }
        }
    }
}