package com.papayainc.findit.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.papayainc.findit.R
import com.papayainc.findit.adapter.DrawerAdapter
import com.papayainc.findit.model.DrawerItem

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mainContainer: FrameLayout
    private lateinit var toolbar: Toolbar

    private lateinit var mNavigationView: NavigationView
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mDrawerList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_base)
        mainContainer = findViewById(R.id.base_main_container)

        setUpToolbar()
        setUpDrawer()
    }

    private fun setUpToolbar(){
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
        mDrawerList = findViewById(R.id.left_drawer)
        mNavigationView = findViewById(R.id.base_navigation_view)

        mDrawerList.adapter = DrawerAdapter(this@BaseActivity, getDrawerItemsList())
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

    protected fun setToolbarVisibility(isVisible: Boolean){
        val newVisibilityState = if (isVisible) View.VISIBLE else View.GONE
        toolbar.visibility = newVisibilityState
    }

    protected fun setDrawerState(opened: Boolean){
        if (opened) {
            mDrawerLayout.openDrawer(GravityCompat.START)
        }else{
            mDrawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    protected fun setDrawerGestureState(swipeAllowed: Boolean){
        val newSwipeState = if (swipeAllowed) DrawerLayout.LOCK_MODE_LOCKED_OPEN else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        mDrawerLayout.setDrawerLockMode(newSwipeState)
    }

    open fun getDrawerItemsList(): ArrayList<DrawerItem>{
        return arrayListOf()
    }
}