package com.cray.software.passwords;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cray.software.passwords.cloud.DropboxHelper;
import com.cray.software.passwords.dialogs.GeneratePassword;
import com.cray.software.passwords.dialogs.HelpOverflow;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.DataBase;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.tasks.DeleteTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewListItem extends ActionBarActivity implements View.OnClickListener {
    EditText comment_enter, link_enter, password_enter, login_enter, title_enter, date_enter;
    TextView viewLogin, viewPassword, viewLink, viewComment, viewDate;
    LinearLayout showLayout, editLayout;
    CheckBox editCheck, showPass;
    Spinner spinnerColor;
    DataBase DB;
    ImageButton generateDialog;
    String DIR_SD = "backup";
    String login_decrypted, pass_decrypted, title_decrypted, link_decrypted, comment_decrypted, date_decrypted,
            title_str, login_str, pass_str, link_str, comment_str, date_str, title_crypted, login_crypted, pass_crypted,
            link_crypted, comment_crypted, date_crypted, colorDB, newColor, uuID;
    int actHeight, receivedColor;
    long itemSelected;

    RelativeLayout showColorRelLay, viewActLayout;

    ColorSetter cSetter;
    SharedPrefs sPrefs = new SharedPrefs(ViewListItem.this);
    ActionBar ab;
    Crypter crypter = new Crypter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);

        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(false);
            ab.setDisplayShowTitleEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayUseLogoEnabled(false);
        }
        cSetter = new ColorSetter(ViewListItem.this);

        viewActLayout = (RelativeLayout) findViewById(R.id.viewActLayout);
        viewActLayout.setVisibility(View.GONE);

        editLayout = (LinearLayout) findViewById(R.id.editLayout);
        editLayout.setVisibility(View.GONE);
        showLayout = (LinearLayout) findViewById(R.id.showLayout);
        showLayout.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                viewActLayout.startAnimation(slide);
                viewActLayout.setVisibility(View.VISIBLE);
            }
        }, 500);

        actHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        Intent intentId = getIntent();
        itemSelected = intentId.getLongExtra("itemId", 1);
        DB = new DataBase(this);
        DB.open();
        Cursor c = DB.fetchPass(itemSelected);
        title_str = c.getString(c.getColumnIndex(Constants.COLUMN_TITLE));
        login_str = c.getString(c.getColumnIndex(Constants.COLUMN_LOGIN));
        pass_str = c.getString(c.getColumnIndex(Constants.COLUMN_PASSWORD));
        link_str = c.getString(c.getColumnIndex(Constants.COLUMN_URL));
        comment_str = c.getString(c.getColumnIndex(Constants.COLUMN_COMMENT));
        date_str = c.getString(c.getColumnIndex(Constants.COLUMN_DATE));
        colorDB = c.getString(c.getColumnIndex(Constants.COLUMN_TECHNICAL));
        uuID = c.getString(c.getColumnIndex(Constants.COLUMN_PIC_SEL));
        c.close();
        DB.close();

        final int colorToCompare = Integer.parseInt(colorDB);
        cSetter = new ColorSetter(ViewListItem.this);
        receivedColor = cSetter.colorCompareChooser(colorToCompare);
        viewSetter(ab, viewActLayout, colorToCompare);

        title_enter = (EditText) findViewById(R.id.title_enter);
        login_enter = (EditText) findViewById(R.id.login_enter);
        viewLogin = (TextView) findViewById(R.id.viewLogin);
        viewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String text = login_decrypted;
                if (text.length() > 0) {
                    android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied text", text);
                    clipboardMgr.setPrimaryClip(clip);
                }
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
        password_enter = (EditText) findViewById(R.id.password_enter);
        password_enter.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        viewPassword = (TextView) findViewById(R.id.viewPassword);
        viewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String text = pass_decrypted;
                if (text.length() > 0) {
                    android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied text", text);
                    clipboardMgr.setPrimaryClip(clip);
                }
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
        showPass = (CheckBox) findViewById(R.id.showPass);
        showPass.setChecked(true);
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
        generateDialog = (ImageButton) findViewById(R.id.generateDialog);
        generateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ViewListItem.this, GeneratePassword.class), Constants.REQUEST_CODE_PASS);
            }
        });
        link_enter = (EditText) findViewById(R.id.link_enter);
        viewLink = (TextView) findViewById(R.id.viewLink);
        viewLink.setOnClickListener(this);
        comment_enter = (EditText) findViewById(R.id.comment_enter);
        comment_enter.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });
        viewComment = (TextView) findViewById(R.id.viewComment);
        date_enter = (EditText) findViewById(R.id.date_enter);
        viewDate = (TextView) findViewById(R.id.viewDate);

        title_decrypted = crypter.decrypt(title_str);
        login_decrypted = crypter.decrypt(login_str);
        pass_decrypted = crypter.decrypt(pass_str);
        link_decrypted = crypter.decrypt(link_str);
        comment_decrypted = crypter.decrypt(comment_str);
        date_decrypted = crypter.decrypt(date_str);
        SpannableString content = new SpannableString(link_decrypted);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        ab.setTitle(title_decrypted);

        setEditText(title_decrypted, login_decrypted, pass_decrypted, link_decrypted, comment_decrypted, date_decrypted);
        setText(login_decrypted, pass_decrypted, content, comment_decrypted, date_decrypted);
        setFocus(false);

        showColorRelLay = (RelativeLayout) findViewById(R.id.showColorRelLay);
        spinnerColor = (Spinner) findViewById(R.id.spinnerColor);
        loadSpinner(colorDB);

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGrayDark));
                } else if (position == 1) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrRed));
                } else if (position == 2) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrViolet));
                } else if (position == 3) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrLightCreen));
                } else if (position == 4) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGreen));
                } else if (position == 5) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrLightBlue));
                } else if (position == 6) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrBlue));
                } else if (position == 7) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrYellow));
                } else if (position == 8) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrOrange));
                } else if (position == 9) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrPink));
                } else if (position == 10) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrSand));
                } else if (position == 11) {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrBrown));
                } else {
                    showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGrayDark));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                showColorRelLay.setBackgroundColor(getResources().getColor(R.color.colorSemiTrGrayDark));
            }
        });

        editCheck = (CheckBox) findViewById(R.id.editCheck);
        editCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    invalidateOptionsMenu();
                    setFocus(true);
                    Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    showLayout.startAnimation(slide);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showLayout.setVisibility(View.GONE);
                        }
                    }, 500);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            editLayout.startAnimation(slideUp);
                            editLayout.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                    String link_enter_str = link_enter.getText().toString();
                    if (link_enter_str.equals("")) {
                        link_enter.setText("http://www.");
                    }
                } else {
                    setFocus(false);
                    invalidateOptionsMenu();
                    DB.open();
                    Cursor c = DB.fetchPass(itemSelected);
                    title_str = c.getString(c.getColumnIndex(Constants.COLUMN_TITLE));
                    login_str = c.getString(c.getColumnIndex(Constants.COLUMN_LOGIN));
                    pass_str = c.getString(c.getColumnIndex(Constants.COLUMN_PASSWORD));
                    link_str = c.getString(c.getColumnIndex(Constants.COLUMN_URL));
                    comment_str = c.getString(c.getColumnIndex(Constants.COLUMN_COMMENT));
                    date_str = c.getString(c.getColumnIndex(Constants.COLUMN_DATE));
                    String colorDB = c.getString(c.getColumnIndex(Constants.COLUMN_TECHNICAL));
                    uuID = c.getString(c.getColumnIndex(Constants.COLUMN_PIC_SEL));
                    c.close();
                    DB.close();

                    title_decrypted = crypter.decrypt(title_str);
                    login_decrypted = crypter.decrypt(login_str);
                    pass_decrypted = crypter.decrypt(pass_str);
                    link_decrypted = crypter.decrypt(link_str);
                    comment_decrypted = crypter.decrypt(comment_str);
                    date_decrypted = crypter.decrypt(date_str);
                    SpannableString content = new SpannableString(link_decrypted);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

                    ab.setTitle(title_decrypted);

                    setText(login_decrypted, pass_decrypted, content, comment_decrypted, date_decrypted);

                    int colorToCompare = Integer.parseInt(colorDB);
                    cSetter = new ColorSetter(ViewListItem.this);
                    receivedColor = cSetter.colorCompareChooser(colorToCompare);
                    viewSetter(ab, viewActLayout, colorToCompare);

                    loadSpinner(colorDB);

                    Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    editLayout.startAnimation(slide);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editLayout.setVisibility(View.GONE);
                        }
                    }, 500);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            showLayout.startAnimation(slideUp);
                            showLayout.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }
            }
        });
        if (isFirstTime()) {
            Intent overflow = new Intent(ViewListItem.this, HelpOverflow.class);
            overflow.putExtra("fromActivity", 3);
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

    private void loadSpinner(String colorDB){
        int dbLoadColor;
        if (colorDB == null){
            dbLoadColor = getResources().getColor(R.color.colorLightCreen);
            showColorRelLay.setBackgroundColor(dbLoadColor);
            spinnerColor.setSelection(0);
        } else {
            dbLoadColor = Integer.parseInt(colorDB);
            showColorRelLay.setBackgroundColor(dbLoadColor);
            if(dbLoadColor == getResources().getColor(R.color.colorSemiTrGrayDark)){
                spinnerColor.setSelection(0);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrRed)){
                spinnerColor.setSelection(1);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrViolet)){
                spinnerColor.setSelection(2);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrLightCreen)){
                spinnerColor.setSelection(3);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrGreen)){
                spinnerColor.setSelection(4);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrLightBlue)){
                spinnerColor.setSelection(5);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrBlue)){
                spinnerColor.setSelection(6);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrYellow)){
                spinnerColor.setSelection(7);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrOrange)){
                spinnerColor.setSelection(8);
            } else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrPink)){
                spinnerColor.setSelection(9);
            }else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrSand)){
                spinnerColor.setSelection(10);
            }else if (dbLoadColor == getResources().getColor(R.color.colorSemiTrBrown)){
                spinnerColor.setSelection(11);
            }else {
                spinnerColor.setSelection(0);
            }
        }
    }

    private void setText(String login, String password, SpannableString link, String comment, String date){
        viewLogin.setText(login);
        viewPassword.setText(password);
        viewLink.setText(link);
        viewComment.setText(comment);
        viewDate.setText(date);
    }

    private void setEditText(String title, String login, String password, String link, String comment, String date){
        title_enter.setText(title);
        login_enter.setText(login);
        password_enter.setText(password);
        link_enter.setText(link);
        comment_enter.setText(comment);
        date_enter.setText(date);
    }

    private void setFocus(boolean focus){
        title_enter.setFocusableInTouchMode(focus);
        title_enter.setFocusable(focus);
        login_enter.setFocusableInTouchMode(focus);
        login_enter.setFocusable(focus);
        password_enter.setFocusableInTouchMode(focus);
        password_enter.setFocusable(focus);
        link_enter.setFocusableInTouchMode(focus);
        link_enter.setFocusable(focus);
        comment_enter.setFocusableInTouchMode(focus);
        comment_enter.setFocusable(focus);
        date_enter.setFocusableInTouchMode(focus);
        date_enter.setFocusable(focus);
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanViewBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanViewBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    private void viewSetter(ActionBar ab, View v, int colorToCompare){
        cSetter = new ColorSetter(ViewListItem.this);
        receivedColor = cSetter.colorCompareChooser(colorToCompare);
        v.setBackgroundColor(colorToCompare);
        ab.setBackgroundDrawable(new ColorDrawable(receivedColor));
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorDarkChooser(colorToCompare));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_save);
        //menu.clear();
        if (editCheck.isChecked()){
            item.setEnabled(true);
        } else {
            item.setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private int spinnerColor(){
        int color;
        int spinnerPos = spinnerColor.getSelectedItemPosition();
        if (spinnerPos == 0) {
            color = getResources().getColor(R.color.colorSemiTrGrayDark);
        } else if (spinnerPos == 1) {
            color = getResources().getColor(R.color.colorSemiTrRed);
        } else if (spinnerPos == 2) {
            color = getResources().getColor(R.color.colorSemiTrViolet);
        } else if (spinnerPos == 3) {
            color = getResources().getColor(R.color.colorSemiTrLightCreen);
        } else if (spinnerPos == 4) {
            color = getResources().getColor(R.color.colorSemiTrGreen);
        } else if (spinnerPos == 5) {
            color = getResources().getColor(R.color.colorSemiTrLightBlue);
        } else if (spinnerPos == 6) {
            color = getResources().getColor(R.color.colorSemiTrBlue);
        } else if (spinnerPos == 7) {
            color = getResources().getColor(R.color.colorSemiTrYellow);
        } else if (spinnerPos == 8) {
            color = getResources().getColor(R.color.colorSemiTrOrange);
        } else if (spinnerPos == 9) {
            color = getResources().getColor(R.color.colorSemiTrPink);
        } else if (spinnerPos == 10) {
            color = getResources().getColor(R.color.colorSemiTrSand);
        } else if (spinnerPos == 11) {
            color = getResources().getColor(R.color.colorSemiTrBrown);
        } else {
            color = getResources().getColor(R.color.colorSemiTrGrayDark);
        }
        return color;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                final long delId = itemSelected;
                showDialog(delId, title_decrypted);
                return true;
            case R.id.action_save:
                String title_new_str = title_enter.getText().toString().trim();
                String login_str = login_enter.getText().toString().trim();
                String pass_str = password_enter.getText().toString().trim();
                String link_str = link_enter.getText().toString().trim();
                String comm_str = comment_enter.getText().toString().trim();
                sPrefs = new SharedPrefs(ViewListItem.this);
                int dateSwitchInd = sPrefs.loadInt(Constants.NEW_PREFERENCES_DATE_FORMAT);
                if(dateSwitchInd == Constants.DATE_EU){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    String date = sdf.format(new Date(System.currentTimeMillis()));
                    date_enter.setText(date);
                } else if(dateSwitchInd == Constants.DATE_US){
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    String date = sdf.format(new Date(System.currentTimeMillis()));
                    date_enter.setText(date);
                } else if(dateSwitchInd == Constants.DATE_CN){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String date = sdf.format(new Date(System.currentTimeMillis()));
                    date_enter.setText(date);
                }
                String date_str = date_enter.getText().toString().trim();

                int newColoring = spinnerColor();
                newColor = Integer.toString(newColoring);

                boolean emptyCheck = emptyLogPassCheck ();
                if (!emptyCheck) {
                    saveChanges(title_new_str, login_str, pass_str, link_str, comm_str, date_str, newColoring);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewLink:
                String text = link_decrypted;
                if (!text.matches("http://www.")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
                    startActivity(browserIntent);
                }
                break;
        }
    }

    private void saveChanges(String title, String login, String password, String url,
                             String comment, String date, int newColoring){
        title_crypted = crypter.encrypt(title);
        login_crypted = crypter.encrypt(login);
        pass_crypted = crypter.encrypt(password);
        link_crypted = crypter.encrypt(url);
        comment_crypted = crypter.encrypt(comment);
        date_crypted = crypter.encrypt(date);
        if (changeCheck()) {
            DB.open();
            DB.updatePass(itemSelected, title_crypted, login_crypted, pass_crypted, link_crypted, comment_crypted, date_crypted, newColor);
            DB.close();
            ab.setTitle(title);
            viewSetter(ab, viewActLayout, newColoring);
            editCheck.setChecked(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File sdPath = Environment.getExternalStorageDirectory();
                    File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + DIR_SD + "/" + uuID + Constants.FILE_EXTENSION);
                    sdPathDr.delete();
                    File sdPathTmp = new File(sdPath.toString() + "/Pass_backup/tmp/" + uuID + Constants.FILE_EXTENSION);
                    if (sdPathTmp.exists()) {
                        sdPathTmp.delete();
                    }
                    DropboxHelper dbx = new DropboxHelper(ViewListItem.this);
                    if (dbx.isLinked()) {
                        if (SyncHelper.isConnected(ViewListItem.this)) {
                            String newFile = ("/" + Constants.DIR_DBX + uuID + Constants.FILE_EXTENSION);
                            dbx.deleteFile(newFile);
                        }
                    }
                }
            }).start();
        } else {
            editCheck.setChecked(false);
        }
    }

    public void showDialog(final long delId, final String selected){
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewListItem.this);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.alert_dialog_pos_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            new DeleteTask(ViewListItem.this, null).execute(delId);
            finish();
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_neg_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setTitle(getString(R.string.alert_title) + " " + selected);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean changeCheck(){
        boolean isChanged;
        if (!title_str.matches(title_crypted)){
            isChanged = true;
        } else if (!login_str.matches(login_crypted)){
            isChanged = true;
        } else if (!pass_str.matches(pass_crypted)){
            isChanged = true;
        } else if (!link_str.matches(link_crypted)){
            isChanged = true;
        } else if (!comment_str.matches(comment_crypted)){
            isChanged = true;
        } else if (!colorDB.matches(newColor)){
            isChanged = true;
        } else {
            isChanged = false;
        }
        return isChanged;
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
    }

    public boolean emptyLogPassCheck (){
        String title_str = title_enter.getText().toString().trim();
        String login_str = login_enter.getText().toString().trim();
        String pass_str = password_enter.getText().toString().trim();
        if (title_str.equals("") && login_str.equals("") && pass_str.equals("")){
            title_enter.setHint("");
            title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
            login_enter.setHint("");
            login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
            password_enter.setHint("");
            password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
            return true;
        } else {
            if (title_str.equals("") && login_str.equals("")){
                title_enter.setHint("");
                title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
                login_enter.setHint("");
                login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
                return true;
            } else{
                if (title_str.equals("") && pass_str.equals("")){
                    title_enter.setHint("");
                    title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
                    password_enter.setHint("");
                    password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
                    return true;
                } else {
                    if (login_str.equals("") && pass_str.equals("")) {
                        login_enter.setHint("");
                        login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
                        password_enter.setHint("");
                        password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
                        return true;
                    } else {
                        if (title_str.equals("")){
                            title_enter.setHint("");
                            title_enter.setError(getResources().getString(R.string.edit_title_is_empty));
                            return true;
                        } else {
                            if (login_str.equals("")){
                                login_enter.setHint("");
                                login_enter.setError(getResources().getString(R.string.edit_login_is_empty));
                                return true;
                            } else {
                                if (pass_str.equals("")) {
                                    password_enter.setError(getResources().getString(R.string.edit_pass_is_empty));
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void onStart() {
        super.onStart();
    }
}
