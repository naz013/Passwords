package com.cray.software.passwords.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.FragmentSecuritySettingsBinding;
import com.cray.software.passwords.fragments.NestedFragment;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

import java.lang.ref.WeakReference;

public class SecuritySettingsFragment extends NestedFragment {

    public static final String TAG = "SecuritySettingsFragment";

    private FragmentSecuritySettingsBinding binding;

    public static SecuritySettingsFragment newInstance() {
        return new SecuritySettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecuritySettingsBinding.inflate(inflater, container, false);

        binding.passwordPref.setOnClickListener(view -> showChangePasswordDialog());
        binding.lengthPref.setOnClickListener(view -> showPasswordLengthDialog());
        binding.keywordPref.setOnClickListener(view -> showKeywordDialog());

        return binding.getRoot();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (anInterface != null) {
            anInterface.setClick(null);
            anInterface.setTitle(getString(R.string.security_block));
        }
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }

    private void showPasswordLength() {
        binding.lengthPref.setValueText(String.valueOf(Prefs.getInstance(getActivity()).getPasswordLength()));
    }

    private void showPasswordLengthDialog() {
        Dialogues.showSeekDialog(getActivity(), (dialog, value) -> {
            Prefs.getInstance(getContext()).setOldPasswordLength(Prefs.getInstance(getActivity()).getPasswordLength());
            Prefs.getInstance(getContext()).setPasswordLength(value);
            dialog.dismiss();
            showPasswordLength();
        }, getString(R.string.password_length_title), Prefs.getInstance(getActivity()).getPasswordLength());
    }

    private void showKeywordDialog() {
        Dialogues.showSimpleDialog(getActivity(), (dialog, view, error) -> {
            String keyword = view.getText().toString().trim();
            if (TextUtils.isEmpty(keyword)) {
                error.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                error.setErrorEnabled(true);
            } else {
                Prefs.getInstance(getActivity()).setRestoreWord(SuperUtil.encrypt(keyword));
                dialog.dismiss();
            }
        }, getString(R.string.keyword_setting_title), getString(R.string.restore_dialog_enter_keyword));
    }

    private void showChangePasswordDialog() {
        Dialogues.showPasswordChangeDialog(getActivity(), (dialog, field, layout, field2, layout2) -> {
            String oldPassStr = field.getText().toString().trim();
            String newPassStr = field2.getText().toString().trim();
            boolean hasError = false;
            if (TextUtils.isEmpty(oldPassStr)) {
                hasError = true;
                layout.setError(getString(R.string.set_att_if_all_field_empty));
                layout.setErrorEnabled(true);
            }
            if (TextUtils.isEmpty(newPassStr)) {
                hasError = true;
                layout2.setError(getString(R.string.set_att_if_all_field_empty));
                layout2.setErrorEnabled(true);
            }
            if (hasError) return;

            WeakReference<String> loadedPass1 = new WeakReference<>(SuperUtil.decrypt(Prefs.getInstance(getActivity()).loadPassPrefs()));
            if (oldPassStr.equals(loadedPass1.get())) {
                Prefs.getInstance(getActivity()).savePassPrefs(SuperUtil.encrypt(newPassStr));
                dialog.dismiss();
            } else {
                layout.setError(getResources().getString(R.string.set_att_if_inserted_not_match_saved));
                layout.setErrorEnabled(true);
            }
        }, getString(R.string.about_dialog_title), getString(R.string.dialog_old_text), getString(R.string.dialog_new_text));
    }
}
