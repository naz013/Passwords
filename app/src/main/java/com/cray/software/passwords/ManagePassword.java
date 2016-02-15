package com.cray.software.passwords;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.cray.software.passwords.dialogs.GeneratePassword;
import com.cray.software.passwords.dialogs.HelpOverflow;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.DataBase;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.helpers.Utils;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;

import java.util.Calendar;

public class ManagePassword extends AppCompatActivity {

    EditText title_enter, login_enter, password_enter, link_enter, comment_enter, date_enter;
    CheckBox showPass;
    Spinner spinnerColor;
    ImageButton generateDialog;
    RelativeLayout showColorRelLay;
    Toolbar toolbar;

    ColorSetter cSetter;
    SyncHelper sHelpers = new SyncHelper(ManagePassword.this);
    SharedPrefs prefs;

    int myYear = 0;
    int myMonth = 0;
    int myDay = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_password);
        cSetter = new ColorSetter(ManagePassword.this);
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.activity_title);

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

        generateDialog = (ImageButton) findViewById(R.id.generateDialog);
        generateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ManagePassword.this, GeneratePassword.class),
                        Constants.REQUEST_CODE_PASS);
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
        String[] items = new String[]{getString(R.string.choose_color), getString(R.string.red), getString(R.string.purple),
                getString(R.string.green), getString(R.string.green_light),
                getString(R.string.blue), getString(R.string.blue_light),
                getString(R.string.yellow), getString(R.string.orange),
                getString(R.string.cyan), getString(R.string.pink),
                getString(R.string.teal), getString(R.string.amber),
                getString(R.string.dark_purple), getString(R.string.dark_orange),
                getString(R.string.lime), getString(R.string.indigo)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        spinnerColor.setAdapter(adapter);
        if (savedInstanceState != null) {
            spinnerColor.setSelection(savedInstanceState.getInt("spinnerPos", 0));
            showColorRelLay.setBackgroundColor(cSetter.getPasswordColor(0));
        } else spinnerColor.setSelection(0);
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) return;
                showColorRelLay.setBackgroundColor(cSetter.getPasswordColor(position - 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                showColorRelLay.setBackgroundColor(Utils.getColor(ManagePassword.this, R.color.colorBlue));
            }
        });

        if(savedInstanceState != null){
            title_enter.setText(savedInstanceState.getString("title"));
            login_enter.setText(savedInstanceState.getString("login"));
            password_enter.setText(savedInstanceState.getString("pass"));
            link_enter.setText(savedInstanceState.getString("link"));
            comment_enter.setText(savedInstanceState.getString("comm"));
            date_enter.setText(savedInstanceState.getString("date"));
        } else {
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

            prefs = new SharedPrefs(ManagePassword.this);
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
            Intent overflow = new Intent(ManagePassword.this, HelpOverflow.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ColorSetter cSetter = new ColorSetter(ManagePassword.this);
        int colorPrimary = cSetter.colorSetter();
        int colorDark = cSetter.colorStatus();
        toolbar.setBackgroundColor(colorPrimary);
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(colorDark);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                save();
                finish();
                return true;
            case R.id.save_and_new:
                save();

                fieldClear();
                String link_enter_str = link_enter.getText().toString();
                if (link_enter_str.equals("")) {
                    link_enter.setText("http://www.");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        String title = title_enter.getText().toString().trim();
        String login = login_enter.getText().toString().trim();
        String password = password_enter.getText().toString().trim();
        String url = link_enter.getText().toString().trim();
        String comment = comment_enter.getText().toString().trim();
        String date = date_enter.getText().toString().trim();
        int color = spinnerColor.getSelectedItemPosition() - 1;
        if (!emptyLogPassCheck()) return;
        title = Crypter.encrypt(title);
        login = Crypter.encrypt(login);
        password = Crypter.encrypt(password);
        url = Crypter.encrypt(url);
        comment = Crypter.encrypt(comment);
        date = Crypter.encrypt(date);
        DataBase DB = new DataBase(ManagePassword.this);
        DB.open();
        String uuID = sHelpers.generateID();
        DB.insertPass(title, login, password, url, comment, date, color, uuID);
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

            prefs = new SharedPrefs(ManagePassword.this);
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
}
