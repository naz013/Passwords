package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

import java.lang.ref.WeakReference;

public class PassChangeDialog extends Activity {

    private EditText newPassInsert, oldPassInsert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_change_dialog);

        int passLengthInt = Prefs.getInstance(this).getPasswordLength();
        int passOldLengthInt = Prefs.getInstance(this).getOldPasswordLength();

        WeakReference<String> passDecrypted = new WeakReference<>(SuperUtil.decrypt(Prefs.getInstance(this).loadPassPrefs()));
        int loadPassLength = passDecrypted.get().length();
        oldPassInsert = findViewById(R.id.oldPassInsert);

        if (passOldLengthInt == loadPassLength) {
            InputFilter[] FilterOldArray = new InputFilter[1];
            FilterOldArray[0] = new InputFilter.LengthFilter(passOldLengthInt);
            oldPassInsert.setFilters(FilterOldArray);
        } else {
            InputFilter[] FilterOldArray = new InputFilter[1];
            FilterOldArray[0] = new InputFilter.LengthFilter(loadPassLength);
            oldPassInsert.setFilters(FilterOldArray);
        }

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(passLengthInt);
        newPassInsert = findViewById(R.id.newPassInsert);
        newPassInsert.setFilters(FilterArray);

        Button buttonOk = findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(v -> {
            String oldPassStr = oldPassInsert.getText().toString().trim();
            String newPassStr = newPassInsert.getText().toString().trim();
            if (oldPassStr.equals("") & newPassStr.equals("")) {
                oldPassInsert.setText("");
                oldPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                newPassInsert.setText("");
                newPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
            } else {
                if (oldPassStr.equals("")) {
                    oldPassInsert.setText("");
                    oldPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                } else {
                    if (newPassStr.equals("")) {
                        newPassInsert.setText("");
                        newPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                    } else {
                        WeakReference<String> loadedPass1 = new WeakReference<>(SuperUtil.decrypt(Prefs.getInstance(this).loadPassPrefs()));
                        if (oldPassStr.equals(loadedPass1.get())) {
                            String insertedPass = newPassInsert.getText().toString().trim();
                            Prefs.getInstance(this).savePassPrefs(SuperUtil.encrypt(insertedPass));
                            finish();
                        } else {
                            oldPassInsert.setText("");
                            oldPassInsert.setError(getResources().getString(R.string.set_att_if_inserted_not_match_saved));
                        }
                    }
                }
            }
        });
    }
}
