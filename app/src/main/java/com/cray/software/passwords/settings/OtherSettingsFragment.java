package com.cray.software.passwords.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.DialogAboutLayoutBinding;
import com.cray.software.passwords.dialogs.ThanksDialog;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.views.roboto.RoboTextView;

public class OtherSettingsFragment extends Fragment {

    private ActionBar ab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.other_settings_layout, container, false);
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.other_settings);
        }
        RoboTextView about = rootView.findViewById(R.id.about);
        about.setOnClickListener(v -> showAboutDialog());
        RoboTextView rateApp = rootView.findViewById(R.id.rateApp);
        rateApp.setOnClickListener(v -> Dialogues.showRateDialog(getActivity()));
        RoboTextView thanks = rootView.findViewById(R.id.thanks);
        thanks.setOnClickListener(v -> startActivity(new Intent(getActivity().getApplicationContext(), ThanksDialog.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
        return rootView;
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DialogAboutLayoutBinding binding = DialogAboutLayoutBinding.inflate(LayoutInflater.from(getContext()));
        String name = getString(R.string.app_name);
        binding.appName.setText(name.toUpperCase());
        PackageInfo pInfo;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            binding.appVersion.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        builder.setView(binding.getRoot());
        builder.create().show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.action_settings);
        }
    }
}
