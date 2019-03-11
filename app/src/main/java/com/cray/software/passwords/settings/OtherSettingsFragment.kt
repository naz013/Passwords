package com.cray.software.passwords.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.DialogAboutLayoutBinding
import com.cray.software.passwords.databinding.FragmentOtherSettingsBinding
import com.cray.software.passwords.modern_ui.NestedFragment
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.utils.Dialogues

class OtherSettingsFragment : NestedFragment() {

    private var binding: FragmentOtherSettingsBinding? = null

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOtherSettingsBinding.inflate(inflater, container, false)

        binding!!.aboutPref.setOnClickListener { v -> showAboutDialog() }
        binding!!.ratePref.setOnClickListener { v -> Dialogues.showRateDialog(activity) }
        binding!!.licensePref.setOnClickListener { v -> openOss() }
        binding!!.buyPref.setOnClickListener { view -> openMarket() }
        binding!!.morePref.setOnClickListener { view -> showMoreApps() }
        binding!!.feedbackPref.setOnClickListener { view -> sendFeedback() }

        if (Module.isPro) {
            binding!!.buyPref.visibility = View.GONE
        } else {
            binding!!.buyPref.visibility = View.VISIBLE
        }

        return binding!!.getRoot()
    }

    private fun openOss() {
        if (anInterface != null) {
            anInterface!!.openScreen(OssFragment.newInstance(), OssFragment.TAG)
        }
    }

    private fun openMarket() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$MARKET_APP_PASSWORDS_PRO")
        startActivity(intent)
    }

    private fun sendFeedback() {
        val emailIntent = Intent(android.content.Intent.ACTION_SEND)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("feedback.cray@gmail.com"))
        if (Module.isPro) {
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords PRO")
        } else {
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords")
        }
        startActivity(Intent.createChooser(emailIntent, "Send mail..."))
    }

    private fun showMoreApps() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://search?q=pub:Nazar Suhovich")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Couldn't launch market", Toast.LENGTH_LONG).show()
        }

    }

    private fun showAboutDialog() {
        if (context == null) return
        val builder = Dialogues.getDialog(context!!)
        val binding = DialogAboutLayoutBinding.inflate(LayoutInflater.from(context))
        val name = getString(R.string.app_name)
        binding.appName.text = name.toUpperCase()
        val pInfo: PackageInfo
        try {
            pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
            binding.appVersion.text = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        builder.setView(binding.getRoot())
        builder.create().show()
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (anInterface != null) {
            anInterface!!.setClick(null)
            anInterface!!.setTitle(getString(R.string.other_settings))
        }
    }

    companion object {

        val TAG = "OtherSettingsFragment"
        private val MARKET_APP_PASSWORDS_PRO = "com.cray.software.passwordspro"

        fun newInstance(): OtherSettingsFragment {
            return OtherSettingsFragment()
        }
    }
}
