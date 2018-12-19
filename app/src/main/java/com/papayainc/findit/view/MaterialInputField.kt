package com.papayainc.findit.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.papayainc.findit.R


class MaterialInputField : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mTextInputField.hint = unpackHint(attrs, context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mTextInputField.hint = unpackHint(attrs, context)
    }

    private val mTextInputLayout: TextInputLayout
    private val mTextInputField: TextInputEditText

    init {
        LayoutInflater.from(context).inflate(R.layout.view_input_field, this, true)

        mTextInputLayout = findViewById(R.id.view_input_field_input_layout)
        mTextInputField = findViewById(R.id.view_input_field_input)
    }

    private fun unpackHint(attrs: AttributeSet?, context: Context): String {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MaterialInputField, 0, 0
            ).apply {
                return context.getString(getResourceId(R.styleable.MaterialInputField_hint, -1))
            }
        }

        return ""
    }
}