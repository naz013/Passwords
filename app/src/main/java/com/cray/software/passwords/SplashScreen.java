package com.cray.software.passwords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
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
import android.widget.Toast;

import com.cray.software.passwords.dialogs.RestoreInsertMail;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.ModuleManager;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class SplashScreen extends Activity {

    TextView textView, textView2, forgotPassword;
    ImageView imageView;
    LinearLayout splashBg, loadLayout, singleLayout, doubleLayout;
    EditText loginPass, firstPass, secondPass;
    Button loginButton, loginSaver;
    Typeface typeface;
    SharedPreferences appSettings, appUISettings;
    SharedPrefs sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        writePrefs();
        ColorSetter cs = new ColorSetter(SplashScreen.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cs.colorStatus());
        }
        splashBg = (LinearLayout) findViewById(R.id.splashBg);
        splashBg.setBackgroundColor(cs.colorSetter());

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        textView = (TextView) findViewById(R.id.textView);
        if (!new ModuleManager().isPro()) textView.setText(getString(R.string.app_name_free));
        else textView.setText(getString(R.string.app_name));
        textView.setTypeface(typeface);

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setTypeface(typeface);

        clearLayout();
        checkPrefs();
        checkKeys();
    }

    private void attachDouble(){
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

        loginSaver = (Button) findViewById(R.id.loginSaver);
        loginSaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstStr = firstPass.getText().toString();
                String secondStr = secondPass.getText().toString();
                firstLogin(firstStr, secondStr);
            }
        });
    }

    private void attachSingle(){
        detachLoading();

        singleLayout = (LinearLayout) findViewById(R.id.singleLayout);
        singleLayout.setVisibility(View.VISIBLE);

        loginPass = (EditText) findViewById(R.id.loginPass);

        int MODE = Context.MODE_MULTI_PROCESS;
        appUISettings = getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
        sPrefs = new SharedPrefs(getApplicationContext());
        int passLenghtInt = sPrefs.loadInt(Constants.NEW_PREFERENCES_EDIT_LENGHT);

        //Log.d(LOG_TAG, "max lenght: " + passLenghtInt);

        appSettings = getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
        String loadedPass = appSettings.getString(Constants.NEW_APP_PREFERENCES_LOGIN, "").trim();
        String passDecrypted = null;
        byte[] byte_pass = Base64.decode(loadedPass, Base64.DEFAULT);
        try {
            passDecrypted = new String(byte_pass, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int loadPassLength = 0;
        if (passDecrypted != null) {
            loadPassLength = passDecrypted.length();
        }

        loginPass = (EditText) findViewById(R.id.loginPass);

        if (passLenghtInt == loadPassLength) {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(passLenghtInt);
            loginPass.setFilters(FilterArray);
        } else {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(loadPassLength);
            loginPass.setFilters(FilterArray);
        }

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setVisibility(View.GONE);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPrefs = new SharedPrefs(SplashScreen.this);
                boolean isKey = sPrefs.isString(Constants.NEW_PREFERENCES_RESTORE_MAIL);
                if (isKey) {
                    forgotPassword();
                } else {
                    Toast.makeText(SplashScreen.this, getString(R.string.not_have_restore_keyword), Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appSettings = getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
                String loadedPass = appSettings.getString(Constants.NEW_APP_PREFERENCES_LOGIN, "");
                String loginPassStr = loginPass.getText().toString().trim();
                try {
                    loginApp(loadedPass, loginPassStr);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void forgotPassword(){
        Intent intent = new Intent(getApplicationContext(), RestoreInsertMail.class);
        startActivity(intent);
    }

    public void loginApp(String loadedPass, String loginPassStr) throws UnsupportedEncodingException {
        String passDecrypted;
        byte[] byte_pass = Base64.decode(loadedPass, Base64.DEFAULT);
        passDecrypted = new String(byte_pass, "UTF-8");
        if (loginPassStr.matches("")){
            loginPass.setError(getResources().getString(R.string.set_att_if_all_field_empty));
        } else {
            if(loginPassStr.equals(passDecrypted)) {
                Intent intentMain = new Intent(this, MainScreen.class);
                startActivity(intentMain);
                finish();
            } else {
                loginPass.setText("");
                loginPass.setError(getResources().getString(R.string.set_att_if_inserted_not_match_saved));
                forgotPassword.setVisibility(View.VISIBLE);
            }
        }
    }

    private void detachLoading(){
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);

        loadLayout.setVisibility(View.GONE);
    }

    private void clearLayout(){
        loadLayout = (LinearLayout) findViewById(R.id.loadLayout);
        loadLayout.setVisibility(View.VISIBLE);

        singleLayout = (LinearLayout) findViewById(R.id.singleLayout);
        singleLayout.setVisibility(View.GONE);

        doubleLayout = (LinearLayout) findViewById(R.id.doubleLayout);
        doubleLayout.setVisibility(View.GONE);
    }

    private void writePrefs(){

        checkPrefs();

        sPrefs = new SharedPrefs(SplashScreen.this);
        boolean isSD = SyncHelper.isSdPresent();
        if (isSD){
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.PREFS);
            if (!sdPathDr.exists()){
                sdPathDr.mkdirs();
            }
            File prefs = new File(sdPathDr + "/prefs.xml");
            if (prefs.exists()){
                sPrefs.loadSharedPreferencesFromFile(prefs);
            } else {
                sPrefs.saveSharedPreferencesToFile(prefs);
            }
        }
    }

    private void checkKeys(){
        File settings = new File("/data/data/" + getPackageName() + "/shared_prefs/" + Constants.NEW_APP_PREFS + ".xml");
        if (settings.exists()){
            sPrefs = new SharedPrefs(SplashScreen.this);
            boolean loadedStr = sPrefs.isPassString();
            if (loadedStr) {
                // single screen
                attachSingle();
            } else {
                // double screen
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

    public void firstLogin(String firstStr, String secondStr){
        if(firstStr.equals("") & secondStr.equals("")){
            firstPass.setHint(R.string.set_att_if_all_field_empty);
            secondPass.setHint(R.string.set_att_if_all_field_empty);
        } else {
            if (firstStr.matches("")){
                firstPass.setHint(R.string.set_att_if_all_field_empty);
                secondPass.setText("");
            } else {
                if (secondStr.matches("")){
                    secondPass.setHint(R.string.set_att_if_all_field_empty);
                    firstPass.setText("");
                } else {
                    if (firstStr.equals(secondStr)) {
                        try {
                            savePass(firstPass.getText().toString().trim());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Intent intentMain = new Intent(this, MainScreen.class);
                        startActivity(intentMain);
                        finish();
                    } else {
                        firstPass.setText("");
                        secondPass.setText("");
                    }
                }
            }}
    }

    void savePass(String insertedPass) throws UnsupportedEncodingException {
        byte[] passByted;
        passByted = insertedPass.getBytes("UTF-8");
        String passEncrypted = Base64.encodeToString(passByted, Base64.DEFAULT).trim();
        sPrefs = new SharedPrefs(SplashScreen.this);
        sPrefs.savePassPrefs(passEncrypted);
    }

    private void checkPrefs(){
        sPrefs = new SharedPrefs(SplashScreen.this);
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_THEME)){
            sPrefs.savePrefs(Constants.NEW_PREFERENCES_THEME, "6");
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_SCREEN)){
            sPrefs.savePrefs(Constants.NEW_PREFERENCES_SCREEN, Constants.SCREEN_AUTO);
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_DATE_FORMAT)){
            sPrefs.saveInt(Constants.NEW_PREFERENCES_DATE_FORMAT, Constants.DATE_EU);
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_EDIT_LENGHT)){
            sPrefs.saveInt(Constants.NEW_PREFERENCES_EDIT_LENGHT, 4);
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_EDIT_OLD_LENGHT)){
            sPrefs.saveInt(Constants.NEW_PREFERENCES_EDIT_OLD_LENGHT, 4);
        }
        if (!sPrefs.isString(Constants.NEW_PREFERENCES_CHECKBOX)){
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_CHECKBOX, false);
        }
        if (new ModuleManager().isPro()){
            if (!sPrefs.isString(Constants.NEW_PREFERENCES_AUTO_SYNC)){
                sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_SYNC, false);
            }
            if (!sPrefs.isString(Constants.NEW_PREFERENCES_AUTO_BACKUP)){
                sPrefs.saveBoolean(Constants.NEW_PREFERENCES_AUTO_BACKUP, false);
            }
        }
    }
}