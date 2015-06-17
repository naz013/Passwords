package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

import java.io.UnsupportedEncodingException;

public class RestoreInsertMail extends Activity {

    EditText restoreMailInsert;
    Button showPassButton;
    String mailDecrypted;

    SharedPrefs sPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_mail_layout);

        sPrefs = new SharedPrefs(RestoreInsertMail.this);
        final String loadedMail = sPrefs.loadPrefs(Constants.NEW_PREFERENCES_RESTORE_MAIL);

        restoreMailInsert = (EditText) findViewById(R.id.restoreMailInsert);

        showPassButton = (Button) findViewById(R.id.showPassButton);
        showPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailInserted = restoreMailInsert.getText().toString().trim();
                byte[] byteMail = Base64.decode(loadedMail, Base64.DEFAULT);

                try {
                    mailDecrypted = new String(byteMail, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                if (mailInserted.matches("")){
                    restoreMailInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                } else {
                    if (mailInserted.equals(mailDecrypted)){
                        Intent intent = new Intent(getApplicationContext(), RestoreRecivePassword.class);
                        startActivity(intent);
                        finish();
                    } else {
                        restoreMailInsert.setError(getString(R.string.keyword_not_match));
                    }
                }
            }
        });
    }
}
