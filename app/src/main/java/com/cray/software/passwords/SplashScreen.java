package com.cray.software.passwords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Permissions;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.login.ActivityLogin;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class SplashScreen extends Activity {

    private LinearLayout loadLayout;
    private LinearLayout doubleLayout;
    private EditText firstPass, secondPass;
    private SharedPreferences appSettings;
    private SharedPrefs sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        writePrefs();
        ColorSetter cs = new ColorSetter(SplashScreen.this);
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cs.getColor(cs.colorPrimaryDark()));
        }
        LinearLayout splashBg = (LinearLayout) findViewById(R.id.splashBg);
        splashBg.setBackgroundColor(cs.getColor(cs.colorPrimary()));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        TextView textView = (TextView) findViewById(R.id.textView);
        if (!Module.isPro()) textView.setText(getString(R.string.app_name_free));
        else textView.setText(getString(R.string.app_name));
        textView.setTypeface(typeface);

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setTypeface(typeface);

        clearLayout();
        checkPrefs();
        checkKeys();
    }

    private void attachDouble() {
        detachLoading();

        doubleLayout = (LinearLayout) findViewById(R.id.doubleLayout);
        doubleLayout.setVisibility(View.VISIBLE);

        firstPass = (EditText) findViewById(R.id.firstPass);
        secondPass = (EditText) findViewById(R.id.secondPass);

        sPrefs = new SharedPrefs(getApplicationContext());
        int passLenght = sPrefs.loadInt(Constants.NEW_PREFERENCES_EDIT_LENGHT);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(passLenght);

        firstPass.setFilters(FilterArray);
        secondPass.setFilters(FilterArray);

        Button loginSaver = (Button) findViewById(R.id.loginSaver);
        loginSaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstStr = firstPass.getText().toString();
                String secondStr = secondPass.getText().toString();
                firstLogin(firstStr, secondStr);
            }
        });
    }

    private void attachSingle() {
        detachLoading();
        startActivity(new Intent(this, ActivityLogin.class));
        finish();
    }

    private void detachLoading() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);

        loadLayout.setVisibility(View.GONE);
    }

    private void clearLayout() {
        loadLayout = (LinearLayout) findViewById(R.id.loadLayout);
        loadLayout.setVisibility(View.VISIBLE);

        LinearLayout singleLayout = (LinearLayout) findViewById(R.id.singleLayout);
        singleLayout.setVisibility(View.GONE);

        doubleLayout = (LinearLayout) findViewById(R.id.doubleLayout);
        doubleLayout.setVisibility(View.GONE);
    }

    private void writePrefs() {
        checkPrefs();

        sPrefs = new SharedPrefs(SplashScreen.this);
        boolean isSD = SyncHelper.isSdPresent();
        if (isSD) {
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.PREFS);
            if (!sdPathDr.exists()) {
                sdPathDr.mkdirs();
            }
            File prefs = new File(sdPathDr + "/prefs.xml");
            if (prefs.exists()) {
                sPrefs.loadSharedPreferencesFromFile(prefs);
            } else {
                sPrefs.saveSharedPreferencesToFile(prefs);
            }
        }
    }

    private void checkKeys() {
        File settings = new File("/data/data/" + getPackageName() + "/shared_prefs/" + Constants.NEW_APP_PREFS + ".xml");
        if (settings.exists()) {
            sPrefs = new SharedPrefs(SplashScreen.this);
            boolean loadedStr = sPrefs.isPassString();
            if (loadedStr) {
                attachSingle();
            } else {
                attachDouble();
            }
        } else {
            appSettings = getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = appSettings.edit();
            ed.commit();
            // double screen
            attachDouble();
        }
    }

    public void firstLogin(String firstStr, String secondStr) {
        if (firstStr.equals("") & secondStr.equals("")) {
            firstPass.setHint(R.string.set_att_if_all_field_empty);
            secondPass.setHint(R.string.set_att_if_all_field_empty);
        } else {
            if (firstStr.matches("")) {
                firstPass.setHint(R.string.set_att_if_all_field_empty);
                secondPass.setText("");
            } else {
                if (secondStr.matches("")) {
                    secondPass.setHint(R.string.set_att_if_all_field_empty);
                    firstPass.setText("");
                } else {
                    if (firstStr.equals(secondStr)) {
                        try {
                            savePass(firstPass.getText().toString().trim());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Intent intentMain = new Intent(this, MainActivity.class);
                        startActivity(intentMain);
                        finish();
                    } else {
                        firstPass.setText("");
                        secondPass.setText("");
                    }
                }
            }
        }
    }

    void savePass(String insertedPass) throws UnsupportedEncodingException {
        byte[] passByted;
        passByted = insertedPass.getBytes("UTF-8");
        String passEncrypted = Base64.encodeToString(passByted, Base64.DEFAULT).trim();
        sPrefs = new SharedPrefs(SplashScreen.this);
        sPrefs.savePassPrefs(passEncrypted);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!Permissions.checkPermission(SplashScreen.this, Permissions.READ_EXTERNAL,
                Permissions.WRITE_EXTERNAL)) {
            Permissions.requestPermission(SplashScreen.this, 102, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL);
        }
    }
}
