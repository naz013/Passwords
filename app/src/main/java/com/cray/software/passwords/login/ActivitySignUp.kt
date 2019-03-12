package com.cray.software.passwords.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.cray.software.passwords.MainActivity
import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.ActivitySignUpBinding
import com.cray.software.passwords.helpers.Permissions
import com.cray.software.passwords.utils.SuperUtil
import com.cray.software.passwords.utils.ThemedActivity
import com.hanks.passcodeview.PasscodeView

class ActivitySignUp : ThemedActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.passcodeView.listener = object : PasscodeView.PasscodeViewListener {
            override fun onFail() {
                Toast.makeText(this@ActivitySignUp, R.string.password_not_match, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(number: String) {
                prefs.savePassPrefs(SuperUtil.encrypt(number))
                val intentMain = Intent(this@ActivitySignUp, MainActivity::class.java)
                startActivity(intentMain)
                finish()
            }
        }

        setFilter()
    }

    override fun onResume() {
        super.onResume()
        if (!Permissions.checkPermission(this, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL)) {
            Permissions.requestPermission(this, 102, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL)
        }
    }

    private fun setFilter() {
        val passLengthInt = prefs.passwordLength
        binding.passcodeView.passcodeLength = passLengthInt
        binding.passcodeView.invalidate()
    }
}
