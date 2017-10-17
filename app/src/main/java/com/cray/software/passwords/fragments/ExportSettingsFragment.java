package com.cray.software.passwords.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.cray.software.passwords.dialogs.CloudDrives;
import com.cray.software.passwords.dialogs.ProMarket;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Prefs;

public class ExportSettingsFragment extends Fragment implements View.OnClickListener {

    private CheckBox autoBackupCheck, autoSyncCheck;
    private ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.export_settings_layout, container, false);
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.export_settings_block);
        }
        TextView clouds = rootView.findViewById(R.id.clouds);
        clouds.setOnClickListener(this);
        RelativeLayout autoBackup = rootView.findViewById(R.id.autoBackup);
        autoBackup.setOnClickListener(this);
        autoBackupCheck = rootView.findViewById(R.id.autoBackupCheck);
        autoBackupCheck.setChecked(Prefs.getInstance(getActivity()).isAutoBackupEnabled());
        RelativeLayout autoSync = rootView.findViewById(R.id.autoSync);
        autoSync.setOnClickListener(this);
        autoSyncCheck = rootView.findViewById(R.id.autoSyncCheck);
        autoSyncCheck.setChecked(Prefs.getInstance(getActivity()).isAutoSyncEnabled());
        return rootView;
    }

    private void autoBackupChange() {
        boolean b = autoBackupCheck.isChecked();
        Prefs.getInstance(getActivity()).setAutoBackupEnabled(!b);
        autoBackupCheck.setChecked(!b);
    }

    private void autoSyncChange() {
        boolean b = autoSyncCheck.isChecked();
        Prefs.getInstance(getActivity()).setAutoSyncEnabled(!b);
        autoSyncCheck.setChecked(!b);
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
            case R.id.autoBackup:
                if (!Module.isPro()) {
                    startActivity(new Intent(getActivity(), ProMarket.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    autoBackupChange();
                }
                break;
            case R.id.autoSync:
                if (!Module.isPro()) {
                    startActivity(new Intent(getActivity(), ProMarket.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    autoSyncChange();
                }
                break;
            case R.id.clouds:
                startActivity(new Intent(getActivity().getApplicationContext(), CloudDrives.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }
}
