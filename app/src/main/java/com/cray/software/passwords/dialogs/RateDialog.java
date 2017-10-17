package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.cray.software.passwords.R;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

public class RateDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.rate_dialog_layout);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button buttonRate = findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(v -> {
            Prefs.getInstance(this).setRateShowed(true);
            SuperUtil.launchMarket(this);
            finish();
        });

        Button rateLater = findViewById(R.id.rateLater);
        rateLater.setOnClickListener(v -> {
            Prefs.getInstance(this).setRateShowed(false);
            Prefs.getInstance(this).setRunsCount(0);
            finish();
        });

        Button rateNever = findViewById(R.id.rateNever);
        rateNever.setOnClickListener(v -> {
            Prefs.getInstance(this).setRateShowed(true);
            finish();
        });
    }

    @Override
    public void onBackPressed() {


    }
}
