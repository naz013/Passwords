package com.cray.software.passwords.fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.dialogs.ChangeKeyword;
import com.cray.software.passwords.dialogs.PassChangeDialog;
import com.cray.software.passwords.dialogs.PassLengthDialog;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class SecuritySettingsFragment extends Fragment implements View.OnClickListener {

    TextView changePassword, passLengthText, keyword;
    RelativeLayout passLength;
    SharedPrefs sPrefs;
    ColorSetter cSetter;
    ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.security_settings_layout, container, false);

        changePassword = (TextView) rootView.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(this);

        keyword = (TextView) rootView.findViewById(R.id.keyword);
        keyword.setOnClickListener(this);

        passLength = (RelativeLayout) rootView.findViewById(R.id.passLength);
        passLength.setOnClickListener(this);

        sPrefs = new SharedPrefs(getActivity());
        passLengthText = (TextView) rootView.findViewById(R.id.passLengthText);
        passLengthText.setText(String.valueOf(sPrefs.loadInt(Constants.NEW_PREFERENCES_EDIT_LENGHT)));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ab = getActivity().getActionBar();
        if (ab != null){
            ab.setTitle(R.string.security_block);
            viewSetter(ab);
        }
        sPrefs = new SharedPrefs(getActivity());
        passLengthText.setText(String.valueOf(sPrefs.loadInt(Constants.NEW_PREFERENCES_EDIT_LENGHT)));
    }

    private void viewSetter(ActionBar ab){
        cSetter = new ColorSetter(getActivity().getApplicationContext());
        ab.setBackgroundDrawable(new ColorDrawable(cSetter.colorSetter()));
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePassword:
                getActivity().getApplicationContext()
                        .startActivity(new Intent(getActivity().getApplicationContext(),
                                PassChangeDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.keyword:
                getActivity().getApplicationContext()
                        .startActivity(new Intent(getActivity().getApplicationContext(),
                                ChangeKeyword.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.passLength:
                getActivity().getApplicationContext()
                        .startActivity(new Intent(getActivity().getApplicationContext(),
                                PassLengthDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }
}