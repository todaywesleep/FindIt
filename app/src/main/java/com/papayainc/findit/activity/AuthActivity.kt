package com.papayainc.findit.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.papayainc.findit.R
import com.papayainc.findit.adapter.DrawerAdapter
import com.papayainc.findit.constants.CommonConstants
import com.papayainc.findit.modal.ErrorModal
import com.papayainc.findit.view.MaterialInputField


class AuthActivity : BaseActivity(), View.OnClickListener {
    override fun getDrawerCallback(): DrawerAdapter.Callback? {
        return null
    }

    private lateinit var mLoginInput: MaterialInputField
    private lateinit var mPasswordInput: MaterialInputField
    private lateinit var mLoginButton: MaterialButton
    private lateinit var mRegisterButton: MaterialButton

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        setToolbarVisibility(false)

        mLoginInput = findViewById(R.id.login_login_input)
        mPasswordInput = findViewById(R.id.login_password_input)
        mLoginButton = findViewById(R.id.login_login_button)
        mRegisterButton = findViewById(R.id.login_register_button)

        if (isUserExist()){
            navigateToMainActivity()
        }

        setDrawerGestureState(false)
        setListeners()
    }

    private fun setListeners(){
        mLoginButton.setOnClickListener(this)
        mRegisterButton.setOnClickListener(this)

        mPasswordInput.setFilters(getString(R.string.login_password_error), 4, null, null, true)
        mLoginInput.setFilters(getString(R.string.login_login_error), 1, null, CommonConstants.emailRegex, true)
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

    private fun isUserExist(): Boolean {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth!!.currentUser

        return currentUser != null
    }

    private fun navigateToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun createUser(){
        val isErrorExist = mPasswordInput.isErrorExist() && mLoginInput.isErrorExist()

        if (!isErrorExist){
            auth!!.createUserWithEmailAndPassword(
                mLoginInput.getText(),
                mPasswordInput.getText()
            ).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful){
                    if (isUserExist()){
                        navigateToMainActivity()
                    }
                }else{
                    showError(getString(R.string.login_registration_error))
                }
            }.addOnFailureListener {exception ->
                showError(exception.message ?: getString(R.string.error_unknown))
            }
        }else{
            showError(getString(R.string.login_fix_errors))
        }
    }

    private fun showError(message: String){
        ErrorModal(this, message).show()
    }
}