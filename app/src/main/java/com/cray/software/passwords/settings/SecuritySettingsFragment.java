package com.cray.software.passwords.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;
import com.cray.software.passwords.views.roboto.RoboTextView;

import java.lang.ref.WeakReference;

public class SecuritySettingsFragment extends Fragment implements View.OnClickListener {

    private RoboTextView passLengthText;
    private ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.security_settings_layout, container, false);
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.security_block);
        }
        RoboTextView changePassword = rootView.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(this);
        RoboTextView keyword = rootView.findViewById(R.id.keyword);
        keyword.setOnClickListener(this);
        RelativeLayout passLength = rootView.findViewById(R.id.passLength);
        passLength.setOnClickListener(this);
        passLengthText = rootView.findViewById(R.id.passLengthText);
        passLengthText.setText(String.valueOf(Prefs.getInstance(getActivity()).getPasswordLength()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showPasswordLength();
    }

    private void showPasswordLength() {
        passLengthText.setText(String.valueOf(Prefs.getInstance(getActivity()).getPasswordLength()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.action_settings);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePassword:
                showChangePasswordDialog();
                break;
            case R.id.keyword:
                showKeywordDialog();
                break;
            case R.id.passLength:
                showPasswordLengthDialog();
                break;
        }
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
