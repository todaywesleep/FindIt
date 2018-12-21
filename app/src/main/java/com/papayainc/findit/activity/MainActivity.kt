package com.papayainc.findit.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.papayainc.findit.R
import com.papayainc.findit.adapter.DrawerAdapter
import com.papayainc.findit.constants.DrawerConstants
import com.papayainc.findit.fragment.CameraFragment
import com.papayainc.findit.fragment.ProfileFragment
import com.papayainc.findit.modal.ScanResultModal
import com.papayainc.findit.model.DrawerItem
import com.papayainc.findit.model.ScanResult

class MainActivity : BaseActivity(), CameraFragment.Callback, ProfileFragment.Callback {
    //Views
    private lateinit var scanResultModal: ScanResultModal

    private lateinit var mCameraFragment: CameraFragment
    private lateinit var mProfileFragment: ProfileFragment

    private lateinit var mBottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        mBottomNavigationBar = findViewById(R.id.activity_main_bottom_navigation)
        mBottomNavigationBar.setOnNavigationItemSelectedListener(getBottomNavigationItemsListener())

        mCameraFragment = CameraFragment.newInstance()
        mCameraFragment.setCallback(this)
        mProfileFragment = ProfileFragment.newInstance()
        mProfileFragment.setCallback(this)

        setUpCameraFragment(savedInstanceState)

        scanResultModal = ScanResultModal(this)
        setToolbarVisibility(false)
    }

    override fun onGetImage(image: Bitmap, scanResult: ArrayList<ScanResult>) {
        scanResultModal.create()
        scanResultModal.setScanResult(image, scanResult)
        scanResultModal.show()
    }

    override fun onAutoFlashChanged(newState: Boolean) {
        //Callback which calls after cameraPreview fragment successfully change auto flash mode
    }

    override fun getDrawerItemsList(): ArrayList<DrawerItem> {
        return arrayListOf(
            DrawerItem(
                getString(R.string.drawer_auto_flash),
                R.drawable.ic_flash_auto,
                DrawerConstants.AUTO_FLASH,
                false
            )
        )
    }

    override fun getDrawerCallback(): DrawerAdapter.Callback? {
        return object : DrawerAdapter.Callback {
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

    private fun setUpCameraFragment(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_main_content_container, mCameraFragment)
                .commit()
        }
    }

    private fun getBottomNavigationItemsListener(): BottomNavigationView.OnNavigationItemSelectedListener {
        return BottomNavigationView.OnNavigationItemSelectedListener {
            val newFragment = when (it.itemId) {
                R.id.activity_main_menu_action -> {
                    mCameraFragment
                }
                R.id.activity_main_menu_profile -> {
                    mProfileFragment
                }
                else -> null
            }

            if (newFragment != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.activity_main_content_container,
                    newFragment as Fragment
                ).commit()
            }
            true
        }
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
}