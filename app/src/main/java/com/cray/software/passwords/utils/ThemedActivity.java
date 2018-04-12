package com.cray.software.passwords.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.cray.software.passwords.interfaces.Module;

public abstract class ThemedActivity extends AppCompatActivity {

    @Nullable
    private ThemeUtil themeUtil;
    @Nullable
    private Prefs mPrefs;

    @Nullable
    protected Prefs getPrefs() {
        return mPrefs;
    }

    @Nullable
    protected ThemeUtil getThemeUtil() {
        return themeUtil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = Prefs.getInstance(this);
        themeUtil = ThemeUtil.getInstance(this);
        if (themeUtil != null) setTheme(themeUtil.getStyle());
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(themeUtil.getColor(themeUtil.colorPrimaryDark()));
        }
    }
}
