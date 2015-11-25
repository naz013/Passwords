package com.cray.software.passwords.fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.dialogs.CloudDrives;
import com.cray.software.passwords.dialogs.ProMarket;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.ModuleManager;

public class ExportSettingsFragment extends Fragment implements View.OnClickListener {

    RelativeLayout autoBackup, autoSync;
    CheckBox autoBackupCheck, autoSyncCheck;
    TextView clouds;
    SharedPrefs sPrefs;
    ColorSetter cSetter;
    ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.export_settings_layout, container, false);

        clouds = (TextView) rootView.findViewById(R.id.clouds);
        clouds.setOnClickListener(this);

        autoBackup = (RelativeLayout) rootView.findViewById(R.id.autoBackup);
        autoBackup.setOnClickListener(this);

        autoBackupCheck = (CheckBox) rootView.findViewById(R.id.autoBackupCheck);
        sPrefs = new SharedPrefs(getActivity().getApplicationContext());
        autoBackupCheck.setChecked(sPrefs.loadBoolean(Constants.NEW_PREFERENCES_AUTO_BACKUP));

        autoSync = (RelativeLayout) rootView.findViewById(R.id.autoSync);
        autoSync.setOnClickListener(this);

        autoSyncCheck = (CheckBox) rootView.findViewById(R.id.autoSyncCheck);
        sPrefs = new SharedPrefs(getActivity().getApplicationContext());
        autoSyncCheck.setChecked(sPrefs.loadBoolean(Constants.NEW_PREFERENCES_AUTO_SYNC));

        return rootView;
    }

    private void autoBackupChange (){
        sPrefs = new SharedPrefs(getActivity().getApplicationContext());
        if (autoBackupCheck.isChecked()){
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_BACKUP, false);
            autoBackupCheck.setChecked(false);
        } else {
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_BACKUP, true);
            autoBackupCheck.setChecked(true);
        }
    }

    private void autoSyncChange (){
        sPrefs = new SharedPrefs(getActivity().getApplicationContext());
        if (autoSyncCheck.isChecked()){
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_SYNC, false);
            autoSyncCheck.setChecked(false);
        } else {
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_SYNC, true);
            autoSyncCheck.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ab = getActivity().getActionBar();
        if (ab != null){
            ab.setTitle(R.string.export_settings_block);
            viewSetter(ab);
        }
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
            case R.id.autoBackup:
                if (!new ModuleManager().isPro()){
                    getActivity().startActivity(new Intent(getActivity(), ProMarket.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    autoBackupChange();
                }
                break;
            case R.id.autoSync:
                if (!new ModuleManager().isPro()){
                    getActivity().startActivity(new Intent(getActivity(), ProMarket.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    autoSyncChange();
                }
                break;
            case R.id.clouds:
                getActivity().getApplicationContext().startActivity(
                        new Intent(getActivity().getApplicationContext(), CloudDrives.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }
}