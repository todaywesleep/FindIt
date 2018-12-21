package com.papayainc.findit.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.papayainc.findit.R
import com.papayainc.findit.adapter.DrawerAdapter
import com.papayainc.findit.constants.CommonConstants
import com.papayainc.findit.modal.ErrorModal
import com.papayainc.findit.utils.AuthUtils
import com.papayainc.findit.utils.AuthUtils.Companion.authObj
import com.papayainc.findit.view.MaterialInputField


class AuthActivity : BaseActivity(), View.OnClickListener, AuthUtils.Companion.Callback {
    override fun getDrawerCallback(): DrawerAdapter.Callback? {
        //Here is no drawer, just do nothing
        return null
    }

    private lateinit var mLoginInput: MaterialInputField
    private lateinit var mPasswordInput: MaterialInputField
    private lateinit var mLoginButton: MaterialButton
    private lateinit var mRegisterButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        setToolbarVisibility(false)

        mLoginInput = findViewById(R.id.login_login_input)
        mPasswordInput = findViewById(R.id.login_password_input)
        mLoginButton = findViewById(R.id.login_login_button)
        mRegisterButton = findViewById(R.id.login_register_button)

        setDrawerGestureState(false)
        setListeners()
        renewSession()
    }

    private fun setListeners() {
        mLoginButton.setOnClickListener(this)
        mRegisterButton.setOnClickListener(this)

        mPasswordInput.setFilters(getString(R.string.login_password_error), 6, null, null, true)
        mLoginInput.setFilters(getString(R.string.login_login_error), 1, null, CommonConstants.emailRegex, true)
    }

    override fun isSessionResumed(b: Boolean) {
        if (b){
            navigateToMainActivity()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.login_login_button -> {
                    navigateToMainActivity()
                }

                R.id.login_register_button -> {
                    createUser()
                }
            }
        }
    }

    private fun renewSession() {
        AuthUtils.setCallback(this)
        AuthUtils.renewSession()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun createUser() {
        val isErrorExist = mPasswordInput.isErrorExist() && mLoginInput.isErrorExist()

        if (!isErrorExist) {
            startLoading()

            authObj.createUserWithEmailAndPassword(
                mLoginInput.getText(),
                mPasswordInput.getText()
            ).addOnCompleteListener { task: Task<AuthResult> ->
                finishLoading()
                if (task.isSuccessful) {
                    navigateToMainActivity()
                }
            }.addOnFailureListener { exception ->
                finishLoading()
                showError(exception.message ?: getString(R.string.error_unknown))
            }
        } else {
            showError(getString(R.string.login_fix_errors))
        }
    }

    private fun showError(message: String) {
        ErrorModal(this, message).show()
    }
}