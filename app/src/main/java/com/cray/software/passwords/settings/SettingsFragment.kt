package com.cray.software.passwords.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.FragmentSettingsBinding
import com.cray.software.passwords.modern_ui.BaseFragment

class SettingsFragment : BaseFragment() {
    private var binding: FragmentSettingsBinding? = null

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.otherSettings.setOnClickListener { view1 ->
            if (anInterface != null) {
                anInterface!!.openScreen(OtherSettingsFragment.newInstance(), OtherSettingsFragment.TAG)
            }
        }
        binding!!.exportSettings.setOnClickListener { view1 ->
            if (anInterface != null) {
                anInterface!!.openScreen(ExportSettingsFragment.newInstance(), ExportSettingsFragment.TAG)
            }
        }
        binding!!.securitySettings.setOnClickListener { view12 ->
            if (anInterface != null) {
                anInterface!!.openScreen(SecuritySettingsFragment.newInstance(), SecuritySettingsFragment.TAG)
            }
        }
        binding!!.generalSettings.setOnClickListener { view12 ->
            if (anInterface != null) {
                anInterface!!.openScreen(GeneralSettingsFragment.newInstance(), GeneralSettingsFragment.TAG)
            }
        }
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (anInterface != null) {
            anInterface!!.setClick(null)
            anInterface!!.setTitle(getString(R.string.action_settings))
        }
    }

    companion object {

        val TAG = "SettingsFragment"

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
