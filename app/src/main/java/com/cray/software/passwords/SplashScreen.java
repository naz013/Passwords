package com.cray.software.passwords;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.login.ActivityLogin;
import com.cray.software.passwords.login.ActivitySignUp;
import com.cray.software.passwords.utils.Prefs;

import java.io.File;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            writePrefs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Prefs.getInstance(this).checkPrefs();
        checkKeys();
    }

    private void writePrefs() {
        boolean isSD = SyncHelper.isSdPresent();
        if (isSD) {
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.PREFS);
            if (!sdPathDr.exists()) {
                sdPathDr.mkdirs();
            }
            File prefs = new File(sdPathDr + "/prefs.xml");
            if (prefs.exists()) {
                Prefs.getInstance(this).loadSharedPreferencesFromFile(prefs);
            } else {
                Prefs.getInstance(this).saveSharedPreferencesToFile(prefs);
            }
        }
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
        File settings = new File("/data/data/" + getPackageName() + "/shared_prefs/" + Prefs.LOGIN_PREFS + ".xml");
        if (settings.exists()) {
            boolean loadedStr = Prefs.getInstance(this).isPassString();
            if (loadedStr) {
                attachSingle();
            } else {
                attachDouble();
            }
        } else {
            SharedPreferences appSettings = getSharedPreferences(Prefs.LOGIN_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = appSettings.edit();
            ed.apply();
            attachDouble();
        }
    }
}
