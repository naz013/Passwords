package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class RateDialog extends Activity {

    Button buttonRate, rateLater, rateNever;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.rate_dialog_layout);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        sharedPrefs = new SharedPrefs(RateDialog.this);

        buttonRate = (Button) findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefs.saveBoolean(Constants.NEW_PREFERENCES_RATE_SHOW, true);
                launchMarket();
                finish();
            }
        });

        rateLater = (Button) findViewById(R.id.rateLater);
        rateLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefs.saveBoolean(Constants.NEW_PREFERENCES_RATE_SHOW, false);
                sharedPrefs.saveInt(Constants.NEW_PREFERENCES_APP_RUNS_COUNT, 0);
                finish();
            }
        });

        rateNever = (Button) findViewById(R.id.rateNever);
        rateNever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefs.saveBoolean(Constants.NEW_PREFERENCES_RATE_SHOW, true);
                finish();
            }
        });
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Couldn't launch market", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {


    }
}
