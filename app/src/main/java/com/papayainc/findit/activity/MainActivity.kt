package com.papayainc.findit.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.FirebaseApp
import com.papayainc.findit.R
import com.papayainc.findit.fragment.CameraFragment
import com.papayainc.findit.model.ScanResult

class MainActivity : AppCompatActivity() {
    private lateinit var mGetImageCallback: CameraFragment.Callback
    private lateinit var mCameraFragment: CameraFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        mCameraFragment = CameraFragment.newInstance()
        mGetImageCallback = generateGetImageCallback()
        mCameraFragment.setCallback(mGetImageCallback)

        setContentView(R.layout.activity_camera)
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mCameraFragment)
                .commit()
        }
    }

    private fun generateGetImageCallback(): CameraFragment.Callback {
        return object: CameraFragment.Callback {
            override fun onGetImage(image: Bitmap, scanResult: ArrayList<ScanResult>) {
                Log.d("dbg", scanResult.toString())
            }
        }
    }
}