package com.cray.software.passwords.dialogs

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout

import com.cray.software.passwords.R
import com.cray.software.passwords.helpers.Utils
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.utils.Prefs
import com.cray.software.passwords.utils.ThemedActivity

class ThemeActivity : ThemedActivity() {

    private var red: ImageButton? = null
    private var green: ImageButton? = null
    private var blue: ImageButton? = null
    private var yellow: ImageButton? = null
    private var greenLight: ImageButton? = null
    private var blueLight: ImageButton? = null
    private var cyan: ImageButton? = null
    private var purple: ImageButton? = null
    private var amber: ImageButton? = null
    private var orange: ImageButton? = null
    private var pink: ImageButton? = null
    private var teal: ImageButton? = null
    private var deepPurple: ImageButton? = null
    private var deepOrange: ImageButton? = null
    private var indigo: ImageButton? = null
    private var lime: ImageButton? = null
    private var mFab: FloatingActionButton? = null

    private var toolbar: Toolbar? = null
    private var prevId: Int = 0

    private var isChanged = false

    private val listener = { v -> themeColorSwitch(v.getId()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.theme_color_layout)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar!!.title = getString(R.string.app_theme_title)

        red = findViewById(R.id.red_checkbox)
        purple = findViewById(R.id.violet_checkbox)
        green = findViewById(R.id.green_checkbox)
        greenLight = findViewById(R.id.light_green_checkbox)
        blue = findViewById(R.id.blue_checkbox)
        blueLight = findViewById(R.id.light_blue_checkbox)
        yellow = findViewById(R.id.yellow_checkbox)
        orange = findViewById(R.id.orange_checkbox)
        cyan = findViewById(R.id.grey_checkbox)
        pink = findViewById(R.id.pink_checkbox)
        teal = findViewById(R.id.sand_checkbox)
        amber = findViewById(R.id.brown_checkbox)

        deepPurple = findViewById(R.id.deepPurple)
        indigo = findViewById(R.id.indigoCheckbox)
        lime = findViewById(R.id.limeCheckbox)
        deepOrange = findViewById(R.id.deepOrange)

        val themeGroupPro = findViewById<LinearLayout>(R.id.themeGroupPro)
        if (Module.isPro) {
            themeGroupPro.visibility = View.VISIBLE
        } else {
            themeGroupPro.visibility = View.GONE
        }

        setOnClickListener(red, green, blue, yellow, greenLight, blueLight, cyan, purple,
                amber, orange, pink, teal, deepPurple, deepOrange, indigo, lime)
        setUpRadio()

        mFab = findViewById(R.id.fab)
        mFab!!.backgroundTintList = Utils.getFabState(this, themeUtil!!.colorAccent(), themeUtil!!.colorPrimary())
    }

    private fun setOnClickListener(vararg views: View) {
        for (view in views) {
            view.setOnClickListener(listener)
        }
    }

    private fun setUpRadio() {
        val loaded = Prefs.getInstance(this).appThemeColor
        when (loaded) {
            0 -> red!!.isSelected = true
            1 -> purple!!.isSelected = true
            2 -> greenLight!!.isSelected = true
            3 -> green!!.isSelected = true
            4 -> blueLight!!.isSelected = true
            5 -> blue!!.isSelected = true
            6 -> yellow!!.isSelected = true
            7 -> orange!!.isSelected = true
            8 -> cyan!!.isSelected = true
            9 -> pink!!.isSelected = true
            10 -> teal!!.isSelected = true
            11 -> amber!!.isSelected = true
            12 -> deepPurple!!.isSelected = true
            13 -> deepOrange!!.isSelected = true
            14 -> lime!!.isSelected = true
            15 -> indigo!!.isSelected = true
            else -> blue!!.isSelected = true
        }
    }

    private fun themeColorSwitch(radio: Int) {
        if (radio == prevId) return
        isChanged = true
        prevId = radio
        disableAll()
        setSelected(radio)
        when (radio) {
            R.id.red_checkbox -> saveColor(0)
            R.id.violet_checkbox -> saveColor(1)
            R.id.light_green_checkbox -> saveColor(2)
            R.id.green_checkbox -> saveColor(3)
            R.id.light_blue_checkbox -> saveColor(4)
            R.id.blue_checkbox -> saveColor(5)
            R.id.yellow_checkbox -> saveColor(6)
            R.id.orange_checkbox -> saveColor(7)
            R.id.grey_checkbox -> saveColor(8)
            R.id.pink_checkbox -> saveColor(9)
            R.id.sand_checkbox -> saveColor(10)
            R.id.brown_checkbox -> saveColor(11)
            R.id.deepPurple -> saveColor(12)
            R.id.deepOrange -> saveColor(13)
            R.id.limeCheckbox -> saveColor(14)
            R.id.indigoCheckbox -> saveColor(15)
        }
        toolbar!!.setBackgroundColor(themeUtil!!.getColor(themeUtil!!.colorPrimary()))
        if (Module.isLollipop) {
            window.statusBarColor = themeUtil!!.getColor(themeUtil!!.colorPrimaryDark())
        }
        mFab!!.backgroundTintList = Utils.getFabState(this, themeUtil!!.colorAccent(), themeUtil!!.colorPrimary())
        mFab!!.rippleColor = themeUtil!!.getColor(themeUtil!!.colorPrimary())
    }

    private fun setSelected(radio: Int) {
        findViewById<View>(radio).isSelected = true
    }

    private fun disableAll() {
        red!!.isSelected = false
        purple!!.isSelected = false
        greenLight!!.isSelected = false
        green!!.isSelected = false
        blueLight!!.isSelected = false
        blue!!.isSelected = false
        yellow!!.isSelected = false
        orange!!.isSelected = false
        cyan!!.isSelected = false
        pink!!.isSelected = false
        teal!!.isSelected = false
        amber!!.isSelected = false
        deepOrange!!.isSelected = false
        deepPurple!!.isSelected = false
        lime!!.isSelected = false
        indigo!!.isSelected = false
    }

    private fun saveColor(code: Int) {
        Prefs.getInstance(this).appThemeColor = code
    }

    override fun onBackPressed() {
        if (isChanged) setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
