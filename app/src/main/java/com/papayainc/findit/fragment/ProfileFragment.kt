package com.papayainc.findit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.papayainc.findit.R

class ProfileFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    interface Callback {
        fun onLogoutPressed()
    }

    private lateinit var logoutButton: MaterialButton
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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton = view.findViewById(R.id.fragment_profile_logout)
    }

    override fun onClick(v: View?) {
        if (v != null && mCallback != null){
            when (v.id){
                R.id.fragment_profile_logout -> {
                    mCallback!!.onLogoutPressed()
                }
            }
        }
    }
}