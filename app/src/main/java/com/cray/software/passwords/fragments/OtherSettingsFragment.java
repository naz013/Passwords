package com.cray.software.passwords.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cray.software.passwords.ApplicationHelp;
import com.cray.software.passwords.R;
import com.cray.software.passwords.dialogs.AboutDialog;
import com.cray.software.passwords.dialogs.RateDialog;
import com.cray.software.passwords.dialogs.ThanksDialog;

public class OtherSettingsFragment extends Fragment {

    ActionBar ab;
    TextView about, rateApp, thanks, help;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.other_settings_layout, container, false);

        ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null){
            ab.setTitle(R.string.other_settings);
        }

        about = (TextView) rootView.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getApplicationContext()
                        .startActivity(new Intent(getActivity().getApplicationContext(),
                                AboutDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        rateApp = (TextView) rootView.findViewById(R.id.rateApp);
        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getApplicationContext()
                        .startActivity(new Intent(getActivity().getApplicationContext(),
                                RateDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        thanks = (TextView) rootView.findViewById(R.id.thanks);
        thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getApplicationContext()
                        .startActivity(new Intent(getActivity().getApplicationContext(),
                                ThanksDialog.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        help = (TextView) rootView.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getApplicationContext()
                        .startActivity(new Intent(getActivity().getApplicationContext(),
                                ApplicationHelp.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null){
            ab.setTitle(R.string.action_settings);
        }
    }
}
