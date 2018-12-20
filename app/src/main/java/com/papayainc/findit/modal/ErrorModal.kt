package com.papayainc.findit.modal

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.papayainc.findit.R

class ErrorModal(context: Context, private val message: String): BaseModal(context){
    private lateinit var errorTextLabel: TextView
    private lateinit var confirmButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modal_error)

        errorTextLabel = findViewById(R.id.modal_error_text)
        errorTextLabel.text = message
        confirmButton = findViewById(R.id.modal_error_confirm_button)
        confirmButton.setOnClickListener {
            dismiss()
        }
    }
}