package com.cray.software.passwords.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.FragmentSettingsBinding;
import com.cray.software.passwords.fragments.BaseFragment;

public class SettingsFragment extends BaseFragment {

    public static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;

    public static BaseFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.otherSettings.setOnClickListener(view1 -> {
            if (anInterface != null) {
                anInterface.openScreen(OtherSettingsFragment.newInstance(), OtherSettingsFragment.TAG);
            }
        });
        binding.exportSettings.setOnClickListener(view1 -> {
            if (anInterface != null) {
                anInterface.openScreen(ExportSettingsFragment.newInstance(), ExportSettingsFragment.TAG);
            }
        });
        binding.securitySettings.setOnClickListener(view12 -> {
            if (anInterface != null) {
                anInterface.openScreen(SecuritySettingsFragment.newInstance(), SecuritySettingsFragment.TAG);
            }
        });
        binding.generalSettings.setOnClickListener(view12 -> {
            if (anInterface != null) {
                anInterface.openScreen(GeneralSettingsFragment.newInstance(), GeneralSettingsFragment.TAG);
            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (anInterface != null) {
            anInterface.setTitle(getString(R.string.action_settings));
        }
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }
}
