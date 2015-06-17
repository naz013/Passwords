package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cray.software.passwords.R;

public class ProMarket extends Activity {
    Button buyButton;
    String MARKET_APP_PASSWORDS_PRO = "com.cray.software.passwordspro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_layout);

        buyButton = (Button) findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MARKET_APP_PASSWORDS_PRO));
                startActivity(intent);
            }
        });
    }
}
