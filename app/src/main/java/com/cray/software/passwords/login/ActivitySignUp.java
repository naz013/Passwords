package com.cray.software.passwords.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.cray.software.passwords.MainActivity;
import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.ActivitySignUpBinding;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Permissions;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

public class ActivitySignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        ColorSetter cs = new ColorSetter(this);
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cs.getColor(R.color.colorGrayDark));
        }
        setFilter();
        binding.secondPass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                tryLogin();
                return true;
            }
            return false;
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> tryLogin());
        loadPhoto();
        setFont();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Permissions.checkPermission(this, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL)) {
            Permissions.requestPermission(this, 102, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL);
        }
    }

    private void setFilter() {
        int passLengthInt = Prefs.getInstance(this).getPasswordLength();
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(passLengthInt);
        binding.firstPass.setFilters(FilterArray);
        binding.secondPass.setFilters(FilterArray);
    }

    private void tryLogin() {
        String firstStr = binding.firstPass.getText().toString();
        String secondStr = binding.secondPass.getText().toString();
        firstLogin(firstStr, secondStr);
    }

    public void firstLogin(String firstStr, String secondStr) {
        if (firstStr.equals("") & secondStr.equals("")) {
            binding.firstPass.setHint(R.string.set_att_if_all_field_empty);
            binding.secondPass.setHint(R.string.set_att_if_all_field_empty);
        } else {
            if (firstStr.matches("")) {
                binding.firstPass.setHint(R.string.set_att_if_all_field_empty);
                binding.secondPass.setText("");
            } else {
                if (secondStr.matches("")) {
                    binding.secondPass.setHint(R.string.set_att_if_all_field_empty);
                    binding.firstPass.setText("");
                } else {
                    if (firstStr.equals(secondStr)) {
                        savePass(binding.firstPass.getText().toString().trim());
                        Intent intentMain = new Intent(this, MainActivity.class);
                        startActivity(intentMain);
                        finish();
                    } else {
                        binding.firstPass.setText("");
                        binding.secondPass.setText("");
                    }
                }
            }
        }
    }

    void savePass(String insertedPass) {
        Prefs.getInstance(this).savePassPrefs(SuperUtil.encrypt(insertedPass));
    }

    private void setFont() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        binding.loginButton.setTypeface(typeface);
        binding.firstPass.setTypeface(typeface);
        binding.secondPass.setTypeface(typeface);
        binding.textView.setTypeface(typeface);
    }

    private void loadPhoto() {
        Glide.with(this)
                .load("https://unsplash.it/768/1280?image=122")
                .into(binding.bgPhoto);
    }
}
