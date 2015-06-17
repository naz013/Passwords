package com.cray.software.passwords;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cray.software.passwords.dialogs.HelpOverflow;
import com.cray.software.passwords.dialogs.ProMarket;
import com.cray.software.passwords.dialogs.RateDialog;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.CustomCursorAdapter;
import com.cray.software.passwords.helpers.DataBase;
import com.cray.software.passwords.helpers.SecurePreferences;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.ModuleManager;
import com.cray.software.passwords.interfaces.SyncListener;
import com.cray.software.passwords.tasks.BackupTask;
import com.cray.software.passwords.tasks.DelayedTask;
import com.cray.software.passwords.tasks.DeleteTask;
import com.cray.software.passwords.tasks.SyncTask;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

public class MainScreen extends ActionBarActivity implements OnDismissCallback, SyncListener {

    ListView listViewMain;
    DataBase DB;

    private CustomCursorAdapter customAdapter;
    ColorSetter cSetter = new ColorSetter(MainScreen.this);
    ActionBar ab;

    private static final byte[] SALT = new byte[] {61,
            20,
            47,
            29,
            56,
            80,
            72,
            66,
            31,
            59,
            21,
            76,
            93,
            4,
            45,
            51,
            48,
            24,
            61,
            38};
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmk5AjvmxJxLZ/F7cEKwASj+EatbBozFHo3lfidSSx4GPtlpWLUZLAAAl/676VJDGPE9WvzHfvEUVUP8SAs70XulOZSzuNe/6T3ghWKS2pMI6PexTRTtGFsKrUlMX77AD+u16VRg3l5G8cabIG5o+/L17BWeNziiJ/ohB2phZB2Wa0J8JUkffH60l825enQWr9qPkvV9/3FF7hNu2B/Rbr1LU8nrbA8w0sAFGzqgamEn02QL0cWIu7bpuDrpqnMCEHzCWYt91SaZzNG+A5zGkHdLxiFDwWZliXSFIZ/WIJu+XYmfGKtun9JmzBE/Hwl4r3GlWHZ3PKT8wFjXuvY0SgQIDAQAB";

    private LicenseChecker mChecker;
    private LicenseCheckerCallback mLicenseCheckerCallback;
    boolean licensed;
    boolean checkingLicense;
    boolean didCheck;
    boolean isOpened;
    FloatingActionButton mFab;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        getIntent().setAction("JustActivity Created");

        cSetter = new ColorSetter(MainScreen.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowTitleEnabled(true);
            if (new ModuleManager().isPro()) ab.setTitle(getString(R.string.app_name));
            else ab.setTitle(getString(R.string.app_name_free));
            ab.setIcon(R.drawable.ic_key);
            viewSetter();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_layout);

