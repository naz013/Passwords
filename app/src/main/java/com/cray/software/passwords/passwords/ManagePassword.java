package com.cray.software.passwords.passwords;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.ActivityManagePasswordBinding;
import com.cray.software.passwords.databinding.DialogColorPickerLayoutBinding;
import com.cray.software.passwords.dialogs.GeneratePassword;
import com.cray.software.passwords.helpers.DataProvider;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.helpers.TImeUtils;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.SuperUtil;
import com.cray.software.passwords.utils.ThemedActivity;
import com.cray.software.passwords.views.ColorPickerView;

import java.util.Random;

public class ManagePassword extends ThemedActivity {

    private static final int MENU_ITEM_DELETE = 12;

    private ActivityManagePasswordBinding binding;
    private Password mPassword;
    private int mColor;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_password);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        id = getIntent().getLongExtra(Constants.INTENT_ID, 0);

        binding.loginEnter.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        binding.commentEnter.setOnKeyListener((v, keyCode, event) -> event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
        binding.generateDialog.setOnClickListener(v ->
                startActivityForResult(new Intent(ManagePassword.this, GeneratePassword.class),
                        Constants.REQUEST_CODE_PASS));
        binding.showPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.passwordEnter.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                binding.passwordEnter.setInputType(129);
            }
        });
        binding.showPass.setChecked(true);

        if (savedInstanceState != null) {
            binding.titleEnter.setText(savedInstanceState.getString("title"));
            binding.loginEnter.setText(savedInstanceState.getString("login"));
            binding.passwordEnter.setText(savedInstanceState.getString("pass"));
            binding.linkEnter.setText(savedInstanceState.getString("link"));
            binding.commentEnter.setText(savedInstanceState.getString("comm"));
            mColor = savedInstanceState.getInt("color");
        }
        mColor = new Random().nextInt(16) + 1;
        showPassword();
        updateBackground();

        if (getThemeUtil() != null && getThemeUtil().isDark()) {
            binding.generateDialog.setImageResource(R.drawable.ic_vpn_key_white_24dp);
        } else {
            binding.generateDialog.setImageResource(R.drawable.ic_vpn_key_black_24dp);
        }
    }

    private void showPassword() {
        mPassword = DataProvider.getPassword(this, id);
        if (mPassword != null) {
            binding.titleEnter.setText(SuperUtil.decrypt(mPassword.getTitle()));
            binding.passwordEnter.setText(SuperUtil.decrypt(mPassword.getPassword()));
            binding.loginEnter.setText(SuperUtil.decrypt(mPassword.getLogin()));
            binding.commentEnter.setText(SuperUtil.decrypt(mPassword.getComment()));
            binding.linkEnter.setText(SuperUtil.decrypt(mPassword.getUrl()));
            mColor = mPassword.getColor();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_PASS) {
            if (resultCode == RESULT_OK) {
                String string = data.getStringExtra("GENERATED_PASSWORD");
                binding.passwordEnter.setText(string);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        if (id != 0) {
            menu.add(Menu.NONE, MENU_ITEM_DELETE, 100, getString(R.string.delete));
        }
        return true;
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = Dialogues.getDialog(this);
        builder.setMessage(R.string.delete_this_password);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            DataProvider.deletePassword(ManagePassword.this, mPassword);
            finish();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
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
            case R.id.changeColor:
                showColorDialog();
                return true;
            case R.id.save_and_new:
                if (save()) {
                    Snackbar.make(binding.windowBackground, R.string.saved, Snackbar.LENGTH_SHORT).show();
                    fieldClear();
                }
                return true;
            case MENU_ITEM_DELETE:
                deleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean save() {
        String title = binding.titleEnter.getText().toString().trim();
        String login = binding.loginEnter.getText().toString().trim();
        String password = binding.passwordEnter.getText().toString().trim();
        String url = binding.linkEnter.getText().toString().trim();
        String comment = binding.commentEnter.getText().toString().trim();
        if (checkEmpty()) return false;
        String date = TImeUtils.getGmtStamp();
        title = SuperUtil.encrypt(title);
        login = SuperUtil.encrypt(login);
        password = SuperUtil.encrypt(password);
        url = SuperUtil.encrypt(url);
        comment = SuperUtil.encrypt(comment);
        date = SuperUtil.encrypt(date);
        if (mPassword == null) {
            mPassword = new Password(title, date, login, comment, url, 0, mColor, password, SyncHelper.generateID());
        } else {
            mPassword.setTitle(title);
            mPassword.setColor(mColor);
            mPassword.setComment(comment);
            mPassword.setDate(date);
            mPassword.setLogin(login);
            mPassword.setUrl(url);
            mPassword.setPassword(password);
        }
        DataProvider.savePassword(this, mPassword);
        return true;
    }

    protected void onSaveInstanceState(Bundle saveInstance) {
        saveInstance.putString("title", binding.titleEnter.getText().toString().trim());
        saveInstance.putString("login", binding.loginEnter.getText().toString().trim());
        saveInstance.putString("pass", binding.passwordEnter.getText().toString().trim());
        saveInstance.putString("link", binding.linkEnter.getText().toString().trim());
        saveInstance.putString("comm", binding.commentEnter.getText().toString().trim());
        saveInstance.putInt("color", mColor);
        super.onSaveInstanceState(saveInstance);
    }

    public void fieldClear() {
        binding.commentEnter.setText("");
        String link_enter_str = binding.linkEnter.getText().toString();
        if (link_enter_str.equals("")) {
            binding.linkEnter.setText("https://www.");
        }
        binding.passwordEnter.setText("");
        binding.loginEnter.setText("");
        binding.titleEnter.setText("");
    }

    public boolean checkEmpty() {
        if (TextUtils.isEmpty(binding.titleEnter.getText().toString().trim())) {
            Snackbar.make(binding.windowBackground, R.string.edit_title_is_empty, Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(binding.loginEnter.getText().toString().trim())) {
            Snackbar.make(binding.windowBackground, R.string.edit_login_is_empty, Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(binding.passwordEnter.getText().toString().trim())) {
            Snackbar.make(binding.windowBackground, R.string.edit_pass_is_empty, Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void updateBackground() {
        if (getThemeUtil() != null) {
            binding.appBar.setBackgroundColor(getThemeUtil().getColor(getThemeUtil().colorPrimary(mColor)));
            if (Module.isLollipop()) {
                getWindow().setStatusBarColor(getThemeUtil().getColor(getThemeUtil().colorPrimaryDark(mColor)));
            }
        }
    }

    private void showColorDialog() {
        AlertDialog.Builder builder = Dialogues.getDialog(this);
        builder.setTitle(getString(R.string.choose_color));
        DialogColorPickerLayoutBinding binding = DialogColorPickerLayoutBinding.inflate(LayoutInflater.from(this));
        ColorPickerView view = binding.pickerView;
        view.setSelectedColor(mColor);
        builder.setView(binding.getRoot());
        final AlertDialog dialog = builder.create();
        view.setListener(code -> {
            mColor = code;
            updateBackground();
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
