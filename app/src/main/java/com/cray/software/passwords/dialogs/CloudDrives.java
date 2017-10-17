package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.cray.software.passwords.R;
import com.cray.software.passwords.cloud.DropboxLogin;
import com.cray.software.passwords.cloud.GoogleLogin;

public class CloudDrives extends Activity implements DropboxLogin.LoginCallback, GoogleLogin.LoginCallback {

    private GoogleLogin mGoogleLogin;
    private DropboxLogin mDropboxLogin;

    private Button linkDropbox, linkGDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clouds_dialog_layout);

        mDropboxLogin = new DropboxLogin(this, this);
        mGoogleLogin = new GoogleLogin(this, this);

        Button aboutClose = findViewById(R.id.aboutClose);
        aboutClose.setOnClickListener(v -> finish());

        linkDropbox = findViewById(R.id.linkDropbox);
        linkDropbox.setOnClickListener(view -> mDropboxLogin.login());

        linkGDrive = findViewById(R.id.linkGDrive);
        linkGDrive.setOnClickListener(v -> {
            if (mGoogleLogin.isLogged()) mGoogleLogin.logOut();
            else mGoogleLogin.login();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDropboxLogin.checkDropboxStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(boolean logged) {
        if (logged) {
            linkDropbox.setText(getString(R.string.logout_button));
        } else {
            linkDropbox.setText(getString(R.string.login_button));
        }
    }

    @Override
    public void onSuccess() {
        linkGDrive.setText(getString(R.string.logout_button));
    }

    @Override
    public void onFail() {
        linkGDrive.setText(getString(R.string.login_button));
    }
}
