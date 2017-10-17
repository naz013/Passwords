package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.interfaces.Module;

public class AboutDialog extends Activity {

    TextView appVersion, appName;
    Button aboutClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_dialog_layout);
        appName = findViewById(R.id.appName);
        String name;
        if (Module.isPro()) name = getString(R.string.app_name);
        else name = getString(R.string.app_name_free);
        appName.setText(name.toUpperCase());

        appVersion = findViewById(R.id.appVersion);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            appVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        aboutClose = findViewById(R.id.aboutClose);
        aboutClose.setOnClickListener(v -> finish());
    }
}
