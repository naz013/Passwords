package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cray.software.passwords.R;
import com.cray.software.passwords.views.roboto.RoboButton;

public class ProMarket extends Activity {

    private static final String MARKET_APP_PASSWORDS_PRO = "com.cray.software.passwordspro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_layout);

        RoboButton buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + MARKET_APP_PASSWORDS_PRO));
            startActivity(intent);
        });
    }
}
