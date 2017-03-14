package com.cray.software.passwords.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cray.software.passwords.MainActivity;
import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.ActivityLoginBinding;
import com.cray.software.passwords.dialogs.RestoreInsertMail;
import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class ActivityLogin extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private SharedPrefs prefs;
    private SharedPreferences appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        prefs = new SharedPrefs(this);
        appSettings = getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
        setFilter();
        binding.forgotPassword.setVisibility(View.INVISIBLE);
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryRestore();
            }
        });

        binding.loginPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    tryLogin();
                    return true;
                }
                return false;
            }
        });

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });
        loadPhoto();
    }

    private void loadPhoto() {
        Glide.with(this)
                .load("https://unsplash.it/768/1280?image=43")
                .into(binding.bgPhoto);
    }

    private void setFilter() {
        int passLengthInt = prefs.loadInt(Constants.NEW_PREFERENCES_EDIT_LENGHT);
        String loadedPass = appSettings.getString(Constants.NEW_APP_PREFERENCES_LOGIN, "").trim();
        loadedPass = Crypter.decrypt(loadedPass);
        int loadPassLength = 0;
        if (loadedPass != null) {
            loadPassLength = loadedPass.length();
        }
        if (passLengthInt == loadPassLength) {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(passLengthInt);
            binding.loginPass.setFilters(FilterArray);
        } else {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(loadPassLength);
            binding.loginPass.setFilters(FilterArray);
        }
    }

    private void tryLogin() {
        appSettings = getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
        String loadedPass = appSettings.getString(Constants.NEW_APP_PREFERENCES_LOGIN, "");
        String loginPassStr = binding.loginPass.getText().toString().trim();
        loginApp(loadedPass, loginPassStr);
    }

    public void loginApp(String loadedPass, String loginPassStr) {
        String passDecrypted = Crypter.decrypt(loadedPass);
        if (TextUtils.isEmpty(loginPassStr)) {
            binding.loginPass.setError(getResources().getString(R.string.set_att_if_all_field_empty));
        } else {
            if (loginPassStr.equals(passDecrypted)) {
                Intent intentMain = new Intent(this, MainActivity.class);
                startActivity(intentMain);
                finish();
            } else {
                binding.loginPass.setText("");
                binding.loginPass.setError(getResources().getString(R.string.set_att_if_inserted_not_match_saved));
                binding.forgotPassword.setVisibility(View.VISIBLE);
            }
        }
    }

    private void tryRestore() {
        boolean isKey = prefs.isString(Constants.NEW_PREFERENCES_RESTORE_MAIL);
        if (isKey) {
            forgotPassword();
        } else {
            Toast.makeText(this, getString(R.string.not_have_restore_keyword), Toast.LENGTH_SHORT).show();
        }
    }

    public void forgotPassword() {
        Intent intent = new Intent(this, RestoreInsertMail.class);
        startActivity(intent);
    }
}
