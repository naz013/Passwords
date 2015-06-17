package com.cray.software.passwords.dialogs;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.ColorSetter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThanksDialog extends ActionBarActivity {

    TextView textView;
    ActionBar ab;
    ColorSetter cSetter = new ColorSetter(ThanksDialog.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);
        cSetter = new ColorSetter(ThanksDialog.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayShowTitleEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayUseLogoEnabled(true);
            ab.setIcon(R.drawable.ic_security_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.license_settings_title);
            viewSetter(ab);
        }

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(readFile());
    }

    private String readFile(){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader input;
            input = new InputStreamReader(getAssets().open("files/LICENSE.txt"));
            reader = new BufferedReader(input);

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private void viewSetter(ActionBar ab){
        cSetter = new ColorSetter(ThanksDialog.this);
        ab.setBackgroundDrawable(new ColorDrawable(cSetter.colorSetter()));
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
