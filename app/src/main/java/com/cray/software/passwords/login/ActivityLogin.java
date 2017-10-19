package com.cray.software.passwords.login;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.cray.software.passwords.MainActivity;
import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.ActivityLoginBinding;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Permissions;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;
import com.hanks.passcodeview.PasscodeView;

public class ActivityLogin extends AppCompatActivity {

    private static final String TAG = "ActivityLogin";

    private ActivityLoginBinding binding;

    private Prefs prefs;
    private ColorSetter cs = new ColorSetter(this);
    private int failTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cs.getColor(cs.colorPrimaryDark()));
        }
        prefs = Prefs.getInstance(this);

        binding.passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(ActivityLogin.this, R.string.password_not_match, Toast.LENGTH_SHORT).show();
                failTimes++;
                if (failTimes >= 3) showRestoreDialog();
            }

            @Override
            public void onSuccess(String number) {
                Intent intentMain = new Intent(ActivityLogin.this, MainActivity.class);
                startActivity(intentMain);
                finish();
            }
        });

        setFilter();
    }

    private void showRestoreDialog() {
        if (Prefs.getInstance(this).hasSecureKey(Prefs.NEW_PREFERENCES_RESTORE_MAIL)) {
            Dialogues.showSimpleDialog(this, (dialog, field, layout) -> {
                String keyword = field.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    layout.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                    layout.setErrorEnabled(true);
                } else {
                    if (keyword.equals(SuperUtil.decrypt(Prefs.getInstance(ActivityLogin.this).getRestoreWord()))) {
                        dialog.dismiss();
                        showPassword();
                    } else {
                        layout.setError(getString(R.string.keyword_not_match));
                        layout.setErrorEnabled(true);
                    }
                }
            }, getString(R.string.keyword_setting_title), getString(R.string.restore_dialog_enter_keyword));
        }
    }

    private void showPassword() {
        Dialogues.showTextDialog(this, Dialog::dismiss, getString(R.string.received_pass_title),
                "{ " + SuperUtil.decrypt(Prefs.getInstance(this).loadPassPrefs()) + " }");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Permissions.checkPermission(this, Permissions.READ_EXTERNAL,
                Permissions.WRITE_EXTERNAL)) {
            Permissions.requestPermission(this, 102, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL);
        }
    }

    private void setFilter() {
        int passLengthInt = prefs.getPasswordLength();
        String loadedPass = prefs.loadPassPrefs();
        loadedPass = SuperUtil.decrypt(loadedPass);
        if (passLengthInt == loadedPass.length()) {
            binding.passcodeView.setPasscodeLength(passLengthInt);
        } else {
            binding.passcodeView.setPasscodeLength(loadedPass.length());
        }
        binding.passcodeView.setLocalPasscode(loadedPass);
        binding.passcodeView.setBackgroundResource(cs.colorPrimary());
        binding.passcodeView.invalidate();
    }
}
