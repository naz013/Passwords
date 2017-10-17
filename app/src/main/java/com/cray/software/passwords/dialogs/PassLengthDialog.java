package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.Prefs;

public class PassLengthDialog extends Activity {

    private NumberPicker passLengthPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_length_change_dialog);

        final int oldP = Prefs.getInstance(this).getOldPasswordLength();

        passLengthPicker = findViewById(R.id.passLengthPicker);
        passLengthPicker.setMinValue(1);
        passLengthPicker.setMaxValue(10);
        passLengthPicker.setValue(oldP);

        Button buttonOk = findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(v -> {
            Prefs.getInstance(this).setPasswordLength(passLengthPicker.getValue());
            Prefs.getInstance(this).setOldPasswordLength(oldP);
            finish();
        });
    }
}
