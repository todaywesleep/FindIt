package com.papayainc.findit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.papayainc.findit.R

class TasksFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): TasksFragment {
            return TasksFragment()
        }
    }

    interface Callback {

    }

    private var mCallback: Callback? = null

    fun setCallback(callback: Callback){
        this.mCallback = callback
    }

    fun clearCallback(){
        if (this.mCallback != null){
            this.mCallback = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(v: View?) {
        if (v != null && mCallback != null){
            when (v.id){
                
            }
        }
    }
}