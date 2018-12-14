package com.papayainc.findit.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.papayainc.findit.R
import com.papayainc.findit.view.CameraFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CameraFragment.newInstance())
                .commit()
        }
    }
}