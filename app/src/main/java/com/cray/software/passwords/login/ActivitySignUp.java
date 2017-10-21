package com.cray.software.passwords.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.Toast;

import com.cray.software.passwords.MainActivity;
import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.ActivitySignUpBinding;
import com.cray.software.passwords.helpers.Permissions;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;
import com.cray.software.passwords.utils.ThemedActivity;
import com.hanks.passcodeview.PasscodeView;

public class ActivitySignUp extends ThemedActivity {

    private ActivitySignUpBinding binding;

    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        prefs = Prefs.getInstance(this);
        binding.passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(ActivitySignUp.this, R.string.password_not_match, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String number) {
                prefs.savePassPrefs(SuperUtil.encrypt(number));
                Intent intentMain = new Intent(ActivitySignUp.this, MainActivity.class);
                startActivity(intentMain);
                finish();
            }
        });

        setFilter();
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
        binding.passcodeView.setPasscodeLength(passLengthInt);
        binding.passcodeView.invalidate();
    }
}
