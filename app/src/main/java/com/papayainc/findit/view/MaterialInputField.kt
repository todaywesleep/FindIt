package com.papayainc.findit.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.papayainc.findit.R


class MaterialInputField : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        unpackHint(attrs, context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        unpackHint(attrs, context)
    }

    private val mTextInputLayout: TextInputLayout
    private val mTextInputField: TextInputEditText

    private var errorMessage: String = ""

    private var minLength: Int? = null
    private var maxLength: Int? = null
    private var regex: String? = null
    private var instantUpdate: Boolean = false

    private fun setWatchers() {
        if (minLength != null || maxLength != null || regex != null) {
            mTextInputField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()

                    if (instantUpdate) {
                        setErrors(text)
                    }else{
                        if (mTextInputLayout.error != null){
                            mTextInputLayout.error = null
                            return
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun setErrors(text: String){
        var isErrorExist = false

        if (minLength != null) {
            isErrorExist = text.length < minLength!!
        }

        if (maxLength != null && !isErrorExist) {
            isErrorExist = text.length > maxLength!!
        }

        if (regex != null && !isErrorExist) {
            isErrorExist = !text.matches(regex!!.toRegex())
        }

        if (isErrorExist){
            mTextInputLayout.error = errorMessage
        }else{
            mTextInputLayout.error = null
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_input_field, this, true)

        mTextInputLayout = findViewById(R.id.view_input_field_input_layout)
        mTextInputField = findViewById(R.id.view_input_field_input)
    }

    private fun unpackHint(attrs: AttributeSet?, context: Context) {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MaterialInputField, 0, 0
            ).apply {
                mTextInputLayout.hint = context.getString(getResourceId(R.styleable.MaterialInputField_hint, -1))
                mTextInputField.inputType =
                        getInteger(R.styleable.MaterialInputField_android_inputType, InputType.TYPE_CLASS_TEXT)
            }
        }
    }

    fun getText(): String {
        return mTextInputField.text.toString()
    }

    fun isErrorExist(): Boolean{
        setErrors(mTextInputField.text.toString())
        return mTextInputLayout.error != null
    }

    fun setFilters(errorMessage: String, minLength: Int?, maxLength: Int?, regex: String?, instantUpdate: Boolean) {
        this.errorMessage = errorMessage
        this.minLength = minLength
        this.maxLength = maxLength
        this.regex = regex
        this.instantUpdate = instantUpdate

        setWatchers()
    }
}