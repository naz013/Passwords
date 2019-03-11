package com.cray.software.passwords.passwords

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem

import com.cray.software.passwords.R
import com.cray.software.passwords.data.Password
import com.cray.software.passwords.databinding.ActivityManagePasswordBinding
import com.cray.software.passwords.databinding.DialogColorPickerLayoutBinding
import com.cray.software.passwords.dialogs.GeneratePassword
import com.cray.software.passwords.helpers.DataProvider
import com.cray.software.passwords.helpers.SyncHelper
import com.cray.software.passwords.helpers.TImeUtils
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.utils.Dialogues
import com.cray.software.passwords.utils.SuperUtil
import com.cray.software.passwords.utils.ThemedActivity
import com.cray.software.passwords.views.ColorPickerView

import java.util.Random

class ManagePassword : ThemedActivity() {

    private var binding: ActivityManagePasswordBinding? = null
    private var mPassword: Password? = null
    private var mColor: Int = 0

    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<T>(this, R.layout.activity_manage_password)

        setSupportActionBar(binding!!.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        id = intent.getLongExtra(Constants.INTENT_ID, 0)

        binding!!.loginEnter.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        binding!!.commentEnter.setOnKeyListener { v, keyCode, event -> event.keyCode == KeyEvent.KEYCODE_ENTER }
        binding!!.generateDialog.setOnClickListener { v ->
            startActivityForResult(Intent(this@ManagePassword, GeneratePassword::class.java),
                    Constants.REQUEST_CODE_PASS)
        }
        binding!!.showPass.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding!!.passwordEnter.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding!!.passwordEnter.inputType = 129
            }
        }
        binding!!.showPass.isChecked = true

        if (savedInstanceState != null) {
            binding!!.titleEnter.setText(savedInstanceState.getString("title"))
            binding!!.loginEnter.setText(savedInstanceState.getString("login"))
            binding!!.passwordEnter.setText(savedInstanceState.getString("pass"))
            binding!!.linkEnter.setText(savedInstanceState.getString("link"))
            binding!!.commentEnter.setText(savedInstanceState.getString("comm"))
            mColor = savedInstanceState.getInt("color")
        }
        mColor = Random().nextInt(16) + 1
        showPassword()
        updateBackground()

        if (themeUtil != null && themeUtil!!.isDark) {
            binding!!.generateDialog.setImageResource(R.drawable.ic_vpn_key_white_24dp)
        } else {
            binding!!.generateDialog.setImageResource(R.drawable.ic_vpn_key_black_24dp)
        }
    }

    private fun showPassword() {
        mPassword = DataProvider.getPassword(this, id)
        if (mPassword != null) {
            binding!!.titleEnter.setText(SuperUtil.decrypt(mPassword!!.title))
            binding!!.passwordEnter.setText(SuperUtil.decrypt(mPassword!!.password))
            binding!!.loginEnter.setText(SuperUtil.decrypt(mPassword!!.login))
            binding!!.commentEnter.setText(SuperUtil.decrypt(mPassword!!.comment))
            binding!!.linkEnter.setText(SuperUtil.decrypt(mPassword!!.url))
            mColor = mPassword!!.color
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_PASS) {
            if (resultCode == Activity.RESULT_OK) {
                val string = data!!.getStringExtra("GENERATED_PASSWORD")
                binding!!.passwordEnter.setText(string)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_add, menu)
        if (id != 0L) {
            menu.add(Menu.NONE, MENU_ITEM_DELETE, 100, getString(R.string.delete))
        }
        return true
    }

    private fun deleteDialog() {
        val builder = Dialogues.getDialog(this)
        builder.setMessage(R.string.delete_this_password)
        builder.setPositiveButton(R.string.yes) { dialog, which ->
            dialog.dismiss()
            DataProvider.deletePassword(this@ManagePassword, mPassword!!)
            finish()
        }
        builder.setNegativeButton(R.string.no) { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.save -> {
                if (save()) finish()
                return true
            }
            R.id.changeColor -> {
                showColorDialog()
                return true
            }
            R.id.save_and_new -> {
                if (save()) {
                    Snackbar.make(binding!!.windowBackground, R.string.saved, Snackbar.LENGTH_SHORT).show()
                    fieldClear()
                }
                return true
            }
            MENU_ITEM_DELETE -> {
                deleteDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun save(): Boolean {
        var title = binding!!.titleEnter.text!!.toString().trim { it <= ' ' }
        var login = binding!!.loginEnter.text!!.toString().trim { it <= ' ' }
        var password = binding!!.passwordEnter.text!!.toString().trim { it <= ' ' }
        var url = binding!!.linkEnter.text!!.toString().trim { it <= ' ' }
        var comment = binding!!.commentEnter.text!!.toString().trim { it <= ' ' }
        if (checkEmpty()) return false
        var date = TImeUtils.gmtStamp
        title = SuperUtil.encrypt(title)
        login = SuperUtil.encrypt(login)
        password = SuperUtil.encrypt(password)
        url = SuperUtil.encrypt(url)
        comment = SuperUtil.encrypt(comment)
        date = SuperUtil.encrypt(date)
        if (mPassword == null) {
            mPassword = Password(title, date, login, comment, url, 0, mColor, password, SyncHelper.generateID())
        } else {
            mPassword!!.title = title
            mPassword!!.color = mColor
            mPassword!!.comment = comment
            mPassword!!.date = date
            mPassword!!.login = login
            mPassword!!.url = url
            mPassword!!.password = password
        }
        DataProvider.savePassword(this, mPassword!!)
        return true
    }

    override fun onSaveInstanceState(saveInstance: Bundle) {
        saveInstance.putString("title", binding!!.titleEnter.text!!.toString().trim { it <= ' ' })
        saveInstance.putString("login", binding!!.loginEnter.text!!.toString().trim { it <= ' ' })
        saveInstance.putString("pass", binding!!.passwordEnter.text!!.toString().trim { it <= ' ' })
        saveInstance.putString("link", binding!!.linkEnter.text!!.toString().trim { it <= ' ' })
        saveInstance.putString("comm", binding!!.commentEnter.text!!.toString().trim { it <= ' ' })
        saveInstance.putInt("color", mColor)
        super.onSaveInstanceState(saveInstance)
    }

    fun fieldClear() {
        binding!!.commentEnter.setText("")
        val link_enter_str = binding!!.linkEnter.text!!.toString()
        if (link_enter_str == "") {
            binding!!.linkEnter.setText("https://www.")
        }
        binding!!.passwordEnter.setText("")
        binding!!.loginEnter.setText("")
        binding!!.titleEnter.setText("")
    }

    fun checkEmpty(): Boolean {
        if (TextUtils.isEmpty(binding!!.titleEnter.text!!.toString().trim { it <= ' ' })) {
            Snackbar.make(binding!!.windowBackground, R.string.edit_title_is_empty, Snackbar.LENGTH_SHORT).show()
            return true
        } else if (TextUtils.isEmpty(binding!!.loginEnter.text!!.toString().trim { it <= ' ' })) {
            Snackbar.make(binding!!.windowBackground, R.string.edit_login_is_empty, Snackbar.LENGTH_SHORT).show()
            return true
        } else if (TextUtils.isEmpty(binding!!.passwordEnter.text!!.toString().trim { it <= ' ' })) {
            Snackbar.make(binding!!.windowBackground, R.string.edit_pass_is_empty, Snackbar.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun updateBackground() {
        if (themeUtil != null) {
            binding!!.appBar.setBackgroundColor(themeUtil!!.getColor(themeUtil!!.colorPrimary(mColor)))
            if (Module.isLollipop) {
                window.statusBarColor = themeUtil!!.getColor(themeUtil!!.colorPrimaryDark(mColor))
            }
        }
    }

    private fun showColorDialog() {
        val builder = Dialogues.getDialog(this)
        builder.setTitle(getString(R.string.choose_color))
        val binding = DialogColorPickerLayoutBinding.inflate(LayoutInflater.from(this))
        val view = binding.pickerView
        view.setSelectedColor(mColor)
        builder.setView(binding.getRoot())
        val dialog = builder.create()
        view.setListener { code ->
            mColor = code
            updateBackground()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {

        private val MENU_ITEM_DELETE = 12
    }
}
