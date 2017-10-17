package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

public class RestoreInsertMail extends Activity {

    private EditText restoreMailInsert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_mail_layout);

        restoreMailInsert = findViewById(R.id.restoreMailInsert);

        Button showPassButton = findViewById(R.id.showPassButton);
        showPassButton.setOnClickListener(v -> {
            String mailInserted = restoreMailInsert.getText().toString().trim();
            if (TextUtils.isEmpty(mailInserted)) {
                restoreMailInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
            } else {
                if (mailInserted.equals(SuperUtil.decrypt(Prefs.getInstance(this).getRestoreWord()))) {
                    Intent intent = new Intent(getApplicationContext(), RestoreReceivePassword.class);
                    startActivity(intent);
                    finish();
                } else {
                    restoreMailInsert.setError(getString(R.string.keyword_not_match));
                }
            }
        });
    }
}
