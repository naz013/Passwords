package com.cray.software.passwords.dialogs;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.ThemedActivity;
import com.cray.software.passwords.views.roboto.RoboTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThanksDialog extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.license_settings_title);

        RoboTextView textView = findViewById(R.id.textView);
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
