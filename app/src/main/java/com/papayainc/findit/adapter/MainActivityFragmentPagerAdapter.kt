package com.papayainc.findit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.papayainc.findit.fragment.CameraFragment
import com.papayainc.findit.fragment.ProfileFragment
import com.papayainc.findit.fragment.QuestsFragment

class MainActivityFragmentPagerAdapter(
    fm: FragmentManager,
    val mCameraFragment: CameraFragment,
    val mProfileFragment: ProfileFragment,
    val mQuestsFragment: QuestsFragment
) : FragmentStatePagerAdapter(fm) {
    companion object {
        enum class Fragments (val idx: Int) {
            FRAGMENT_PROFILE(0),
            FRAGMENT_CAMERA(1),
            FRAGMENT_TASKS(2)
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            Fragments.FRAGMENT_PROFILE.idx -> {
                mProfileFragment
            }
            Fragments.FRAGMENT_CAMERA.idx -> {
                mCameraFragment
            }
            else -> {
                mQuestsFragment
            }
        }
    }

    override fun getCount(): Int {
        return Fragments.values().size
    }
}