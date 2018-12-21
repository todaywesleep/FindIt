package com.papayainc.findit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.papayainc.findit.fragment.CameraFragment
import com.papayainc.findit.fragment.ProfileFragment

class MainActivityFragmentPagerAdapter(
    fm: FragmentManager,
    val mCameraFragment: CameraFragment,
    val profileFragment: ProfileFragment
) : FragmentStatePagerAdapter(fm) {
    companion object {
        const val CAMERA_FRAGMENT = 0
        const val PROFILE_FRAGMENT = 1
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            CAMERA_FRAGMENT -> {
                mCameraFragment
            }
            else -> {
                profileFragment
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}