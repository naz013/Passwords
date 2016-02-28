package com.cray.software.passwords;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cray.software.passwords.fragments.ExportSettingsFragment;
import com.cray.software.passwords.fragments.GeneralSettingsFragment;
import com.cray.software.passwords.fragments.OtherSettingsFragment;
import com.cray.software.passwords.fragments.SecuritySettingsFragment;
import com.cray.software.passwords.fragments.SettingsFragment;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;

import java.io.File;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnHeadlineSelectedListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            SettingsFragment firstFragment = new SettingsFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onArticleSelected(int position) {
        if (position == 0){
            GeneralSettingsFragment newFragment = new GeneralSettingsFragment();
            Bundle args = new Bundle();
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (position == 1){
            SecuritySettingsFragment newFragment = new SecuritySettingsFragment();
            Bundle args = new Bundle();
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (position == 2){
            ExportSettingsFragment newFragment = new ExportSettingsFragment();
            Bundle args = new Bundle();
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (position == 3){
            OtherSettingsFragment newFragment = new OtherSettingsFragment();
            Bundle args = new Bundle();
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void syncPrefs(){
        SharedPrefs sPrefs = new SharedPrefs(SettingsActivity.this);
        boolean isSD = SyncHelper.isSdPresent();
        if (isSD){
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.PREFS);
            if (!sdPathDr.exists()){
                sdPathDr.mkdirs();
            }
            File prefs = new File(sdPathDr + "/prefs.xml");
            if (prefs.exists()){
                prefs.delete();
            }
            sPrefs.saveSharedPreferencesToFile(prefs);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ColorSetter cSetter = new ColorSetter(SettingsActivity.this);
        int colorPrimary = cSetter.colorPrimary();
        int colorDark = cSetter.colorPrimaryDark();
        toolbar.setBackgroundColor(cSetter.getColor(colorPrimary));
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cSetter.getColor(colorDark));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        syncPrefs();
    }
}