        mFab = (FloatingActionButton) findViewById(R.id.button_floating_action);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_zoom_out);
                mFab.startAnimation(slide);
                mFab.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentMain = new Intent(MainScreen.this, AddItem.class);
                        startActivity(intentMain);
                    }
                }, 300);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_zoom);
                mFab.startAnimation(slide);
                mFab.setVisibility(View.VISIBLE);
            }
        }, 500);

        listViewMain = (ListView) findViewById(R.id.listViewMain);

        mFab.attachToListView(listViewMain);

        listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intentId = new Intent(MainScreen.this, ViewListItem.class);
                intentId.putExtra("itemId", id);
                startActivity(intentId);
            }
        });
    }

    private boolean isListFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanListBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanListBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    public static final int MENU_ITEM_PRO = 12;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        if (!new ModuleManager().isPro()) {
            menu.add(Menu.NONE, MENU_ITEM_PRO, 100, getString(R.string.buy_pro_title));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                new SyncTask(getSupportActionBar().getThemedContext(), MainScreen.this).execute();
                return true;
            case R.id.action_settings:
                Intent intentS = new Intent(MainScreen.this, SettingsActivity.class);
                startActivity(intentS);
                return true;
            case R.id.action_send:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"feedback.cray@gmail.com"});
                if (new ModuleManager().isPro()){
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords PRO");
                } else {
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords");
                }
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                return true;
            case MENU_ITEM_PRO:
                Intent market = new Intent(MainScreen.this, ProMarket.class);
                startActivity(market);
                return true;
            case R.id.action_order:
                showOrders();
                return true;
            case R.id.action_more:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://search?q=pub:Nazar Suhovich"));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Couldn't launch market", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showOrders() {
        final CharSequence[] items = {getString(R.string.sort_by_date_az),
                getString(R.string.sort_by_date_za),
                getString(R.string.sort_by_title_az),
                getString(R.string.sort_by_title_za)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.menu_sort_title));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                SharedPrefs prefs = new SharedPrefs(MainScreen.this);
                if (item == 0) {
                    prefs.savePrefs(Constants.NEW_PREFERENCES_ORDER_BY, Constants.ORDER_DATE_A_Z);
                } else if (item == 1) {
                    prefs.savePrefs(Constants.NEW_PREFERENCES_ORDER_BY, Constants.ORDER_DATE_Z_A);
                } else if (item == 2) {
                    prefs.savePrefs(Constants.NEW_PREFERENCES_ORDER_BY, Constants.ORDER_TITLE_A_Z);
                } else if (item == 3) {
                    prefs.savePrefs(Constants.NEW_PREFERENCES_ORDER_BY, Constants.ORDER_TITLE_Z_A);
                }
                dialog.dismiss();
                loaderAdapter();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void viewSetter(){
        cSetter = new ColorSetter(MainScreen.this);
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setBackgroundDrawable(new ColorDrawable(cSetter.colorSetter()));
            ab.setDisplayShowTitleEnabled(false);
            ab.setDisplayShowTitleEnabled(true);
        }
    }

    private void setSwipeDismissAdapter() {
        SwipeDismissAdapter adapter = new SwipeDismissAdapter(customAdapter, this);
        adapter.setAbsListView(listViewMain);
        listViewMain.setAdapter(adapter);
    }

    private void setBottomAdapter() {
        AnimationAdapter animAdapter = new SwingRightInAnimationAdapter(customAdapter);
        animAdapter.setAbsListView(listViewMain);
        listViewMain.setAdapter(animAdapter);
    }

    public void loaderAdapter(){
        DB = new DataBase(MainScreen.this);
        DB.open();
        customAdapter = new CustomCursorAdapter(MainScreen.this, DB.fetchAllPasswords());
        listViewMain.setAdapter(customAdapter);
        setSwipeDismissAdapter();
        setBottomAdapter();
    }

    @Override
    protected void onResume() {

        String action = getIntent().getAction();
        if(action == null || !action.equals("JustActivity Created")) {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
            finish();
        }
        else
            getIntent().setAction(null);


        viewSetter();
        mFab.setColorNormal(cSetter.colorSetter());
        mFab.setColorPressed(cSetter.colorChooser());

        loaderAdapter();

        isOpened = false;
        DB = new DataBase(MainScreen.this);
        DB.open();
        int count = DB.getCountPass();
        if (count > 0) {
            if (isListFirstTime()) {
                Intent overflow = new Intent(MainScreen.this, HelpOverflow.class);
                overflow.putExtra("fromActivity", 4);
                startActivity(overflow);
            }
        }
        DB.close();

        delayedThreads();

        if (new ModuleManager().isPro()) {
            if (!BuildConfig.DEBUG) {
                SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "cray_prefs", "LjubovM", true);
                boolean isLicensed = preferences.containsKey("isPasswordsProAppLicensed");
                if (isLicensed) {
                    String licStatus = preferences.getString("isPasswordsProAppLicensed");
                    if (!licStatus.matches("appIsLicensed")) {
                        preferences.put("check_license", "isTrue");
                        preferences.put("isPasswordsProAppLicensed", "appIsLicensed");
                        thanksDialog().show();
                    }
                } else {
                    preferences.put("check_license", "isTrue");
                    preferences.put("isPasswordsProAppLicensed", "appIsLicensed");
                    thanksDialog().show();
                }
            }
        }

        showRate();
        SharedPrefs prefs = new SharedPrefs(MainScreen.this);
        if (new ModuleManager().isPro()) {
            if (prefs.loadBoolean(Constants.NEW_PREFERENCES_AUTO_BACKUP))
                new BackupTask(MainScreen.this).execute();

            if (prefs.loadBoolean(Constants.NEW_PREFERENCES_AUTO_SYNC))
                new SyncTask(MainScreen.this, null).execute();
        }

        super.onResume();
    }
    private void showRate(){
        SharedPrefs sPrefs = new SharedPrefs(MainScreen.this);

        if (sPrefs.isString(Constants.NEW_PREFERENCES_RATE_SHOW)) {
            if (!sPrefs.loadBoolean(Constants.NEW_PREFERENCES_RATE_SHOW)) {
                int counts = sPrefs.loadInt(Constants.NEW_PREFERENCES_APP_RUNS_COUNT);
                if (counts < 10) {
                    sPrefs.saveInt(Constants.NEW_PREFERENCES_APP_RUNS_COUNT, counts + 1);
                } else {
                    sPrefs.saveInt(Constants.NEW_PREFERENCES_APP_RUNS_COUNT, 0);
                    startActivity(new Intent(MainScreen.this, RateDialog.class));
                }
            }
        } else {
            sPrefs.saveBoolean(Constants.NEW_PREFERENCES_RATE_SHOW, false);
            sPrefs.saveInt(Constants.NEW_PREFERENCES_APP_RUNS_COUNT, 0);
        }
    }

    private void delayedThreads(){
        new DelayedTask(MainScreen.this).execute();
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.back_button_toast), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void startCheckingLicence(){
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        mChecker = new LicenseChecker(getApplicationContext(), new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
        doCheck();
    }

    private void doCheck() {
        didCheck = false;
        checkingLicense = true;
        setSupportProgressBarIndeterminateVisibility(true);
        mChecker.checkAccess(mLicenseCheckerCallback);
    }

    @Override
    public void EndExecution(boolean result) {
        loaderAdapter();
    }

    @Override
    public void onDismiss(ViewGroup viewGroup, int[] ints) {
        for (int position : ints) {
            long del = customAdapter.getItemId(position);
            new DeleteTask(MainScreen.this, this).execute(del);
        }
    }


    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {

        @Override
        public void allow(int reason) {
            if (isFinishing()) {
                return;
            }

            SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "cray_prefs", "LjubovM", true);
            boolean licenseStatus = preferences.containsKey("check_license");
            if (!licenseStatus){
                preferences.put("check_license", "isTrue");
                preferences.put("isPasswordsProAppLicensed", "appIsLicensed");
                thanksDialog().show();
            } else {
                String dialog = preferences.getString("check_license");
                if (dialog.matches("isFalse")){
                    preferences.put("check_license", "isTrue");
                    preferences.put("isPasswordsProAppLicensed", "appIsLicensed");
                    thanksDialog().show();
                }
            }

            licensed = true;
            checkingLicense = false;
            didCheck = true;

        }

        @SuppressWarnings("deprecation")
        @Override
        public void dontAllow(int reason) {
            if (isFinishing()) {
                return;
            }

            SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "cray_prefs", "LjubovM", true);
            preferences.put("isPasswordsProAppLicensed", "appIsNotLicensed");
            preferences.put("check_license", "isFalse");
            licensed = false;
            checkingLicense = false;
            didCheck = true;

            if (!checkDialog().isShowing()) {
                checkDialog().show();
            }

        }

        @SuppressWarnings("deprecation")
        @Override
        public void applicationError(int reason) {
            if (isFinishing()) {
                return;
            }
            licensed = true;
            checkingLicense = false;
            didCheck = false;

            if (!checkDialog().isShowing()) {
                checkDialog().show();
            }
        }
    }

    protected Dialog checkDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_not_licensed_title))
                .setMessage(getString(R.string.dialog_not_licensed_message))
                .setPositiveButton(getString(R.string.dialog_buy_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://market.android.com/details?id=" + getPackageName()));
                        startActivity(marketIntent);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_exit_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton(getString(R.string.dialog_recheck_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doCheck();
                    }
                })
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener(){
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        finish();
                        return true;
                    }
                })
                .create();
    }

    protected Dialog thanksDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_licensed_title))
                .setMessage(getString(R.string.dialog_licensed_message))
                .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
    }
}
