package com.papayainc.findit.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.papayainc.findit.R
import com.papayainc.findit.adapter.DrawerAdapter
import com.papayainc.findit.constants.DrawerConstants
import com.papayainc.findit.fragment.CameraFragment
import com.papayainc.findit.modal.ScanResultModal
import com.papayainc.findit.model.DrawerItem
import com.papayainc.findit.model.ScanResult
import com.papayainc.findit.utils.AuthUtils
import com.papayainc.findit.utils.FireBaseDatabase

class MainActivity : BaseActivity(), CameraFragment.Callback,
    View.OnClickListener, FireBaseDatabase.Companion.Callback {

    //Views
    private lateinit var scanResultModal: ScanResultModal
    private lateinit var mCameraFragment: CameraFragment
    private lateinit var mSettingsButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)
        mSettingsButton = findViewById(R.id.activity_main_settings)
        mSettingsButton.setOnClickListener(this)

        setUpCameraFragment(savedInstanceState)

        scanResultModal = ScanResultModal(this)
        setToolbarVisibility(false)

        FireBaseDatabase.setCallback(this)
        FireBaseDatabase.isUserExistInDatabase(AuthUtils.authObj?.currentUser?.email ?: "")
    }

    override fun isUserExistInDatabase(isExist: Boolean) {
        if (!isExist)
            logout()
    }

    override fun onGetImage(image: Bitmap, scanResult: ArrayList<ScanResult>) {
        scanResultModal.create()
        scanResultModal.setScanResult(image, scanResult)
        scanResultModal.show()
    }

    override fun onAutoFlashChanged(newState: Boolean) {
        //Callback which calls after cameraPreview fragment successfully change auto flash mode
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.activity_main_settings -> {
                    setDrawerState(true)
                }
            }
        }
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

    private fun setUpCameraFragment(savedInstanceState: Bundle?) {
        mCameraFragment = CameraFragment.newInstance()
        mCameraFragment.setCallback(this)

        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.camera_preview_container, mCameraFragment)
                .commit()
        }
    }
}