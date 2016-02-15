package com.cray.software.passwords.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.dialogs.DateFormatDialog;
import com.cray.software.passwords.dialogs.ThemerDialog;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class GeneralSettingsFragment extends Fragment implements View.OnClickListener {

    RelativeLayout themeColor, backupFile;
    CheckBox backupFileCheck;
    TextView dateFormat;
    View themeColorSwitcher;
    SharedPrefs sPrefs;
    ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.general_settings_layout, container, false);

        ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null){
            ab.setTitle(R.string.interface_block);
        }

        getActivity().getIntent().setAction("General attached");

        themeColor = (RelativeLayout) rootView.findViewById(R.id.themeColor);
        themeColorSwitcher = rootView.findViewById(R.id.themeColorSwitcher);

        themeView();
        themeColor.setOnClickListener(this);

        dateFormat = (TextView) rootView.findViewById(R.id.dateFormat);
        dateFormat.setOnClickListener(this);

        backupFile = (RelativeLayout) rootView.findViewById(R.id.backupFile);
        backupFile.setOnClickListener(this);

        backupFileCheck = (CheckBox) rootView.findViewById(R.id.backupFileCheck);
        backupFileCheck.setChecked(sPrefs.loadBoolean(Constants.NEW_PREFERENCES_CHECKBOX));

        return rootView;
    }

    private void setDeleteFileChange (){
        sPrefs = new SharedPrefs(getActivity().getApplicationContext());
        if (backupFileCheck.isChecked()){
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_CHECKBOX, false);
            backupFileCheck.setChecked(false);
        } else {
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_CHECKBOX, true);
            backupFileCheck.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String action = getActivity().getIntent().getAction();
        if(action == null || !action.equals("General attached")) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        getActivity().recreate();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });
        } else {
            getActivity().getIntent().setAction(null);
        }

        themeView();
    }

    private void themeView(){
        sPrefs = new SharedPrefs(getActivity().getApplicationContext());
        String loadedColor = sPrefs.loadPrefs(Constants.NEW_PREFERENCES_THEME);
        switch (loadedColor) {
            case "1":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_red);
                break;
            case "2":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_violet);
                break;
            case "3":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_green_l);
                break;
            case "4":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_green);
                break;
            case "5":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_blue_l);
                break;
            case "6":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_blue);
                break;
            case "7":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_yellow);
                break;
            case "8":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_orange);
                break;
            case "9":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_grey);
                break;
            case "10":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_pink);
                break;
            case "11":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_sand);
                break;
            case "12":
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_brown);
                break;
            default:
                themeColorSwitcher.setBackgroundResource(R.drawable.checkbox_sand);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null){
            ab.setTitle(R.string.action_settings);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.themeColor:
                getActivity().getApplicationContext().startActivity(
                        new Intent(getActivity().getApplicationContext(), ThemerDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.backupFile:
                setDeleteFileChange();
                break;
            case R.id.dateFormat:
                getActivity().getApplicationContext().startActivity(
                        new Intent(getActivity().getApplicationContext(), DateFormatDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
