package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

public class ChangeKeyword extends Activity {

    private EditText restoreMailInsert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_mail_layout);

        restoreMailInsert = findViewById(R.id.restoreMailInsert);

        Button showPassButton = findViewById(R.id.showPassButton);
        showPassButton.setText(getString(R.string.button_save));
        showPassButton.setOnClickListener(v -> {
            String mailStr = restoreMailInsert.getText().toString().trim();

            if (TextUtils.isEmpty(mailStr)){
                restoreMailInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
            } else {
                Prefs.getInstance(this).setRestoreWord(SuperUtil.encrypt(mailStr));
                finish();
            }
        });
    }
}
