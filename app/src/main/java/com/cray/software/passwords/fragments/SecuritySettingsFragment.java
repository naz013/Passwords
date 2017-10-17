package com.cray.software.passwords.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.dialogs.ChangeKeyword;
import com.cray.software.passwords.dialogs.PassChangeDialog;
import com.cray.software.passwords.dialogs.PassLengthDialog;
import com.cray.software.passwords.utils.Prefs;

public class SecuritySettingsFragment extends Fragment implements View.OnClickListener {

    private TextView passLengthText;
    private ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.security_settings_layout, container, false);
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.security_block);
        }
        TextView changePassword = rootView.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(this);
        TextView keyword = rootView.findViewById(R.id.keyword);
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
                startActivity(new Intent(getActivity().getApplicationContext(), PassChangeDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.keyword:
                startActivity(new Intent(getActivity().getApplicationContext(), ChangeKeyword.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.passLength:
                startActivity(new Intent(getActivity().getApplicationContext(), PassLengthDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }
}
