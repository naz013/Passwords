package com.cray.software.passwords.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.cray.software.passwords.MainActivity
import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.ActivityLoginBinding
import com.cray.software.passwords.helpers.Permissions
import com.cray.software.passwords.utils.PrefsConstants
import com.cray.software.passwords.utils.SuperUtil
import com.cray.software.passwords.utils.ThemedActivity
import com.hanks.passcodeview.PasscodeView

class ActivityLogin : ThemedActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var failTimes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.passcodeView.listener = object : PasscodeView.PasscodeViewListener {
            override fun onFail() {
                Toast.makeText(this@ActivityLogin, R.string.password_not_match, Toast.LENGTH_SHORT).show()
                failTimes++
                if (failTimes >= 3) showRestoreDialog()
            }

            override fun onSuccess(number: String) {
                val intentMain = Intent(this@ActivityLogin, MainActivity::class.java)
                startActivity(intentMain)
                finish()
            }
        }

        setFilter()
    }

    private fun showRestoreDialog() {
        if (prefs.hasSecureKey(PrefsConstants.KEYWORD)) {
//            Dialogues.showSimpleDialog(this, { dialog, field, layout ->
//                val keyword = field.text.toString().trim()
//                if (TextUtils.isEmpty(keyword)) {
//                    layout.error = resources.getString(R.string.set_att_if_all_field_empty)
//                    layout.isErrorEnabled = true
//                } else {
//                    if (keyword == SuperUtil.decrypt(Prefs.getInstance(this@ActivityLogin).restoreWord)) {
//                        dialog.dismiss()
//                        showPassword()
//                    } else {
//                        layout.error = getString(R.string.keyword_not_match)
//                        layout.isErrorEnabled = true
//                    }
//                }
//            }, getString(R.string.keyword_setting_title), getString(R.string.restore_dialog_enter_keyword))
        }
    }

    private fun showPassword() {
//        Dialogues.showTextDialog(this, DialogOkClick { it.dismiss() }, getString(R.string.received_pass_title),
//                "{ " + SuperUtil.decrypt(Prefs.getInstance(this).loadPassPrefs()) + " }")
    }

    override fun onResume() {
        super.onResume()
        if (!Permissions.checkPermission(this, Permissions.READ_EXTERNAL,
                        Permissions.WRITE_EXTERNAL)) {
            Permissions.requestPermission(this, 102, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL)
        }
    }

    private fun setFilter() {
        val passLengthInt = prefs.passwordLength
        var loadedPass = prefs.loadPassPrefs()
        loadedPass = SuperUtil.decrypt(loadedPass)
        if (passLengthInt == loadedPass.length) {
            binding.passcodeView.passcodeLength = passLengthInt
        } else {
            binding.passcodeView.passcodeLength = loadedPass.length
        }
        binding.passcodeView.localPasscode = loadedPass
        binding.passcodeView.invalidate()
    }
}
