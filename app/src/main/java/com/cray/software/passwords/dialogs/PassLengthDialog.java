package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class PassLengthDialog extends Activity {

    Button buttonOk;
    NumberPicker passLengthPicker;
    SharedPrefs sPrefs;
    ColorSetter cSetter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_length_change_dialog);

        cSetter = new ColorSetter(PassLengthDialog.this);
        sPrefs = new SharedPrefs(PassLengthDialog.this);
        final int oldP = sPrefs.loadInt(Constants.NEW_PREFERENCES_EDIT_LENGHT);

        passLengthPicker = (NumberPicker) findViewById(R.id.passLengthPicker);
        passLengthPicker.setMinValue(1);
        passLengthPicker.setMaxValue(10);
        passLengthPicker.setValue(oldP);

        buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pLPick = passLengthPicker.getValue();
                sPrefs = new SharedPrefs(PassLengthDialog.this);
                sPrefs.saveInt(Constants.NEW_PREFERENCES_EDIT_LENGHT, pLPick);
                sPrefs.saveInt(Constants.NEW_PREFERENCES_EDIT_OLD_LENGHT, oldP);
                finish();
            }
        });
    }
}
