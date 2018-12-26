package com.papayainc.findit.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papayainc.findit.R
import com.papayainc.findit.adapter.SettingsDrawerAdapter
import com.papayainc.findit.adapter.MainActivityFragmentPagerAdapter
import com.papayainc.findit.constants.DrawerConstants
import com.papayainc.findit.fragment.CameraFragment
import com.papayainc.findit.fragment.ProfileFragment
import com.papayainc.findit.fragment.QuestsFragment
import com.papayainc.findit.modal.ScanResultModal
import com.papayainc.findit.model.DrawerItem
import com.papayainc.findit.model.ScanResult
import com.papayainc.findit.utils.SharedPrefsUtils

class MainActivity : BaseActivity(),
    CameraFragment.Callback,
    ProfileFragment.Callback {
    companion object {
        const val TAB_PROFILE = R.id.activity_main_menu_profile
        const val TAB_CAMERA = R.id.activity_main_menu_action
        const val TAB_TASKS = R.id.activity_main_menu_tasks
    }

    //Views
    private lateinit var scanResultModal: ScanResultModal

    private lateinit var mCameraFragment: CameraFragment
    private lateinit var mProfileFragment: ProfileFragment
    private lateinit var mQuestsFragment: QuestsFragment

    private lateinit var mFragmentPager: ViewPager
    private lateinit var mFragmentPagerAdapter: MainActivityFragmentPagerAdapter
    private lateinit var mBottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFragmentPager = findViewById(R.id.activity_main_pager)
        mFragmentPager.addOnPageChangeListener(getOnPageChangeListener())

        mBottomNavigationBar = findViewById(R.id.activity_main_bottom_navigation)
        mBottomNavigationBar.setOnNavigationItemSelectedListener(getBottomNavigationItemsListener())

        mCameraFragment = CameraFragment.newInstance()
        mCameraFragment.setCallback(this)
        mProfileFragment = ProfileFragment.newInstance()
        mProfileFragment.setCallback(this)
        mQuestsFragment = QuestsFragment.newInstance()

        mFragmentPagerAdapter = MainActivityFragmentPagerAdapter(
                    supportFragmentManager,
                    mCameraFragment,
                    mProfileFragment,
                    mQuestsFragment)
        mFragmentPager.adapter = mFragmentPagerAdapter
        mFragmentPager.currentItem = MainActivityFragmentPagerAdapter.Companion.Fragments.FRAGMENT_CAMERA.idx

        scanResultModal = ScanResultModal(this)
        setToolbarVisibility(false)
        setDrawerGestureState(false)
    }

    override fun onGetImage(image: Bitmap, scanResult: ArrayList<ScanResult>) {
        scanResultModal.create()
        scanResultModal.setScanResult(image, scanResult)
        scanResultModal.show()
    }

    override fun onAutoFlashChanged(isAutoFlashEnabled: Boolean) {
        setItemSelection(SettingsDrawerAdapter.SETTINGS_DRAWER_AUTO_FLASH, isAutoFlashEnabled)
        SharedPrefsUtils.setCameraFlashMode(isAutoFlashEnabled)
    }

    override fun getDrawerCallback(): SettingsDrawerAdapter.Callback? {
        return object : SettingsDrawerAdapter.Callback {
            override fun onItemSelected(item: DrawerItem) {
                when (item.itemType) {
                    DrawerConstants.AUTO_FLASH -> {
                        mCameraFragment.switchAutoFlash()
                    }

                    else -> return
                }
            }
        }
    }

    override fun onOpenDrawer() {
        setDrawerState(true)
    }

    //Profile fragment section start //
    override fun onLogoutPressed() {
        logout()
    }
    //Profile fragment section end //

    override fun onDestroy() {
        mCameraFragment.clearCallback()
        mProfileFragment.clearCallback()
        super.onDestroy()
    }

    private fun getOnPageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                mBottomNavigationBar.selectedItemId = when (position) {
                    MainActivityFragmentPagerAdapter.Companion.Fragments.FRAGMENT_CAMERA.idx -> TAB_CAMERA
                    MainActivityFragmentPagerAdapter.Companion.Fragments.FRAGMENT_PROFILE.idx -> TAB_PROFILE
                    else -> TAB_TASKS
                }
            }
        }
    }

    private fun getBottomNavigationItemsListener(): BottomNavigationView.OnNavigationItemSelectedListener {
        return BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.activity_main_menu_action -> {
                    mFragmentPager.setCurrentItem(
                        MainActivityFragmentPagerAdapter.Companion.Fragments.FRAGMENT_CAMERA.idx,
                        true
                    )
                }

                R.id.activity_main_menu_profile -> {
                    mFragmentPager.setCurrentItem(
                        MainActivityFragmentPagerAdapter.Companion.Fragments.FRAGMENT_PROFILE.idx,
                        true
                    )
                }

                R.id.activity_main_menu_tasks -> {
                    mFragmentPager.setCurrentItem(
                        MainActivityFragmentPagerAdapter.Companion.Fragments.FRAGMENT_TASKS.idx,
                        true
                    )
                }
            }
            true
        }
    }
}