package com.cray.software.passwords.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.FragmentExportSettingsBinding;
import com.cray.software.passwords.fragments.NestedFragment;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Prefs;

public class ExportSettingsFragment extends NestedFragment {

    public static final String TAG = "ExportSettingsFragment";
    private FragmentExportSettingsBinding binding;

    public static ExportSettingsFragment newInstance() {
        return new ExportSettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExportSettingsBinding.inflate(inflater, container, false);

        binding.cloudsPref.setOnClickListener(view -> openClouds());

        binding.backupPref.setChecked(Prefs.getInstance(getActivity()).isAutoBackupEnabled());
        binding.backupPref.setOnCheckedListener(this::updateBackup);

        binding.syncPref.setChecked(Prefs.getInstance(getActivity()).isAutoSyncEnabled());
        binding.syncPref.setOnCheckedListener(this::updateSync);

        return binding.getRoot();
    }

    private void openClouds() {
        if (anInterface != null) {
            anInterface.openScreen(CloudFragment.newInstance(), CloudFragment.TAG);
        }
    }

    private void updateBackup(boolean checked) {
        if (Module.isPro()) {
            Prefs.getInstance(getActivity()).setAutoBackupEnabled(checked);
        } else {
            Prefs.getInstance(getActivity()).setAutoBackupEnabled(false);
            if (checked) {
                binding.backupPref.setChecked(false);
                Toast.makeText(getContext(), R.string.this_feature_avilable_for_pro, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateSync(boolean checked) {
        if (Module.isPro()) {
            Prefs.getInstance(getActivity()).setAutoSyncEnabled(checked);
        } else {
            Prefs.getInstance(getActivity()).setAutoSyncEnabled(false);
            if (checked) {
                binding.syncPref.setChecked(false);
                Toast.makeText(getContext(), R.string.this_feature_avilable_for_pro, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (anInterface != null) {
            anInterface.setClick(null);
            anInterface.setTitle(getString(R.string.export_settings_block));
        }
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }
}
