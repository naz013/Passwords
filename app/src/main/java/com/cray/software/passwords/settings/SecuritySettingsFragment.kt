package com.cray.software.passwords.settings

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.FragmentSecuritySettingsBinding
import com.cray.software.passwords.modern_ui.NestedFragment
import com.cray.software.passwords.utils.Dialogues
import com.cray.software.passwords.utils.Prefs
import com.cray.software.passwords.utils.SuperUtil

import java.lang.ref.WeakReference

class SecuritySettingsFragment : NestedFragment() {

    private var binding: FragmentSecuritySettingsBinding? = null

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSecuritySettingsBinding.inflate(inflater, container, false)

        binding!!.passwordPref.setOnClickListener { view -> showChangePasswordDialog() }
        binding!!.lengthPref.setOnClickListener { view -> showPasswordLengthDialog() }
        binding!!.keywordPref.setOnClickListener { view -> showKeywordDialog() }

        return binding!!.getRoot()
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (anInterface != null) {
            anInterface!!.setClick(null)
            anInterface!!.setTitle(getString(R.string.security_block))
        }
    }

    private fun showPasswordLength() {
        binding!!.lengthPref.setValueText(Prefs.getInstance(activity).passwordLength.toString())
    }

    private fun showPasswordLengthDialog() {
        Dialogues.showSeekDialog(activity, { dialog, value ->
            Prefs.getInstance(context).oldPasswordLength = Prefs.getInstance(activity).passwordLength
            Prefs.getInstance(context).passwordLength = value
            dialog.dismiss()
            showPasswordLength()
        }, getString(R.string.password_length_title), Prefs.getInstance(activity).passwordLength)
    }

    private fun showKeywordDialog() {
        Dialogues.showSimpleDialog(activity, { dialog, view, error ->
            val keyword = view.getText().toString().trim({ it <= ' ' })
            if (TextUtils.isEmpty(keyword)) {
                error.setError(resources.getString(R.string.set_att_if_all_field_empty))
                error.setErrorEnabled(true)
            } else {
                Prefs.getInstance(activity).restoreWord = SuperUtil.encrypt(keyword)
                dialog.dismiss()
            }
        }, getString(R.string.keyword_setting_title), getString(R.string.restore_dialog_enter_keyword))
    }

    private fun showChangePasswordDialog() {
        Dialogues.showPasswordChangeDialog(activity, { dialog, field, layout, field2, layout2 ->
            val oldPassStr = field.getText().toString().trim({ it <= ' ' })
            val newPassStr = field2.getText().toString().trim({ it <= ' ' })
            var hasError = false
            if (TextUtils.isEmpty(oldPassStr)) {
                hasError = true
                layout.setError(getString(R.string.set_att_if_all_field_empty))
                layout.setErrorEnabled(true)
            }
            if (TextUtils.isEmpty(newPassStr)) {
                hasError = true
                layout2.setError(getString(R.string.set_att_if_all_field_empty))
                layout2.setErrorEnabled(true)
            }
            if (hasError) return@Dialogues.showPasswordChangeDialog

            val loadedPass1 = WeakReference(SuperUtil.decrypt(Prefs.getInstance(activity).loadPassPrefs()))
            if (oldPassStr == loadedPass1.get()) {
                Prefs.getInstance(activity).savePassPrefs(SuperUtil.encrypt(newPassStr))
                dialog.dismiss()
            } else {
                layout.setError(resources.getString(R.string.set_att_if_inserted_not_match_saved))
                layout.setErrorEnabled(true)
            }
        }, getString(R.string.about_dialog_title), getString(R.string.dialog_old_text), getString(R.string.dialog_new_text))
    }

    companion object {

        val TAG = "SecuritySettingsFragment"

        fun newInstance(): SecuritySettingsFragment {
            return SecuritySettingsFragment()
        }
    }
}
