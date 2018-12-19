package com.papayainc.findit.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import com.google.firebase.FirebaseApp
import com.papayainc.findit.R
import com.papayainc.findit.fragment.CameraFragment
import com.papayainc.findit.modal.ScanResultModal
import com.papayainc.findit.model.ScanResult

class MainActivity : AppCompatActivity(), CameraFragment.Callback, View.OnClickListener {
    private lateinit var mGetImageCallback: CameraFragment.Callback

    //Views
    private lateinit var scanResultModal: ScanResultModal
    private lateinit var mCameraFragment: CameraFragment
    private lateinit var mSwitchAutoFlash: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        mCameraFragment = CameraFragment.newInstance()
        mGetImageCallback = this
        mCameraFragment.setCallback(mGetImageCallback)

        setContentView(R.layout.activity_camera)
        mSwitchAutoFlash = findViewById(R.id.activity_main_switch_auto_flash)

        bindListeners()

        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.camera_preview_container, mCameraFragment)
                .commit()
        }

        scanResultModal = ScanResultModal(this)
    }

    override fun onGetImage(image: Bitmap, scanResult: ArrayList<ScanResult>) {
        scanResultModal.create()
        scanResultModal.setScanResult(image, scanResult)
        scanResultModal.show()
    }

    override fun onAutoFlashChanged(newState: Boolean) {
        val newBackground = if (newState) {
            R.drawable.background_transparent_corner_full_size
        } else {
            0
        }

        mSwitchAutoFlash.setBackgroundResource(newBackground)
    }

    private fun bindListeners(){
        mSwitchAutoFlash.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null){
            when (v.id){
                R.id.activity_main_switch_auto_flash -> mCameraFragment.switchAitoFlash()
            }
        }
    }
}