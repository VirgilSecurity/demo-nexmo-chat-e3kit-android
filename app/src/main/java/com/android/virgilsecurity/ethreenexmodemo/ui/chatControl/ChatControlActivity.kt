package com.android.virgilsecurity.ethreenexmodemo.ui.chatControl

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.android.virgilsecurity.ethreenexmodemo.data.local.Preferences
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.addThread.AddThreadFragment
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.threadsList.ThreadsListFragment
import com.android.virgilsecurity.ethreenexmodemo.ui.signUp.SignUpActivity
import com.nexmo.client.NexmoClient
import kotlinx.android.synthetic.main.activity_chat_control.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import android.support.v7.app.ActionBarDrawerToggle
import com.android.virgilsecurity.ethreenexmodemo.R
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.thread.ThreadFragment


/**
 * ChatControlActivity
 */
class ChatControlActivity : AppCompatActivity() {

    private val preferences: Preferences by lazy { Preferences(this) }
    private var doubleBack = false
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_control)

        nvBase.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            dlBase.closeDrawers()

            handleDrawerClick(item)

            true
        }

        nvBase.getHeaderView(0).ivDrawerHeaderName.text = preferences.username()

        changeFragment(ThreadsListFragment.instance())
        nvBase.setCheckedItem(0)

        setSupportActionBar(toolbar)
        toggle = object :
            ActionBarDrawerToggle(this, dlBase, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {}
        dlBase.addDrawerListener(toggle)
    }

    fun showBackButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
    }

    fun showDrawerButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        toggle.syncState()
    }

    private fun handleDrawerClick(item: MenuItem) {
        when (item.itemId) {
            R.id.itemDrawerThreadsList -> {
                changeFragment(ThreadsListFragment.instance())
            }
            R.id.itemDrawerAddThread -> {
                changeFragment(AddThreadFragment.instance())
            }
            R.id.itemDrawerSignOut -> {
                preferences.clearAuthToken()
                preferences.clearNexmoToken()
                preferences.clearVirgilToken()
                preferences.clearUsername()
                NexmoClient.get().logout()

                SignUpActivity.start(this)
            }
        }
    }

    fun changeFragment(fragment: Fragment) {
        when (fragment) {
            is AddThreadFragment -> supportFragmentManager.beginTransaction()
                .addToBackStack(ADD_THREAD_TAG)
                .replace(R.id.flContainer, fragment)
                .commit()
            is ThreadFragment -> supportFragmentManager.beginTransaction()
                .addToBackStack(THREAD_TAG)
                .replace(R.id.flContainer, fragment)
                .commit()
            else -> supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            super.onBackPressed()
            showDrawerButton()
        } else {
            if (doubleBack) {
                finish()
            } else {
                doubleBack = true
                Handler().postDelayed({
                    doubleBack = false
                }, DOUBLE_BACK_DELAY)
                Toast.makeText(this, "Press back once more to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleHomeClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleHomeClick() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            dlBase.openDrawer(GravityCompat.START)
    }

    companion object {
        private const val ADD_THREAD_TAG = "ADD_THREAD_TAG"
        private const val THREAD_TAG = "THREAD_TAG"
        private const val DOUBLE_BACK_DELAY = 2000L

        fun start(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, ChatControlActivity::class.java))
            activity.finish()
        }
    }
}