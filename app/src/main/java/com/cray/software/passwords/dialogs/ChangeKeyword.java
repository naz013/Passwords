package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class ChangeKeyword extends Activity {

    EditText restoreMailInsert;
    Button showPassButton;

    SharedPrefs sPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_mail_layout);

        sPrefs = new SharedPrefs(ChangeKeyword.this);

        restoreMailInsert = (EditText) findViewById(R.id.restoreMailInsert);

        showPassButton = (Button) findViewById(R.id.showPassButton);
        showPassButton.setText(getString(R.string.button_save));
        showPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailStr = restoreMailInsert.getText().toString().trim();

                if (mailStr.matches("")){
                    restoreMailInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                } else {
                    String mailCrypted = new Crypter().encrypt(mailStr);
                    sPrefs = new SharedPrefs(ChangeKeyword.this);
                    sPrefs.savePrefs(Constants.NEW_PREFERENCES_RESTORE_MAIL, mailCrypted);
                    finish();
                }
            }
        });
    }
}
