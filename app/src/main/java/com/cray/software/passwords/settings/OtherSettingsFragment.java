package com.cray.software.passwords.settings;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.DialogAboutLayoutBinding;
import com.cray.software.passwords.databinding.FragmentOtherSettingsBinding;
import com.cray.software.passwords.dialogs.ThanksDialog;
import com.cray.software.passwords.fragments.NestedFragment;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Dialogues;

public class OtherSettingsFragment extends NestedFragment {

    public static final String TAG = "OtherSettingsFragment";
    private static final String MARKET_APP_PASSWORDS_PRO = "com.cray.software.passwordspro";

    private FragmentOtherSettingsBinding binding;

    public static OtherSettingsFragment newInstance() {
        return new OtherSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtherSettingsBinding.inflate(inflater, container, false);

        binding.aboutPref.setOnClickListener(v -> showAboutDialog());
        binding.ratePref.setOnClickListener(v -> Dialogues.showRateDialog(getActivity()));
        binding.licensePref.setOnClickListener(v -> startActivity(new Intent(getActivity().getApplicationContext(), ThanksDialog.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
        binding.buyPref.setOnClickListener(view -> openMarket());
        binding.morePref.setOnClickListener(view -> showMoreApps());
        binding.feedbackPref.setOnClickListener(view -> sendFeedback());

        if (Module.isPro()) {
            binding.buyPref.setVisibility(View.GONE);
        } else {
            binding.buyPref.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    private void openMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + MARKET_APP_PASSWORDS_PRO));
        startActivity(intent);
    }

    private void sendFeedback() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"feedback.cray@gmail.com"});
        if (Module.isPro()) {
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords PRO");
        } else {
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords");
        }
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    private void showMoreApps() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:Nazar Suhovich"));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Couldn't launch market", Toast.LENGTH_LONG).show();
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = Dialogues.getDialog(getContext());
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
    public void onFragmentResume() {
        super.onFragmentResume();
        if (anInterface != null) {
            anInterface.setTitle(getString(R.string.other_settings));
        }
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }
}
