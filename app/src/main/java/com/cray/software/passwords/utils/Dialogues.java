package com.cray.software.passwords.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.SeekBar;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.DialogSeekBarBinding;
import com.cray.software.passwords.databinding.DialogSingleFieldBinding;
import com.cray.software.passwords.databinding.DialogTextViewBinding;
import com.cray.software.passwords.databinding.DialogTwoFieldsBinding;

import java.lang.ref.WeakReference;

import static com.cray.software.passwords.utils.ThemeUtil.THEME_AMOLED;

/**
 * Copyright 2017 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Dialogues {

    public static AlertDialog.Builder getDialog(@NonNull Context context) {
        if (Prefs.getInstance(context).getAppTheme() == THEME_AMOLED) {
            return new AlertDialog.Builder(context, ThemeUtil.getInstance(context).getDialogStyle());
        } else {
            return new AlertDialog.Builder(context);
        }
    }

    public static void showTextDialog(Activity activity, DialogOkClick click, String title, String value) {
        AlertDialog.Builder builder = getDialog(activity);
        DialogTextViewBinding binding = DialogTextViewBinding.inflate(LayoutInflater.from(activity));
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        binding.dialogTitle.setText(title);
        binding.valueView.setText(value);
        binding.buttonOk.setOnClickListener(view -> click.onClick(dialog));
        dialog.show();
    }

    public static void showSeekDialog(Activity activity, DialogSeekOkClick click, String title, int current) {
        AlertDialog.Builder builder = getDialog(activity);
        DialogSeekBarBinding binding = DialogSeekBarBinding.inflate(LayoutInflater.from(activity));
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        binding.dialogTitle.setText(title);
        binding.seekView.setProgress(current - 1);
        binding.valueView.setText(String.valueOf(current));
        binding.seekView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.valueView.setText(String.valueOf(i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.buttonOk.setOnClickListener(view -> click.onClick(dialog, binding.seekView.getProgress() + 1));
        dialog.show();
    }

    public static void showSimpleDialog(Activity activity, DialogOneOkClick click, String title, String hint) {
        AlertDialog.Builder builder = getDialog(activity);
        DialogSingleFieldBinding binding = DialogSingleFieldBinding.inflate(LayoutInflater.from(activity));
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        binding.dialogTitle.setText(title);
        binding.inputField.setHint(hint);
        binding.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.inputLayout.setError("");
                binding.inputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
        binding.buttonOk.setOnClickListener(view -> click.onClick(dialog, binding.inputField, binding.inputLayout));
        dialog.show();
    }

    public static void showPasswordChangeDialog(Activity activity, DialogTwoOkClick click, String title, String hint1, String hint2) {
        AlertDialog.Builder builder = getDialog(activity);
        DialogTwoFieldsBinding binding = DialogTwoFieldsBinding.inflate(LayoutInflater.from(activity));
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        binding.dialogTitle.setText(title);

        binding.inputField.setHint(hint1);
        binding.input2Field.setHint(hint2);

        binding.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.inputLayout.setError("");
                binding.inputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
        binding.input2Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.input2Layout.setError("");
                binding.input2Layout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        int passLengthInt = Prefs.getInstance(activity).getPasswordLength();
        int passOldLengthInt = Prefs.getInstance(activity).getOldPasswordLength();

        WeakReference<String> passDecrypted = new WeakReference<>(SuperUtil.decrypt(Prefs.getInstance(activity).loadPassPrefs()));
        int loadPassLength = passDecrypted.get().length();

        if (passOldLengthInt == loadPassLength) {
            InputFilter[] FilterOldArray = new InputFilter[1];
            FilterOldArray[0] = new InputFilter.LengthFilter(passOldLengthInt);
            binding.inputField.setFilters(FilterOldArray);
        } else {
            InputFilter[] FilterOldArray = new InputFilter[1];
            FilterOldArray[0] = new InputFilter.LengthFilter(loadPassLength);
            binding.inputField.setFilters(FilterOldArray);
        }

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(passLengthInt);
        binding.input2Field.setFilters(FilterArray);

        binding.buttonOk.setOnClickListener(view -> click.onClick(dialog, binding.inputField,
                binding.inputLayout, binding.input2Field, binding.input2Layout));
        dialog.show();
    }

    public static void showRateDialog(Activity activity) {
        AlertDialog.Builder builder = getDialog(activity);
        builder.setMessage(R.string.rate_question);
        builder.setPositiveButton(R.string.button_rate, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Prefs.getInstance(activity).setRateShowed(true);
            SuperUtil.launchMarket(activity);
        });
        builder.setNegativeButton(R.string.button_never, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Prefs.getInstance(activity).setRateShowed(true);
        });
        builder.setNeutralButton(R.string.button_later, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Prefs.getInstance(activity).setRateShowed(false);
            Prefs.getInstance(activity).setRunsCount(0);
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    public interface DialogOneOkClick {
        void onClick(AlertDialog dialog, EditText field, TextInputLayout layout);
    }

    public interface DialogTwoOkClick {
        void onClick(AlertDialog dialog, EditText field, TextInputLayout layout, EditText field2, TextInputLayout layout2);
    }

    public interface DialogSeekOkClick {
        void onClick(AlertDialog dialog, int value);
    }

    public interface DialogOkClick {
        void onClick(AlertDialog dialog);
    }
}
