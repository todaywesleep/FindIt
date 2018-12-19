package com.papayainc.findit.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.papayainc.findit.R

abstract class BaseActivity: AppCompatActivity() {
    private lateinit var mainContainer: FrameLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_base)
        mainContainer = findViewById(R.id.base_main_container)
        toolbar = findViewById(R.id.toolbar)
    }

    override fun setContentView(view: View?) {
        mainContainer.addView(view)
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, mainContainer)
    }
}