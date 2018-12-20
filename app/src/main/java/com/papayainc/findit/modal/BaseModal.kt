package com.papayainc.findit.modal

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.papayainc.findit.R

open class BaseModal(context: Context) : Dialog(context){
    private lateinit var contentContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.modal_base)

        if (window != null){
            window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        contentContainer = findViewById(R.id.modal_base_content_container)
    }

    override fun setContentView(view: View?) {
        contentContainer.addView(view)
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, contentContainer)
    }
}