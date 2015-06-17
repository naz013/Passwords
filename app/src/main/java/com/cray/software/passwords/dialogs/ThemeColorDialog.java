package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class ThemeColorDialog extends Activity implements View.OnClickListener {
    Button themeClose;
    RadioButton red_checkbox, violet_checkbox, green_checkbox, light_green_checkbox, blue_checkbox, light_blue_checkbox,
            yellow_checkbox, orange_checkbox, grey_checkbox, pink_checkbox, sand_checkbox, brown_checkbox;
    RadioGroup themeGroup, themeGroup2, themeGroup3;
    SharedPrefs sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_color_layout);

        themeClose = (Button) findViewById(R.id.themeClose);
        themeClose.setOnClickListener(this);

        red_checkbox = (RadioButton) findViewById(R.id.red_checkbox);
        violet_checkbox = (RadioButton) findViewById(R.id.violet_checkbox);
        green_checkbox = (RadioButton) findViewById(R.id.green_checkbox);
        light_green_checkbox = (RadioButton) findViewById(R.id.light_green_checkbox);
        blue_checkbox = (RadioButton) findViewById(R.id.blue_checkbox);
        light_blue_checkbox = (RadioButton) findViewById(R.id.light_blue_checkbox);
        yellow_checkbox = (RadioButton) findViewById(R.id.yellow_checkbox);
        orange_checkbox = (RadioButton) findViewById(R.id.orange_checkbox);
        grey_checkbox = (RadioButton) findViewById(R.id.grey_checkbox);
        pink_checkbox = (RadioButton) findViewById(R.id.pink_checkbox);
        sand_checkbox = (RadioButton) findViewById(R.id.sand_checkbox);
        brown_checkbox = (RadioButton) findViewById(R.id.brown_checkbox);

        themeGroup = (RadioGroup) findViewById(R.id.themeGroup);
        themeGroup2 = (RadioGroup) findViewById(R.id.themeGroup2);
        themeGroup3 = (RadioGroup) findViewById(R.id.themeGroup3);

        themeGroup.clearCheck();
        themeGroup2.clearCheck();
        themeGroup3.clearCheck();
        themeGroup.setOnCheckedChangeListener(listener1);
        themeGroup2.setOnCheckedChangeListener(listener2);
        themeGroup3.setOnCheckedChangeListener(listener3);

        setUpRadio();
    }

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                themeGroup2.setOnCheckedChangeListener(null);
                themeGroup3.setOnCheckedChangeListener(null);
                themeGroup2.clearCheck();
                themeGroup3.clearCheck();
                themeGroup2.setOnCheckedChangeListener(listener2);
                themeGroup3.setOnCheckedChangeListener(listener3);
                themeColorSwitch(group.getCheckedRadioButtonId());
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                themeGroup.setOnCheckedChangeListener(null);
                themeGroup3.setOnCheckedChangeListener(null);
                themeGroup.clearCheck();
                themeGroup3.clearCheck();
                themeGroup.setOnCheckedChangeListener(listener1);
                themeGroup3.setOnCheckedChangeListener(listener3);
                themeColorSwitch(group.getCheckedRadioButtonId());
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listener3 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                themeGroup.setOnCheckedChangeListener(null);
                themeGroup2.setOnCheckedChangeListener(null);
                themeGroup.clearCheck();
                themeGroup2.clearCheck();
                themeGroup.setOnCheckedChangeListener(listener1);
                themeGroup2.setOnCheckedChangeListener(listener2);
                themeColorSwitch(group.getCheckedRadioButtonId());
            }
        }
    };

    public void setUpRadio(){
        sPrefs = new SharedPrefs(ThemeColorDialog.this);
        String loaded = sPrefs.loadPrefs(Constants.NEW_PREFERENCES_THEME);
        switch (loaded) {
            case "1":
                red_checkbox.setChecked(true);
                break;
            case "2":
                violet_checkbox.setChecked(true);
                break;
            case "3":
                light_green_checkbox.setChecked(true);
                break;
            case "4":
                green_checkbox.setChecked(true);
                break;
            case "5":
                light_blue_checkbox.setChecked(true);
                break;
            case "6":
                blue_checkbox.setChecked(true);
                break;
            case "7":
                yellow_checkbox.setChecked(true);
                break;
            case "8":
                orange_checkbox.setChecked(true);
                break;
            case "9":
                grey_checkbox.setChecked(true);
                break;
            case "10":
                pink_checkbox.setChecked(true);
                break;
            case "11":
                sand_checkbox.setChecked(true);
                break;
            case "12":
                brown_checkbox.setChecked(true);
                break;
            default:
                green_checkbox.setChecked(true);
                break;
        }
    }


    private void themeColorSwitch(int radio){
        switch (radio){
            case R.id.red_checkbox:
                saveColor("1");
                break;
            case R.id.violet_checkbox:
                saveColor("2");
                break;
            case R.id.green_checkbox:
                saveColor("4");
                break;
            case R.id.light_green_checkbox:
                saveColor("3");
                break;
            case R.id.light_blue_checkbox:
                saveColor("5");
                break;
            case R.id.blue_checkbox:
                saveColor("6");
                break;
            case R.id.yellow_checkbox:
                saveColor("7");
                break;
            case R.id.orange_checkbox:
                saveColor("8");
                break;
            case R.id.grey_checkbox:
                saveColor("9");
                break;
            case R.id.pink_checkbox:
                saveColor("10");
                break;
            case R.id.sand_checkbox:
                saveColor("11");
                break;
            case R.id.brown_checkbox:
                saveColor("12");
                break;
        }
    }

    void saveColor(String string) {
        sPrefs = new SharedPrefs(ThemeColorDialog.this);
        sPrefs.savePrefs(Constants.NEW_PREFERENCES_THEME, string);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.themeClose:
                finish();
                break;
        }
    }
}
