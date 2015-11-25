package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cray.software.passwords.R;

import java.util.HashMap;
import java.util.Random;

public class GeneratePassword extends Activity implements View.OnClickListener {
    TextView passLength;
    SeekBar passwordLength;
    CheckBox aZCheck, azCheck, numericCheck, symbolCheck;
    EditText passwordShow;
    Button genButton, genCancel, genInsert;
    static final String AZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String az = "abcdefghijklmnopqrstuvwxyz";
    static final String numeric = "0123456789";
    static final String symbols = "!@#$%()?^*";
    static Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_dialog_layout);

        passLength = (TextView) findViewById(R.id.passLength);
        passwordLength = (SeekBar) findViewById(R.id.passwordLength);
        passwordLength.setMax(32);
        passwordLength.setProgress(16);
        passLength.setText("16");
        passwordLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passLength.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        aZCheck = (CheckBox) findViewById(R.id.aZCheck);
        azCheck = (CheckBox) findViewById(R.id.azCheck);
        numericCheck = (CheckBox) findViewById(R.id.numericCheck);
        symbolCheck = (CheckBox) findViewById(R.id.symbolCheck);

        passwordShow = (EditText) findViewById(R.id.passwordShow);

        genButton = (Button) findViewById(R.id.genButton);
        genButton.setOnClickListener(this);
        genCancel = (Button) findViewById(R.id.genCancel);
        genCancel.setOnClickListener(this);
        genInsert = (Button) findViewById(R.id.genInsert);
        genInsert.setOnClickListener(this);
    }

    private String randomString(int len, int upper, int lower, int num, int sym) {
        String up = null;
        if (upper != 0){
            up = AZ;
        }
        String low = null;
        if (lower != 0){
            low = az;
        }
        String numerical = null;
        if (num != 0){
            numerical = numeric;
        }
        String symbolical = null;
        if (sym != 0){
            symbolical = symbols;
        }
        int mapLength = upper + lower + num + sym;
        HashMap<Integer, String> map = new HashMap<>();

        for (int i = 0; i < mapLength; i++){
            if (up != null && !map.containsValue(up)){
                map.put(i, up);
            }
            if (low != null && !map.containsValue(low)){
                map.put(i, low);
            }
            if (numerical != null && !map.containsValue(numerical)){
                map.put(i, numerical);
            }
            if (symbolical != null && !map.containsValue(symbolical)){
                map.put(i, symbolical);
            }
        }
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ ) {
            Random type = new Random();
            int next = type.nextInt(mapLength);
            String rand = map.get(next);
            sb.append(rand.charAt(rnd.nextInt(rand.length())));
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.genCancel:
                finish();
                break;
            case R.id.genButton:
                if (preGenerateCheck()) {
                    startGenerating();
                }
                break;
            case R.id.genInsert:
                Intent intent = new Intent();
                String genPass = passwordShow.getText().toString().trim();
                intent.putExtra("GENERATED_PASSWORD", genPass);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private boolean preGenerateCheck(){
        boolean status;
        if (aZCheck.isChecked() || azCheck.isChecked() || numericCheck.isChecked() || symbolCheck.isChecked()){
            if (passwordLength.getProgress() > 0){
                status = true;
            } else {
                status = false;
                Toast.makeText(GeneratePassword.this, getString(R.string.pass_length), Toast.LENGTH_SHORT).show();
            }
        } else {
            status = false;
            aZCheck.setChecked(true);
            Toast.makeText(GeneratePassword.this, getString(R.string.pass_checks), Toast.LENGTH_SHORT).show();
        }
        return status;
    }

    private void startGenerating() {
        int aZCh = 0;
        if (aZCheck.isChecked()) {
            aZCh = 1;
        }
        int azCh = 0;
        if (azCheck.isChecked()) {
            azCh = 1;
        }
        int numCh = 0;
        if (numericCheck.isChecked()) {
            numCh = 1;
        }
        int symCh = 0;
        if (symbolCheck.isChecked()) {
            symCh = 1;
        }
        String password = randomString(passwordLength.getProgress(), aZCh, azCh, numCh, symCh);
        passwordShow.setText(password);
    }
}