package com.cray.software.passwords.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.FragmentExportSettingsBinding
import com.cray.software.passwords.modern_ui.NestedFragment
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.utils.Prefs

class ExportSettingsFragment : NestedFragment() {
    private var binding: FragmentExportSettingsBinding? = null

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExportSettingsBinding.inflate(inflater, container, false)

        binding!!.cloudsPref.setOnClickListener { view -> openClouds() }

        binding!!.backupPref.isChecked = Prefs.getInstance(activity).isAutoBackupEnabled
        binding!!.backupPref.setOnCheckedListener(OnCheckedListener { this.updateBackup(it) })

        binding!!.syncPref.isChecked = Prefs.getInstance(activity).isAutoSyncEnabled
        binding!!.syncPref.setOnCheckedListener(OnCheckedListener { this.updateSync(it) })

        return binding!!.getRoot()
    }

    private fun openClouds() {
        if (anInterface != null) {
            anInterface!!.openScreen(CloudFragment.newInstance(), CloudFragment.TAG)
        }
    }

    private fun updateBackup(checked: Boolean) {
        if (Module.isPro) {
            Prefs.getInstance(activity).isAutoBackupEnabled = checked
        } else {
            Prefs.getInstance(activity).isAutoBackupEnabled = false
            if (checked) {
                binding!!.backupPref.isChecked = false
                Toast.makeText(context, R.string.this_feature_avilable_for_pro, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSync(checked: Boolean) {
        if (Module.isPro) {
            Prefs.getInstance(activity).isAutoSyncEnabled = checked
        } else {
            Prefs.getInstance(activity).isAutoSyncEnabled = false
            if (checked) {
                binding!!.syncPref.isChecked = false
                Toast.makeText(context, R.string.this_feature_avilable_for_pro, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (anInterface != null) {
            anInterface!!.setClick(null)
            anInterface!!.setTitle(getString(R.string.export_settings_block))
        }
    }

    companion object {

        val TAG = "ExportSettingsFragment"

        fun newInstance(): ExportSettingsFragment {
            return ExportSettingsFragment()
        }
    }
}
