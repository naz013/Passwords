package com.cray.software.passwords;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.cray.software.passwords.dialogs.GeneratePassword;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.DataBase;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.helpers.Utils;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;

public class ManagePassword extends AppCompatActivity {

    private EditText title_enter;
    private EditText login_enter;
    private EditText password_enter;
    private EditText link_enter;
    private EditText comment_enter;
    private Spinner spinnerColor;
    private RelativeLayout showColorRelLay;
    private Toolbar toolbar;

    private ColorSetter cSetter;
    private SyncHelper sHelpers = new SyncHelper(ManagePassword.this);
    private SharedPrefs prefs;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_password);
        cSetter = new ColorSetter(ManagePassword.this);
        prefs = new SharedPrefs(ManagePassword.this);
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cSetter.getColor(cSetter.colorPrimaryDark()));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.activity_title);

        id = getIntent().getLongExtra("itemId", 0);

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

        ImageButton generateDialog = (ImageButton) findViewById(R.id.generateDialog);
        generateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ManagePassword.this, GeneratePassword.class),
                        Constants.REQUEST_CODE_PASS);
            }
        });

        CheckBox showPass = (CheckBox) findViewById(R.id.showPass);
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
                getString(R.string.lime), getString(R.string.indigo), getString(R.string.white)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        spinnerColor.setAdapter(adapter);
        if (savedInstanceState != null) {
            spinnerColor.setSelection(savedInstanceState.getInt("spinnerPos", 0));
            showColorRelLay.setBackgroundColor(cSetter.getPasswordColor(0));
        } else {
            spinnerColor.setSelection(0);
        }
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

        if (savedInstanceState != null) {
            title_enter.setText(savedInstanceState.getString("title"));
            login_enter.setText(savedInstanceState.getString("login"));
            password_enter.setText(savedInstanceState.getString("pass"));
            link_enter.setText(savedInstanceState.getString("link"));
            comment_enter.setText(savedInstanceState.getString("comm"));
        }

        if (id != 0) {
            DataBase db = new DataBase(this);
            db.open();
            Cursor c = db.fetchPass(id);
            if (c != null && c.moveToFirst()) {
                String title = c.getString(c.getColumnIndex(Constants.COLUMN_TITLE));
                String login = c.getString(c.getColumnIndex(Constants.COLUMN_LOGIN));
                String password = c.getString(c.getColumnIndex(Constants.COLUMN_PASSWORD));
                String url = c.getString(c.getColumnIndex(Constants.COLUMN_URL));
                String comment = c.getString(c.getColumnIndex(Constants.COLUMN_COMMENT));
                int color = c.getInt(c.getColumnIndex(Constants.COLUMN_TECHNICAL));
                title_enter.setText(Crypter.decrypt(title));
                password_enter.setText(Crypter.decrypt(password));
                login_enter.setText(Crypter.decrypt(login));
                comment_enter.setText(Crypter.decrypt(comment));
                link_enter.setText(Crypter.decrypt(url));
                spinnerColor.setSelection(color + 1);
            }
            db.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_PASS) {
            if (resultCode == RESULT_OK) {
                String string = data.getStringExtra("GENERATED_PASSWORD");
                password_enter.setText(string);
            }
        }
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
        int colorPrimary = cSetter.colorPrimary();
        int colorDark = cSetter.colorPrimaryDark();
        toolbar.setBackgroundColor(cSetter.getColor(colorPrimary));
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cSetter.getColor(colorDark));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                if (save()) finish();
                return true;
            case R.id.save_and_new:
                if (save()) {
                    Snackbar.make(toolbar, R.string.saved, Snackbar.LENGTH_SHORT).show();
                    fieldClear();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean save() {
        String title = title_enter.getText().toString().trim();
        String login = login_enter.getText().toString().trim();
        String password = password_enter.getText().toString().trim();
        String url = link_enter.getText().toString().trim();
        String comment = comment_enter.getText().toString().trim();
        String date = date_enter.getText().toString().trim();
        int color = spinnerColor.getSelectedItemPosition() - 1;
        if (color < 0) {
            Snackbar.make(toolbar, R.string.you_dont_select_a_color, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (checkEmpty()) return false;
        title = Crypter.encrypt(title);
        login = Crypter.encrypt(login);
        password = Crypter.encrypt(password);
        url = Crypter.encrypt(url);
        comment = Crypter.encrypt(comment);
        date = Crypter.encrypt(date);
        DataBase DB = new DataBase(ManagePassword.this);
        DB.open();
        if (id != 0) {
            DB.updatePass(id, title, login, password, url, comment, date, color);
        } else {
            String uuID = sHelpers.generateID();
            DB.insertPass(title, login, password, url, comment, date, color, uuID);
        }
        DB.close();
        return true;
    }

    protected void onSaveInstanceState(Bundle saveInstance) {
        super.onSaveInstanceState(saveInstance);
        String title_str = title_enter.getText().toString();
        String login_str = login_enter.getText().toString();
        String pass_str = password_enter.getText().toString();
        String link_str = link_enter.getText().toString();
        String comm_str = comment_enter.getText().toString();
        saveInstance.putString("title", title_str);
        saveInstance.putString("login", login_str);
        saveInstance.putString("pass", pass_str);
        saveInstance.putString("link", link_str);
        saveInstance.putString("comm", comm_str);
        int spinnerPos = spinnerColor.getSelectedItemPosition();
        saveInstance.putInt("spinnerPos", spinnerPos);
    }

    public void fieldClear() {
        comment_enter.setText("");
        String link_enter_str = link_enter.getText().toString();
        if (link_enter_str.equals("")) {
            link_enter.setText("http://www.");
        }
        password_enter.setText("");
        login_enter.setText("");
        title_enter.setText("");
    }

    public boolean checkEmpty() {
        String title_str = title_enter.getText().toString().trim();
        String login_str = login_enter.getText().toString().trim();
        String pass_str = password_enter.getText().toString().trim();
        if (title_str.equals("")) {
            Snackbar.make(toolbar, R.string.edit_title_is_empty, Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (login_str.equals("")) {
            Snackbar.make(toolbar, R.string.edit_login_is_empty, Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (pass_str.equals("")) {
            Snackbar.make(toolbar, R.string.edit_pass_is_empty, Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
