package com.cray.software.passwords.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cray.software.passwords.R;
import com.cray.software.passwords.dialogs.ThemerDialog;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.views.roboto.RoboCheckBox;

public class GeneralSettingsFragment extends Fragment implements View.OnClickListener {

    private RoboCheckBox backupFileCheck;
    private View themeColorSwitcher;
    private ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.general_settings_layout, container, false);
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.interface_block);
        }
        getActivity().getIntent().setAction("General attached");
        RelativeLayout themeColor = rootView.findViewById(R.id.themeColor);
        themeColorSwitcher = rootView.findViewById(R.id.themeColorSwitcher);
        themeView();
        themeColor.setOnClickListener(this);
        RelativeLayout backupFile = rootView.findViewById(R.id.backupFile);
        backupFile.setOnClickListener(this);
        backupFileCheck = rootView.findViewById(R.id.backupFileCheck);
        backupFileCheck.setChecked(Prefs.getInstance(getActivity()).isDeleteBackFileEnabled());
        return rootView;
    }

    private void setDeleteFileChange() {
        boolean b = backupFileCheck.isChecked();
        Prefs.getInstance(getActivity()).setDeleteBackFileEnabled(!b);
        backupFileCheck.setChecked(!b);
    }

    @Override
    public void onResume() {
        super.onResume();
        String action = getActivity().getIntent().getAction();
        if (action == null || !action.equals("General attached")) {
            new Handler().post(() -> {
                try {
                    getActivity().recreate();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            });
        } else {
            getActivity().getIntent().setAction(null);
        }
        themeView();
    }

    private void themeView() {
        int loadedColor = Prefs.getInstance(getActivity()).getAppThemeColor();
        themeColorSwitcher.setBackgroundResource(new ColorSetter(getActivity()).getIndicator(loadedColor));
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
            case R.id.themeColor:
                startActivity(new Intent(getActivity().getApplicationContext(), ThemerDialog.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.backupFile:
                setDeleteFileChange();
                break;
        }
    }
}
