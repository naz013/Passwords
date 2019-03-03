package com.cray.software.passwords

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.util.Log
import android.view.View
import android.widget.Toast

import com.cray.software.passwords.databinding.ActivityMainBinding
import com.cray.software.passwords.fragments.BaseFragment
import com.cray.software.passwords.fragments.FragmentInterface
import com.cray.software.passwords.fragments.NestedFragment
import com.cray.software.passwords.helpers.SyncHelper
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.notes.NotesFragment
import com.cray.software.passwords.passwords.PasswordsFragment
import com.cray.software.passwords.settings.SettingsFragment
import com.cray.software.passwords.tasks.BackupTask
import com.cray.software.passwords.tasks.DelayedTask
import com.cray.software.passwords.tasks.SyncTask
import com.cray.software.passwords.utils.Dialogues
import com.cray.software.passwords.utils.Prefs
import com.cray.software.passwords.utils.ThemeUtil
import com.cray.software.passwords.utils.ThemedActivity
import com.cray.software.passwords.utils.ViewUtils
import com.roughike.bottombar.BottomBarTab

import java.io.File

class MainActivity : ThemedActivity(), FragmentInterface, FragmentManager.OnBackStackChangedListener {

    private var binding: ActivityMainBinding? = null
    private var mFragment: BaseFragment? = null

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<T>(this, R.layout.activity_main)

        setSupportActionBar(binding!!.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
        replaceFragment(HomeFragment.newInstance(), HomeFragment.TAG)

        initBottomBar()
        showRate()
    }

    private fun initBottomBar() {
        val themeUtil = themeUtil
        for (i in 0 until binding!!.bottomBar.tabCount) {
            val tab = binding!!.bottomBar.getTabAtPosition(i)
            if (themeUtil != null) {
                tab.activeColor = themeUtil.getColor(themeUtil.colorAccent())
                tab.inActiveColor = themeUtil.getColor(themeUtil.colorPrimary())
                tab.barColorWhenSelected = themeUtil.backgroundStyle
            }
        }
        binding!!.bottomBar.setDefaultTabPosition(0)
        binding!!.bottomBar.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_home -> replaceFragment(HomeFragment.newInstance(), HomeFragment.TAG)
                R.id.tab_passwords -> replaceFragment(PasswordsFragment.newInstance(), PasswordsFragment.TAG)
                R.id.tab_notes -> replaceFragment(NotesFragment.newInstance(), NotesFragment.TAG)
                R.id.tab_settings -> replaceFragment(SettingsFragment.newInstance(), SettingsFragment.TAG)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        delayedThreads()
    }

    override fun onDestroy() {
        super.onDestroy()
        autoBackup()
        syncPrefs()
    }

    private fun autoBackup() {
        if (Module.isPro) {
            if (Prefs.getInstance(this).isAutoBackupEnabled) {
                BackupTask(this@MainActivity).execute()
            }
            if (Prefs.getInstance(this).isAutoSyncEnabled) {
                SyncTask(this@MainActivity, null).execute()
            }
        }
    }

    private fun showRate() {
        val prefs = Prefs.getInstance(this)
        if (!prefs.isRateShowed) {
            val counts = prefs.runsCount
            if (counts < 10) {
                prefs.runsCount = counts + 1
            } else {
                prefs.runsCount = 0
                Dialogues.showRateDialog(this)
            }
        }
    }

    private fun delayedThreads() {
        DelayedTask(this).execute()
    }

    override fun onBackPressed() {
        if (!mFragment!!.canGoBack()) return
        if (mFragment is NestedFragment) {
            super.onBackPressed()
        } else {
            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.back_button_toast), Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(tag)
                .commitAllowingStateLoss()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        clearBackStack()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(tag)
                .commitAllowingStateLoss()
    }

    override fun setCurrent(fragment: BaseFragment) {
        mFragment = fragment
    }

    override fun setTitle(title: String) {
        binding!!.toolbar.setTitle(title)
    }

    override fun setClick(clickListener: View.OnClickListener?) {
        if (clickListener != null) {
            binding!!.fab.setOnClickListener(clickListener)
            ViewUtils.show(binding!!.fab, null)
        } else {
            ViewUtils.hide(binding!!.fab, null)
        }
    }

    override fun moveBack() {
        onBackPressed()
    }

    override fun openScreen(fragment: BaseFragment, tag: String) {
        addFragment(fragment, tag)
    }

    override fun onBackStackChanged() {
        if (supportFragmentManager == null) return
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        Log.d(TAG, "onBackStackChanged: current: " + fragment!!)
        if (fragment != null && fragment is BaseFragment) {
            fragment.onFragmentResume()
        }
        val count = supportFragmentManager.backStackEntryCount
        if (count >= 2) {
            val f = supportFragmentManager.fragments[count - 2]
            if (f != null && f is BaseFragment) {
                f.onFragmentPause()
            }
        }
    }

    private fun clearBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mFragment != null) mFragment!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun syncPrefs() {
        val isSD = SyncHelper.isSdPresent
        if (isSD) {
            val sdPath = Environment.getExternalStorageDirectory()
            val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.PREFS)
            if (!sdPathDr.exists()) {
                sdPathDr.mkdirs()
            }
            val prefs = File("$sdPathDr/prefs.xml")
            if (prefs.exists()) {
                prefs.delete()
            }
            Prefs.getInstance(this).saveSharedPreferencesToFile(prefs)
        }
    }

    companion object {

        private val TAG = "MainActivity"
    }
}
