package com.cray.software.passwords.dialogs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.Utils;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.ThemedActivity;

public class ThemerDialog extends ThemedActivity {

    private ImageButton red, green, blue, yellow, greenLight, blueLight, cyan, purple,
            amber, orange, pink, teal, deepPurple, deepOrange, indigo, lime;
    private FloatingActionButton mFab;

    private Toolbar toolbar;
    private int prevId;

    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_color_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getString(R.string.app_theme_title));

        red = findViewById(R.id.red_checkbox);
        purple = findViewById(R.id.violet_checkbox);
        green = findViewById(R.id.green_checkbox);
        greenLight = findViewById(R.id.light_green_checkbox);
        blue = findViewById(R.id.blue_checkbox);
        blueLight = findViewById(R.id.light_blue_checkbox);
        yellow = findViewById(R.id.yellow_checkbox);
        orange = findViewById(R.id.orange_checkbox);
        cyan = findViewById(R.id.grey_checkbox);
        pink = findViewById(R.id.pink_checkbox);
        teal = findViewById(R.id.sand_checkbox);
        amber = findViewById(R.id.brown_checkbox);

        deepPurple = findViewById(R.id.deepPurple);
        indigo = findViewById(R.id.indigoCheckbox);
        lime = findViewById(R.id.limeCheckbox);
        deepOrange = findViewById(R.id.deepOrange);

        LinearLayout themeGroupPro = findViewById(R.id.themeGroupPro);
        if (Module.isPro()) {
            themeGroupPro.setVisibility(View.VISIBLE);
        } else {
            themeGroupPro.setVisibility(View.GONE);
        }

        setOnClickListener(red, green, blue, yellow, greenLight, blueLight, cyan, purple,
                amber, orange, pink, teal, deepPurple, deepOrange, indigo, lime);
        setUpRadio();

        mFab = findViewById(R.id.fab);
        mFab.setBackgroundTintList(Utils.getFabState(this, getThemeUtil().colorAccent(), getThemeUtil().colorPrimary()));
    }

    private void setOnClickListener(View... views){
        for (View view : views){
            view.setOnClickListener(listener);
        }
    }

    private View.OnClickListener listener = v -> themeColorSwitch(v.getId());

    private void setUpRadio(){
        int loaded = Prefs.getInstance(this).getAppThemeColor();
        switch (loaded) {
            case 0:
                red.setSelected(true);
                break;
            case 1:
                purple.setSelected(true);
                break;
            case 2:
                greenLight.setSelected(true);
                break;
            case 3:
                green.setSelected(true);
                break;
            case 4:
                blueLight.setSelected(true);
                break;
            case 5:
                blue.setSelected(true);
                break;
            case 6:
                yellow.setSelected(true);
                break;
            case 7:
                orange.setSelected(true);
                break;
            case 8:
                cyan.setSelected(true);
                break;
            case 9:
                pink.setSelected(true);
                break;
            case 10:
                teal.setSelected(true);
                break;
            case 11:
                amber.setSelected(true);
                break;
            case 12:
                deepPurple.setSelected(true);
                break;
            case 13:
                deepOrange.setSelected(true);
                break;
            case 14:
                lime.setSelected(true);
                break;
            case 15:
                indigo.setSelected(true);
                break;
            default:
                blue.setSelected(true);
                break;
        }
    }

    private void themeColorSwitch(int radio){
        if (radio == prevId) return;
        isChanged = true;
        prevId = radio;
        disableAll();
        setSelected(radio);
        switch (radio){
            case R.id.red_checkbox:
                saveColor(0);
                break;
            case R.id.violet_checkbox:
                saveColor(1);
                break;
            case R.id.light_green_checkbox:
                saveColor(2);
                break;
            case R.id.green_checkbox:
                saveColor(3);
                break;
            case R.id.light_blue_checkbox:
                saveColor(4);
                break;
            case R.id.blue_checkbox:
                saveColor(5);
                break;
            case R.id.yellow_checkbox:
                saveColor(6);
                break;
            case R.id.orange_checkbox:
                saveColor(7);
                break;
            case R.id.grey_checkbox:
                saveColor(8);
                break;
            case R.id.pink_checkbox:
                saveColor(9);
                break;
            case R.id.sand_checkbox:
                saveColor(10);
                break;
            case R.id.brown_checkbox:
                saveColor(11);
                break;
            case R.id.deepPurple:
                saveColor(12);
                break;
            case R.id.deepOrange:
                saveColor(13);
                break;
            case R.id.limeCheckbox:
                saveColor(14);
                break;
            case R.id.indigoCheckbox:
                saveColor(15);
                break;
        }
        toolbar.setBackgroundColor(getThemeUtil().getColor(getThemeUtil().colorPrimary()));
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(getThemeUtil().getColor(getThemeUtil().colorPrimaryDark()));
        }
        mFab.setBackgroundTintList(Utils.getFabState(this, getThemeUtil().colorAccent(), getThemeUtil().colorPrimary()));
        mFab.setRippleColor(getThemeUtil().getColor(getThemeUtil().colorPrimary()));
    }

    private void setSelected(int radio) {
        findViewById(radio).setSelected(true);
    }

    private void disableAll() {
        red.setSelected(false);
        purple.setSelected(false);
        greenLight.setSelected(false);
        green.setSelected(false);
        blueLight.setSelected(false);
        blue.setSelected(false);
        yellow.setSelected(false);
        orange.setSelected(false);
        cyan.setSelected(false);
        pink.setSelected(false);
        teal.setSelected(false);
        amber.setSelected(false);
        deepOrange.setSelected(false);
        deepPurple.setSelected(false);
        lime.setSelected(false);
        indigo.setSelected(false);
    }

    private void saveColor(int code) {
        Prefs.getInstance(this).setAppThemeColor(code);
    }

    @Override
    public void onBackPressed() {
        if (isChanged) setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
