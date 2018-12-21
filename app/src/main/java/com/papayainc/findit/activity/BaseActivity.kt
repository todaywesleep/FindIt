package com.papayainc.findit.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.papayainc.findit.R
import com.papayainc.findit.adapter.DrawerAdapter
import com.papayainc.findit.model.DrawerItem
import com.papayainc.findit.utils.AuthUtils
import com.papayainc.findit.view.LoadingModal

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mainContainer: FrameLayout
    private lateinit var toolbar: Toolbar

    private lateinit var mNavigationView: NavigationView
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mDrawerRecycler: RecyclerView

    private var mLoadingModal: LoadingModal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_base)
        mainContainer = findViewById(R.id.base_main_container)

        setUpToolbar()
        setUpDrawer()
    }

    private fun setUpToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setUpDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.base_navigation_view)
        mDrawerRecycler = findViewById(R.id.drawer_recycler)
        mDrawerRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BaseActivity)

            val drawerAdapter = DrawerAdapter(this@BaseActivity, getDrawerItemsList())
            val drawerCallback = getDrawerCallback()
            if (drawerCallback != null) {
                drawerAdapter.setCallback(drawerCallback)
            }

            adapter = drawerAdapter
        }

        mNavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setContentView(view: View?) {
        mainContainer.addView(view)
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, mainContainer)
    }

    override fun onDestroy() {
        AuthUtils.clearListener()
        super.onDestroy()
    }

    protected fun setToolbarVisibility(isVisible: Boolean) {
        val newVisibilityState = if (isVisible) View.VISIBLE else View.GONE
        toolbar.visibility = newVisibilityState
    }

    protected fun setDrawerState(opened: Boolean) {
        if (opened) {
            mDrawerLayout.openDrawer(GravityCompat.START)
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    protected fun setDrawerGestureState(swipeAllowed: Boolean) {
        val newSwipeState =
            if (swipeAllowed) DrawerLayout.LOCK_MODE_LOCKED_OPEN else DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        mDrawerLayout.setDrawerLockMode(newSwipeState)
    }

    protected fun logout(){
        AuthUtils.authObj.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    protected fun startLoading(){
        if (mLoadingModal == null){
            mLoadingModal = LoadingModal(this)
        }

        mLoadingModal!!.show()
    }

    protected fun finishLoading(){
        if (mLoadingModal != null){
            mLoadingModal!!.dismiss()
            mLoadingModal = null
        }
    }

    open fun getDrawerItemsList(): ArrayList<DrawerItem> {
        return arrayListOf()
    }

    abstract fun getDrawerCallback(): DrawerAdapter.Callback?
}