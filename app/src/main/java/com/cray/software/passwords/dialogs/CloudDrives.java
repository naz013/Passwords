package com.cray.software.passwords.dialogs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.cloud.DropboxHelper;
import com.cray.software.passwords.cloud.GDriveHelper;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;

public class CloudDrives extends Activity {

    Button aboutClose;
    DropboxHelper dbx = new DropboxHelper(CloudDrives.this);
    GDriveHelper gdx = new GDriveHelper(CloudDrives.this);
    SharedPrefs prefs = new SharedPrefs(CloudDrives.this);

    Button linkDropbox, linkGDrive;
    TextView gDriveTitle, dropboxTitle;
    private static final int REQUEST_AUTHORIZATION = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 2;

    String MARKET_APP_JUSTREMINDER = "com.cray.software.passwords";
    String MARKET_APP_JUSTREMINDER_PRO = "com.cray.software.passwordspro";

    String accountName;
    private Context ctx = this;
    private Activity a = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clouds_dialog_layout);

        dbx.startSession();

        aboutClose = (Button) findViewById(R.id.aboutClose);
        aboutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linkDropbox = (Button) findViewById(R.id.linkDropbox);
        linkDropbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isIn;
                if (new Module().isPro()) isIn = isAppInstalled(MARKET_APP_JUSTREMINDER);
                else isIn = isAppInstalled(MARKET_APP_JUSTREMINDER_PRO);
                if (isIn){
                    checkDialog().show();
                } else {
                if (dbx.isLinked()){
                    if (dbx.unlink()){
                        linkDropbox.setText(getString(R.string.login_button));
                    }
                } else {
                    dbx.startLink();
                }
            }
            }
        });

        linkGDrive = (Button) findViewById(R.id.linkGDrive);
        linkGDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gdx.isLinked()){
                    gdx.unlink();
                    setUpButton();
                } else {
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
                    startActivityForResult(intent, REQUEST_AUTHORIZATION);
                }
            }
        });

        dropboxTitle = (TextView) findViewById(R.id.dropboxTitle);
        gDriveTitle = (TextView) findViewById(R.id.gDriveTitle);
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    protected Dialog checkDialog() {
        return new AlertDialog.Builder(this)
                .setMessage(getString(R.string.other_version_message))
                .setPositiveButton(getString(R.string.open_app_dialog_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i;
                        PackageManager manager = getPackageManager();
                        if (new Module().isPro()) i = manager.getLaunchIntentForPackage(MARKET_APP_JUSTREMINDER);
                        else i = manager.getLaunchIntentForPackage(MARKET_APP_JUSTREMINDER_PRO);
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(i);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        if (new Module().isPro())
                            intent.setData(Uri.parse("package:" + MARKET_APP_JUSTREMINDER));
                        else intent.setData(Uri.parse("package:" + MARKET_APP_JUSTREMINDER_PRO));
                        startActivity(intent);
                    }
                })
                .setNeutralButton(getString(R.string.button_close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
    }

    private void setUpButton(){
        if (gdx.isLinked()){
            linkGDrive.setText(getString(R.string.logout_button));
        } else {
            linkGDrive.setText(getString(R.string.login_button));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!dbx.isLinked()) {
            if (dbx.checkLink()) {
                linkDropbox.setText(getString(R.string.logout_button));
                linkDropbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dbx.unlink()){
                            linkDropbox.setText(getString(R.string.login_button));
                        }
                    }
                });
            }
        } else {
            linkDropbox.setText(getString(R.string.logout_button));
        }

        setUpButton();
    }

    void getAndUseAuthTokenInAsyncTask(Account account) {
        AsyncTask<Account, String, String> task = new AsyncTask<Account, String, String>() {
            ProgressDialog progressDlg;
            AsyncTask<Account, String, String> me = this;

            @Override
            protected void onPreExecute() {
                progressDlg = new ProgressDialog(CloudDrives.this, ProgressDialog.STYLE_SPINNER);
                progressDlg.setMax(100);
                progressDlg.setMessage(getString(R.string.verify_message));
                progressDlg.setCancelable(false);
                progressDlg.setIndeterminate(false);
                progressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface d) {
                        progressDlg.dismiss();
                        me.cancel(true);
                    }
                });
                progressDlg.show();
            }

            @Override
            protected String doInBackground(Account... params) {
                return getAccessToken(params[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    // Wait for the extra intent
                } else {
                    accountName = s;
                }
                progressDlg.dismiss();
            }
        };
        task.execute(account);
    }

    private String getAccessToken(Account account) {
        try {
            return GoogleAuthUtil.getToken(ctx, account.name, "oauth2:" + DriveScopes.DRIVE);  // IMPORTANT: DriveScopes must be changed depending on what level of access you want
        } catch (UserRecoverableAuthException e) {
            // Start the Approval Screen intent, if not run from an Activity, add the Intent.FLAG_ACTIVITY_NEW_TASK flag.
            a.startActivityForResult(e.getIntent(), REQUEST_ACCOUNT_PICKER);
            e.printStackTrace();
            return null;
        } catch (GoogleAuthException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            GoogleAccountManager gam = new GoogleAccountManager(this);
            getAndUseAuthTokenInAsyncTask(gam.getAccountByName(accountName));
            prefs.saveSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER, accountName);
            Log.d("SiteTrack", "CHOOSE_ACCOUNT");
        } else if (requestCode == REQUEST_ACCOUNT_PICKER && resultCode == RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            prefs.saveSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER, accountName);
            Log.d("SiteTrack", "REQUEST_TOKEN");
        }
    }
}
