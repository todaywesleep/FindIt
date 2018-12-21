package com.papayainc.findit.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.papayainc.findit.R

class LoadingModal(context: Context): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modal_loading)

        setCancelable(false)

        if (window != null){
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}