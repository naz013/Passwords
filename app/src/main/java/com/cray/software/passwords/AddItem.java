package com.cray.software.passwords;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.cray.software.passwords.dialogs.GeneratePassword;
import com.cray.software.passwords.dialogs.HelpOverflow;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.DataBase;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.ModuleManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;

public class AddItem extends ActionBarActivity {
    EditText title_enter, login_enter, password_enter, link_enter, comment_enter, date_enter;
    CheckBox showPass;
    Spinner spinnerColor;
    ImageButton generateDialog;
    RelativeLayout showColorRelLay, addActLayout;
    LinearLayout linearLayout;

    ColorSetter cSetter;
    SyncHelper sHelpers = new SyncHelper(AddItem.this);
    SharedPrefs prefs;
    ActionBar ab;

    int myYear = 0;
    int myMonth = 0;
    int myDay = 1;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        cSetter = new ColorSetter(AddItem.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayUseLogoEnabled(false);
            ab.setDisplayShowHomeEnabled(false);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.activity_title);
        }

        addActLayout = (RelativeLayout) findViewById(R.id.addActLayout);
        addActLayout.setVisibility(View.GONE);
        viewSetter(ab);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                addActLayout.startAnimation(slide);
                addActLayout.setVisibility(View.VISIBLE);
            }
        }, 500);

        title_enter = (EditText) findViewById(R.id.title_enter);

        login_enter = (EditText) findViewById(R.id.login_enter);
        login_enter.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        password_enter = (EditText) findViewById(R.id.password_enter);

        link_enter = (EditText) findViewById(R.id.link_enter);

        comment_enter = (EditText) findViewById(R.id.comment_enter);
        comment_enter.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        date_enter = (EditText) findViewById(R.id.date_enter);
        date_enter.setFocusable(false);
        date_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog().show();
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.GONE);
        if (!new ModuleManager().isPro()) {
            adView = (AdView) findViewById(R.id.adView);
            adView.setVisibility(View.GONE);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    linearLayout.setVisibility(View.GONE);
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    linearLayout.setVisibility(View.VISIBLE);
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }

        generateDialog = (ImageButton) findViewById(R.id.generateDialog);
        generateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddItem.this, GeneratePassword.class), Constants.REQUEST_CODE_PASS);
            }
        });

        showPass = (CheckBox) findViewById(R.id.showPass);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    password_enter.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    password_enter.setInputType(129);
                }
            }
        });

        showColorRelLay = (RelativeLayout) findViewById(R.id.showColorRelLay);

        spinnerColor = (Spinner) findViewById(R.id.spinnerColor);
        if(savedInstanceState != null){
            spinnerColor.setSelection(savedInstanceState.getInt("spinnerPos", 0));
            showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrRed));
        } else {
            spinnerColor.setPrompt("Choose sticker color");
        }
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGrayDark));
                } else if (position == 1) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGrayDark));
                } else if (position == 2) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrRed));
                } else if (position == 3) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrViolet));
                } else if (position == 4) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrLightCreen));
                } else if (position == 5) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGreen));
                } else if (position == 6) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrLightBlue));
                } else if (position == 7) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrBlue));
                } else if (position == 8) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrYellow));
                } else if (position == 9) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrOrange));
                } else if (position == 10) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrPink));
                } else if (position == 11) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrSand));
                } else if (position == 12) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrBrown));
                } else {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGrayDark));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorBlue));
            }
        });

        if(savedInstanceState != null){
            title_enter.setText(savedInstanceState.getString("title"));
            login_enter.setText(savedInstanceState.getString("login"));
            password_enter.setText(savedInstanceState.getString("pass"));
            link_enter.setText(savedInstanceState.getString("link"));
            comment_enter.setText(savedInstanceState.getString("comm"));
            date_enter.setText(savedInstanceState.getString("date"));
        }
        else {

            final Calendar cal = Calendar.getInstance();
            myYear = cal.get(Calendar.YEAR);
            myMonth = cal.get(Calendar.MONTH);
            myDay = cal.get(Calendar.DAY_OF_MONTH);

            String dayStr;
            String monthStr;

            if (myDay < 10) dayStr = "0" + myDay;
            else dayStr = String.valueOf(myDay);

            if (myMonth < 9) monthStr = "0" + (myMonth + 1);
            else monthStr = String.valueOf(myMonth + 1);

            prefs = new SharedPrefs(AddItem.this);
            int dateSwitchInd = prefs.loadInt(Constants.NEW_PREFERENCES_DATE_FORMAT);
            //Log.d(LOG_TAG, "check ind: " + dateSwitchInd);
            if(dateSwitchInd == 1){
                date_enter.setText(dayStr + "." + monthStr + "." + String.valueOf(myYear));
            }
            else if(dateSwitchInd == 2){
                date_enter.setText(monthStr + "/" + dayStr + "/" + String.valueOf(myYear));
            }
            else if(dateSwitchInd == 3){
                date_enter.setText(String.valueOf(myYear) + "-" + monthStr + "-" + dayStr);
            }
        }

        if (isFirstTime()) {
            Intent overflow = new Intent(AddItem.this, HelpOverflow.class);
            overflow.putExtra("fromActivity", 2);
            startActivity(overflow);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_PASS) {
            if (resultCode == RESULT_OK) {
                //Use Data to get string
                String string = data.getStringExtra("GENERATED_PASSWORD");
                password_enter.setText(string);
            }
        }
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanAddBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanAddBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    private void viewSetter(ActionBar ab){
        cSetter = new ColorSetter(AddItem.this);
        ab.setBackgroundDrawable(new ColorDrawable(cSetter.colorSetter()));
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    private String spinnerColor(){
        int color;
        String colorString;
        int spinnerPos = spinnerColor.getSelectedItemPosition();
        if (spinnerPos == 0) {
            color = getResources().getColor(R.color.colorSemiTrGrayDark);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 1) {
            color = getResources().getColor(R.color.colorSemiTrGrayDark);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 2) {
            color = getResources().getColor(R.color.colorSemiTrRed);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 3) {
            color = getResources().getColor(R.color.colorSemiTrViolet);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 4) {
            color = getResources().getColor(R.color.colorSemiTrLightCreen);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 5) {
            color = getResources().getColor(R.color.colorSemiTrGreen);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 6) {
            color = getResources().getColor(R.color.colorSemiTrLightBlue);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 7) {
            color = getResources().getColor(R.color.colorSemiTrBlue);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 8) {
            color = getResources().getColor(R.color.colorSemiTrYellow);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 9) {
            color = getResources().getColor(R.color.colorSemiTrOrange);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 10) {
            color = getResources().getColor(R.color.colorSemiTrPink);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 11) {
            color = getResources().getColor(R.color.colorSemiTrSand);
            colorString = Integer.toString(color);
        } else if (spinnerPos == 12) {
            color = getResources().getColor(R.color.colorSemiTrBrown);
            colorString = Integer.toString(color);
        } else {
            color = getResources().getColor(R.color.colorSemiTrGrayDark);
            colorString = Integer.toString(color);
        }
        return colorString;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                String title_str = title_enter.getText().toString().trim();
                String login_str = login_enter.getText().toString().trim();
                String pass_str = password_enter.getText().toString().trim();
                String link_str = link_enter.getText().toString().trim();
                String comm_str = comment_enter.getText().toString().trim();
                String date_str = date_enter.getText().toString().trim();
                String colorString = spinnerColor();
                boolean emptyCheck = emptyLogPassCheck();
                if (!emptyCheck) {
                    savePassword(title_str, login_str, pass_str, link_str, comm_str, date_str, colorString);
                    finish();
                }
                return true;
            case R.id.save_and_new:
                String mtitle_str = title_enter.getText().toString().trim();
                String mlogin_str = login_enter.getText().toString().trim();
                String mpass_str = password_enter.getText().toString().trim();
                String mlink_str = link_enter.getText().toString().trim();
                String mcomm_str = comment_enter.getText().toString().trim();
                if (mcomm_str.equals("")) {
                    mcomm_str = getString(R.string.comment_stock_string);
                }
                String mdate_str = date_enter.getText().toString().trim();
                boolean memptyCheck = emptyLogPassCheck();

                String colorStringI = spinnerColor();

                if (!memptyCheck) {
                    savePassword(mtitle_str, mlogin_str, mpass_str, mlink_str, mcomm_str, mdate_str,
                                colorStringI);
                    fieldClear();
                    String link_enter_str = link_enter.getText().toString();
                    if (link_enter_str.equals("")) {
                        link_enter.setText("http://www.");
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void savePassword(String title, String login, String password, String url, String comment,
                              String date, String color){
        Crypter crypter = new Crypter();
        String title_crypted = crypter.encrypt(title);
        String login_crypted = crypter.encrypt(login);
        String pass_crypted = crypter.encrypt(password);
        String link_crypted = crypter.encrypt(url);
        String comment_crypted = crypter.encrypt(comment);
        String date_crypted = crypter.encrypt(date);
        DataBase DB = new DataBase(AddItem.this);
        DB.open();
        String uuID = sHelpers.generateID();
        DB.insertPass(title_crypted, login_crypted, pass_crypted, link_crypted, comment_crypted, date_crypted, color, uuID);
        DB.close();
    }

    protected void onSaveInstanceState(Bundle saveInstance) {
        super.onSaveInstanceState(saveInstance);

        String title_str = title_enter.getText().toString();
        String login_str = login_enter.getText().toString();
        String pass_str = password_enter.getText().toString();
        String link_str = link_enter.getText().toString();
        String comm_str = comment_enter.getText().toString();
        String date_str = date_enter.getText().toString();
        saveInstance.putString("title", title_str);
        saveInstance.putString("login", login_str);
        saveInstance.putString("pass", pass_str);
        saveInstance.putString("link", link_str);
        saveInstance.putString("comm", comm_str);
        saveInstance.putString("date", date_str);
        int spinnerPos = spinnerColor.getSelectedItemPosition();
        saveInstance.putInt("spinnerPos", spinnerPos);
    }

    public void fieldClear(){
        comment_enter.setText("");
        link_enter.setText("http://www.");
        password_enter.setText("");
        login_enter.setText("");
        title_enter.setText("");
    }

    public boolean emptyLogPassCheck (){
        String title_str = title_enter.getText().toString().trim();
        String login_str = login_enter.getText().toString().trim();
        String pass_str = password_enter.getText().toString().trim();
        if (title_str.equals("") && login_str.equals("") && pass_str.equals("")){
            title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
            login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
            password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
            return true;
        }
        else if (title_str.equals("") && login_str.equals("")){
            title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
            login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
            return true;
        }
        else if (title_str.equals("") && pass_str.equals("")){
            title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
            password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
            return true;
        }
        else if (login_str.equals("") && pass_str.equals("")) {
            login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
            password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
            return true;
        }
        else if (title_str.equals("")){
            title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
            return true;
        }
        else if (login_str.equals("")){
            login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
            return true;
        }
        else if (pass_str.equals(""))
        {
            password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
            return true;
        }
        else {
            return false;
        }
    }

    protected Dialog dateDialog() {
        return new DatePickerDialog(this, myDateCallBack, myYear, myMonth, myDay);
    }

    DatePickerDialog.OnDateSetListener myDateCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;

            String dayStr;
            String monthStr;

            if (myDay < 10) dayStr = "0" + myDay;
            else dayStr = String.valueOf(myDay);

            if (myMonth < 9) monthStr = "0" + (myMonth + 1);
            else monthStr = String.valueOf(myMonth + 1);

            prefs = new SharedPrefs(AddItem.this);
            int dateSwitchInd = prefs.loadInt(Constants.NEW_PREFERENCES_DATE_FORMAT);
            //Log.d(LOG_TAG, "check ind: " + dateSwitchInd);
            if(dateSwitchInd == 1){
                date_enter.setText(dayStr + "." + monthStr + "." + String.valueOf(myYear));
            }
            else if(dateSwitchInd == 2){
                date_enter.setText(monthStr + "/" + dayStr + "/" + String.valueOf(myYear));
            }
            else if(dateSwitchInd == 3){
                date_enter.setText(String.valueOf(myYear) + "-" + monthStr + "-" + dayStr);
            }
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (!new ModuleManager().isPro()) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
