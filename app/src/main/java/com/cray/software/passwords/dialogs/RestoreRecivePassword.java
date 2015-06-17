package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.SharedPrefs;

import java.io.UnsupportedEncodingException;

public class RestoreRecivePassword extends Activity {
    TextView receivedPassword;
    Button closeButton;
    String pass_decrypted;

    SharedPrefs sPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_password_layout);

        sPrefs = new SharedPrefs(RestoreRecivePassword.this);
        final String loadedPass = sPrefs.loadPassPrefs();
        byte[] byte_pass = Base64.decode(loadedPass, Base64.DEFAULT);

        try {
            pass_decrypted = new String(byte_pass, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        receivedPassword = (TextView) findViewById(R.id.receivedPassword);
        receivedPassword.setText("{ " + pass_decrypted + " }");

        closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
