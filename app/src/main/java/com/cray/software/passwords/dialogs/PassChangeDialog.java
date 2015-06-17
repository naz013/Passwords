package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class PassChangeDialog extends Activity {
    EditText newPassInsert, oldPassInsert;
    Button buttonOk;
    String pass_decrypted;
    SharedPrefs sPrefs;
    ColorSetter cSetter;
    Crypter crypter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_change_dialog);

        cSetter = new ColorSetter(PassChangeDialog.this);
        crypter = new Crypter();

        sPrefs = new SharedPrefs(PassChangeDialog.this);
        int passLenghtInt = sPrefs.loadInt(Constants.NEW_PREFERENCES_EDIT_LENGHT);
        int passOldLenghtInt = sPrefs.loadInt(Constants.NEW_PREFERENCES_EDIT_OLD_LENGHT);

        String loadedPass = sPrefs.loadPassPrefs().trim();
        String passDecrypted = crypter.decrypt(loadedPass);
        int loadPassLength = passDecrypted.length();
        oldPassInsert = (EditText) findViewById(R.id.oldPassInsert);

        if(passOldLenghtInt == loadPassLength) {
            InputFilter[] FilterOldArray = new InputFilter[1];
            FilterOldArray[0] = new InputFilter.LengthFilter(passOldLenghtInt);
            oldPassInsert.setFilters(FilterOldArray);
        } else {
            InputFilter[] FilterOldArray = new InputFilter[1];
            FilterOldArray[0] = new InputFilter.LengthFilter(loadPassLength);
            oldPassInsert.setFilters(FilterOldArray);
        }

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(passLenghtInt);
        newPassInsert = (EditText) findViewById(R.id.newPassInsert);
        newPassInsert.setFilters(FilterArray);

        buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassStr = oldPassInsert.getText().toString().trim();
                String newPassStr = newPassInsert.getText().toString().trim();
                if(oldPassStr.equals("") & newPassStr.equals("")){
                    oldPassInsert.setText("");
                    oldPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                    newPassInsert.setText("");
                    newPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                }
                else {
                    if(oldPassStr.equals("")){
                        oldPassInsert.setText("");
                        oldPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                    }
                    else {
                        if (newPassStr.equals("")){
                            newPassInsert.setText("");
                            newPassInsert.setError(getResources().getString(R.string.set_att_if_all_field_empty));
                        }
                        else {
                            sPrefs = new SharedPrefs(PassChangeDialog.this);
                            String loadedPass = sPrefs.loadPassPrefs();
                            pass_decrypted = crypter.decrypt(loadedPass);
                            if (oldPassStr.equals(pass_decrypted)){
                                String insertedPass = newPassInsert.getText().toString().trim();
                                String passCrypted = crypter.encrypt(insertedPass);
                                sPrefs.savePassPrefs(passCrypted);
                                finish();
                            }
                            else {
                                oldPassInsert.setText("");
                                oldPassInsert.setError(getResources().getString(R.string.set_att_if_inserted_not_match_saved));
                            }
                        }
                    }
                }
            }
        });
    }
}
