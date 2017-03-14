package com.cray.software.passwords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.login.ActivityLogin;
import com.cray.software.passwords.login.ActivitySignUp;

import java.io.File;

public class SplashScreen extends Activity {

    private SharedPrefs sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPrefs = new SharedPrefs(SplashScreen.this);
        ColorSetter cs = new ColorSetter(SplashScreen.this);
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cs.getColor(cs.colorPrimaryDark()));
        }
        checkPrefs();
        checkKeys();
    }

    private void attachDouble() {
        startActivity(new Intent(this, ActivitySignUp.class));
        finish();
    }

    private void attachSingle() {
        startActivity(new Intent(this, ActivityLogin.class));
        finish();
    }

    private void checkKeys() {
        File settings = new File("/data/data/" + getPackageName() + "/shared_prefs/" + Constants.NEW_APP_PREFS + ".xml");
        if (settings.exists()) {
            boolean loadedStr = sPrefs.isPassString();
            if (loadedStr) {
                attachSingle();
            } else {
                attachDouble();
            }
        } else {
            SharedPreferences appSettings = getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = appSettings.edit();
            ed.apply();
            attachDouble();
        }
    }

    private void checkPrefs() {
        sPrefs = new SharedPrefs(SplashScreen.this);
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_THEME)) {
            sPrefs.savePrefs(Constants.NEW_PREFERENCES_THEME, "6");
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_SCREEN)) {
            sPrefs.savePrefs(Constants.NEW_PREFERENCES_SCREEN, Constants.SCREEN_AUTO);
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_EDIT_LENGHT)) {
            sPrefs.saveInt(Constants.NEW_PREFERENCES_EDIT_LENGHT, 4);
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_EDIT_OLD_LENGHT)) {
            sPrefs.saveInt(Constants.NEW_PREFERENCES_EDIT_OLD_LENGHT, 4);
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_CHECKBOX)) {
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_CHECKBOX, false);
        }
        if (Module.isPro()) {
            if (!sPrefs.isString(Constants.NEW_PREFERENCES_AUTO_SYNC)) {
                sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_SYNC, false);
            }
            if (!sPrefs.isString(Constants.NEW_PREFERENCES_AUTO_BACKUP)) {
                sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_BACKUP, false);
            }
        }
    }
}
