package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.DialogGeneratePasswordBinding;
import com.cray.software.passwords.utils.ThemeUtil;

import java.util.HashMap;
import java.util.Random;

public class GeneratePassword extends Activity implements View.OnClickListener {

    private static final String AZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String az = "abcdefghijklmnopqrstuvwxyz";
    private static final String numeric = "0123456789";
    private static final String symbols = "!@#$%()?^*";

    private DialogGeneratePasswordBinding binding;
    @NonNull
    private Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil themeUtil = ThemeUtil.getInstance(this);
        if (themeUtil != null) setTheme(themeUtil.getDialogStyle());
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_generate_password);
        if (themeUtil != null) binding.bgView.setBackgroundColor(themeUtil.getBackgroundStyle());

        binding.passwordLength.setMax(32);
        binding.passwordLength.setProgress(16);
        binding.passLength.setText(String.valueOf(binding.passwordLength.getProgress()));
        binding.passwordLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.passLength.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button genButton = findViewById(R.id.genButton);
        genButton.setOnClickListener(this);
        Button genCancel = findViewById(R.id.genCancel);
        genCancel.setOnClickListener(this);
        Button genInsert = findViewById(R.id.genInsert);
        genInsert.setOnClickListener(this);
    }

    private String randomString(int len, int upper, int lower, int num, int sym) {
        String up = null;
        if (upper != 0) {
            up = AZ;
        }
        String low = null;
        if (lower != 0) {
            low = az;
        }
        String numerical = null;
        if (num != 0) {
            numerical = numeric;
        }
        String symbolical = null;
        if (sym != 0) {
            symbolical = symbols;
        }
        int mapLength = upper + lower + num + sym;
        HashMap<Integer, String> map = new HashMap<>();

        for (int i = 0; i < mapLength; i++) {
            if (up != null && !map.containsValue(up)) {
                map.put(i, up);
            }
            if (low != null && !map.containsValue(low)) {
                map.put(i, low);
            }
            if (numerical != null && !map.containsValue(numerical)) {
                map.put(i, numerical);
            }
            if (symbolical != null && !map.containsValue(symbolical)) {
                map.put(i, symbolical);
            }
        }
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            Random type = new Random();
            int next = type.nextInt(mapLength);
            String rand = map.get(next);
            sb.append(rand.charAt(rnd.nextInt(rand.length())));
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                String genPass = binding.passwordShow.getText().toString().trim();
                intent.putExtra("GENERATED_PASSWORD", genPass);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private boolean preGenerateCheck() {
        boolean status;
        if (binding.aZCheck.isChecked() || binding.azCheck.isChecked() || binding.numericCheck.isChecked() || binding.symbolCheck.isChecked()) {
            if (binding.passwordLength.getProgress() > 0) {
                status = true;
            } else {
                status = false;
                Toast.makeText(GeneratePassword.this, getString(R.string.pass_length), Toast.LENGTH_SHORT).show();
            }
        } else {
            status = false;
            binding.aZCheck.setChecked(true);
            Toast.makeText(GeneratePassword.this, getString(R.string.pass_checks), Toast.LENGTH_SHORT).show();
        }
        return status;
    }

    private void startGenerating() {
        int aZCh = 0;
        if (binding.aZCheck.isChecked()) {
            aZCh = 1;
        }
        int azCh = 0;
        if (binding.azCheck.isChecked()) {
            azCh = 1;
        }
        int numCh = 0;
        if (binding.numericCheck.isChecked()) {
            numCh = 1;
        }
        int symCh = 0;
        if (binding.symbolCheck.isChecked()) {
            symCh = 1;
        }
        String password = randomString(binding.passwordLength.getProgress(), aZCh, azCh, numCh, symCh);
        binding.passwordShow.setText(password);
    }
}
