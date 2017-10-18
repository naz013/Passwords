package com.cray.software.passwords;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cray.software.passwords.dialogs.ProMarket;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.DataProvider;
import com.cray.software.passwords.helpers.ListInterface;
import com.cray.software.passwords.helpers.Utils;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.interfaces.SimpleListener;
import com.cray.software.passwords.interfaces.SyncListener;
import com.cray.software.passwords.passwords.PasswordListInterface;
import com.cray.software.passwords.passwords.PasswordsRecyclerAdapter;
import com.cray.software.passwords.tasks.BackupTask;
import com.cray.software.passwords.tasks.DelayedTask;
import com.cray.software.passwords.tasks.DeleteNoteTask;
import com.cray.software.passwords.tasks.DeleteTask;
import com.cray.software.passwords.tasks.SyncTask;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.Prefs;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SyncListener, SimpleListener {

    private static final int MENU_ITEM_PRO = 12;

    private RecyclerView currentList;
    private LinearLayout emptyItem;

    private List<ListInterface> data;

    private FloatingActionButton mFab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Module.isPro()) toolbar.setTitle(getString(R.string.app_name));
        else toolbar.setTitle(getString(R.string.app_name_free));
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(view -> {
            Intent intentMain = new Intent(MainActivity.this, ManagePassword.class);
            startActivity(intentMain);
        });
        currentList = findViewById(R.id.currentList);
        currentList.setLayoutManager(new LinearLayoutManager(this));
        emptyItem = findViewById(R.id.emptyItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        if (!Module.isPro()) {
            menu.add(Menu.NONE, MENU_ITEM_PRO, 100, getString(R.string.buy_pro_title));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                new SyncTask(this, MainActivity.this).execute();
                return true;
            case R.id.action_settings:
                Intent intentS = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentS);
                return true;
            case R.id.action_send:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"feedback.cray@gmail.com"});
                if (Module.isPro()) {
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords PRO");
                } else {
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Passwords");
                }
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                return true;
            case MENU_ITEM_PRO:
                Intent market = new Intent(MainActivity.this, ProMarket.class);
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
        builder.setItems(items, (dialog, item) -> {
            Prefs prefs = Prefs.getInstance(this);
            if (item == 0) {
                prefs.setOrderBy(Constants.ORDER_DATE_A_Z);
            } else if (item == 1) {
                prefs.setOrderBy(Constants.ORDER_DATE_Z_A);
            } else if (item == 2) {
                prefs.setOrderBy(Constants.ORDER_TITLE_A_Z);
            } else if (item == 3) {
                prefs.setOrderBy(Constants.ORDER_TITLE_Z_A);
            }
            dialog.dismiss();
            loaderAdapter();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void viewSetter() {
        ColorSetter cSetter = new ColorSetter(MainActivity.this);
        int colorPrimary = cSetter.colorPrimary();
        int colorDark = cSetter.colorPrimaryDark();
        toolbar.setBackgroundColor(cSetter.getColor(colorPrimary));
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cSetter.getColor(colorDark));
        }
        if (colorPrimary != 0 && colorDark != 0) {
            mFab.setBackgroundTintList(Utils.getFabState(this, colorPrimary, colorDark));
        }
    }

    public void loaderAdapter() {
        data = DataProvider.getData(this);
        PasswordsRecyclerAdapter adapter = new PasswordsRecyclerAdapter(this, data);
        adapter.setEventListener(this);
        currentList.setAdapter(adapter);
        if (data.size() > 0) {
            currentList.setVisibility(View.VISIBLE);
            emptyItem.setVisibility(View.GONE);
        } else {
            currentList.setVisibility(View.GONE);
            emptyItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewSetter();
        loaderAdapter();
        delayedThreads();
        showRate();
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
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.back_button_toast), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public void endExecution(boolean result) {
        loaderAdapter();
    }

    @Override
    public void onItemClicked(int position, View view) {
        edit(position);
    }

    void edit(int position) {
        ListInterface item = data.get(position);
        long id = item.getId();
        if (item instanceof PasswordListInterface) {
            Intent intentId = new Intent(this, ManagePassword.class);
            intentId.putExtra(Constants.INTENT_ID, id);
            startActivity(intentId);
        } else {
            Intent intentId = new Intent(this, ManagePassword.class);
            intentId.putExtra(Constants.INTENT_ID, id);
            startActivity(intentId);
        }
    }

    private void delete(int position) {
        ListInterface item = data.get(position);
        long del = data.get(position).getId();
        if (item instanceof PasswordListInterface) {
            new DeleteTask(this, result -> Snackbar.make(mFab, R.string.removed, Snackbar.LENGTH_SHORT).show()).execute(del);
        } else {
            new DeleteNoteTask(this, result -> Snackbar.make(mFab, R.string.removed, Snackbar.LENGTH_SHORT).show()).execute(del);
        }
    }

    @Override
    public void onItemLongClicked(final int position, View view) {
        final String[] items = {getString(R.string.edit), getString(R.string.delete)};
        Utils.showLCAM(this, item -> {
            if (item == 0) {
                edit(position);
            }
            if (item == 1) {
                delete(position);
            }
        }, items);
    }
}
