package com.cray.software.passwords;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cray.software.passwords.databinding.ActivityMainBinding;
import com.cray.software.passwords.fragments.BaseFragment;
import com.cray.software.passwords.fragments.FragmentInterface;
import com.cray.software.passwords.fragments.NestedFragment;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.notes.NotesFragment;
import com.cray.software.passwords.passwords.PasswordsFragment;
import com.cray.software.passwords.settings.SettingsFragment;
import com.cray.software.passwords.tasks.BackupTask;
import com.cray.software.passwords.tasks.DelayedTask;
import com.cray.software.passwords.tasks.SyncTask;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.ThemedActivity;

public class MainActivity extends ThemedActivity implements FragmentInterface, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private BaseFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        replaceFragment(HomeFragment.newInstance(), HomeFragment.TAG);

        initBottomBar();
    }

    private void initBottomBar() {
        for (int i = 0; i < binding.bottomBar.getTabCount(); i++) {
            binding.bottomBar.getTabAtPosition(i).setActiveColor(getThemeUtil().getColor(getThemeUtil().colorAccent()));
        }
        binding.bottomBar.setDefaultTabPosition(0);
        binding.bottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.tab_home:
                    replaceFragment(HomeFragment.newInstance(), HomeFragment.TAG);
                    break;
                case R.id.tab_passwords:
                    replaceFragment(PasswordsFragment.newInstance(), PasswordsFragment.TAG);
                    break;
                case R.id.tab_notes:
                    replaceFragment(NotesFragment.newInstance(), NotesFragment.TAG);
                    break;
                case R.id.tab_settings:
                    replaceFragment(SettingsFragment.newInstance(), SettingsFragment.TAG);
                    break;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        delayedThreads();
        showRate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoBackup();
    }

    private void autoBackup() {
        if (Module.isPro()) {
            if (Prefs.getInstance(this).isAutoBackupEnabled()) {
                new BackupTask(MainActivity.this).execute();
            }
            if (Prefs.getInstance(this).isAutoSyncEnabled()) {
                new SyncTask(MainActivity.this, null).execute();
            }
        }
    }

    private void showRate() {
        Prefs prefs = Prefs.getInstance(this);
        if (!prefs.isRateShowed()) {
            int counts = prefs.getRunsCount();
            if (counts < 10) {
                prefs.setRunsCount(counts + 1);
            } else {
                prefs.setRunsCount(0);
                Dialogues.showRateDialog(this);
            }
        }
    }

    private void delayedThreads() {
        new DelayedTask(this).execute();
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (!mFragment.canGoBack()) return;
        if (mFragment instanceof NestedFragment) {
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_button_toast), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    private void addFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        clearBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    @Override
    public void setCurrent(BaseFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void setTitle(@NonNull String title) {
        binding.toolbar.setTitle(title);
    }

    @Override
    public void setClick(@Nullable View.OnClickListener clickListener) {
        if (clickListener != null) {
            binding.fab.setOnClickListener(clickListener);
            binding.fab.setVisibility(View.VISIBLE);
        } else {
            binding.fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void moveBack() {
        onBackPressed();
    }

    @Override
    public void openScreen(BaseFragment fragment, String tag) {
        addFragment(fragment, tag);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager() == null) return;
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        Log.d(TAG, "onBackStackChanged: current: " + fragment);
        if (fragment != null && fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onFragmentResume();
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count >= 2) {
            Fragment f = getSupportFragmentManager().getFragments().get(count - 2);
            if (f != null && f instanceof BaseFragment) {
                ((BaseFragment) f).onFragmentPause();
            }
        }
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFragment != null) mFragment.onActivityResult(requestCode, resultCode, data);
    }
}
