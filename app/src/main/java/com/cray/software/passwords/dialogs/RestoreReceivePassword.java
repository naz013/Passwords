package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

public class RestoreReceivePassword extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_password_layout);

        TextView receivedPassword = findViewById(R.id.receivedPassword);
        receivedPassword.setText("{ " + SuperUtil.decrypt(Prefs.getInstance(this).loadPassPrefs()) + " }");

        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());
    }
}
