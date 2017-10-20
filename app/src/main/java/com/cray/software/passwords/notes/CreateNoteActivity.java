package com.cray.software.passwords.notes;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.ActivityCreateNoteBinding;
import com.cray.software.passwords.databinding.DialogColorPickerLayoutBinding;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.DataProvider;
import com.cray.software.passwords.helpers.Permissions;
import com.cray.software.passwords.helpers.TImeUtils;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.BitmapUtil;
import com.cray.software.passwords.utils.SuperUtil;
import com.cray.software.passwords.views.ColorPickerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

/**
 * Copyright 2016 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class CreateNoteActivity extends AppCompatActivity {

    private static final String TAG = "CreateNoteActivity";
    public static final int MENU_ITEM_DELETE = 12;
    private static final int REQUEST_SD_CARD = 1112;

    private int mColor = 0;
    private Uri mImageUri;
    private ColorSetter cs = new ColorSetter(this);

    private RelativeLayout layoutContainer;

    private ActivityCreateNoteBinding binding;

    private NoteItem mItem;
    private AppBarLayout toolbar;
    private EditText taskField;

    private void setText(String text) {
        binding.taskMessage.setText(text);
        binding.taskMessage.setSelection(binding.taskMessage.getText().length());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);
        initActionBar();
        initMenu();
        initBgContainer();
        loadNote();
        if (mItem != null) {
            mColor = mItem.getColor();
            setText(SuperUtil.decrypt(mItem.getSummary()));
            Glide.with(this).asBitmap().load(mItem.getImage()).into(binding.noteImage);
        } else {
            mColor = new Random().nextInt(16);
        }
        updateBackground();
    }

    private void initBgContainer() {
        layoutContainer = binding.layoutContainer;
    }

    private void initMenu() {
        binding.colorButton.setOnClickListener(view -> showColorDialog());
        binding.imageButton.setOnClickListener(view -> selectImages());
    }

    private void selectImages() {
        if (Permissions.checkPermission(this, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL)) {
            getImage();
        } else {
            Permissions.requestPermission(this, REQUEST_SD_CARD, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL);
        }
    }

    private void loadNote() {
        long id = getIntent().getLongExtra(Constants.INTENT_ID, 0);
        if (id != 0) {
            mItem = DataProvider.getNote(this, id);
        }
    }

    private void initActionBar() {
        toolbar = binding.appBar;
        setSupportActionBar(binding.toolbar);
        taskField = binding.taskMessage;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0f);
        toolbar.setVisibility(View.VISIBLE);
    }

    private boolean createObject() {
        String note = taskField.getText().toString().trim();
        if (TextUtils.isEmpty(note)) {
            taskField.setError(getString(R.string.must_be_not_empty));
            return false;
        }
        if (mItem == null) {
            mItem = new NoteItem();
        }
        mItem.setSummary(SuperUtil.encrypt(note));
        mItem.setDate(TImeUtils.getGmtStamp());
        if (mImageUri != null) {
            try {
                mItem.setImage(BitmapUtil.getCompressed(BitmapUtil.decodeUriToBitmap(this, mImageUri)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        mItem.setColor(mColor);
        return true;
    }

    private void saveNote() {
        if (!createObject()) {
            return;
        }
        DataProvider.saveNote(this, mItem);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case MENU_ITEM_DELETE:
                deleteDialog();
                return true;
            case R.id.action_add:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showColorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_color));
        DialogColorPickerLayoutBinding binding = DialogColorPickerLayoutBinding.inflate(LayoutInflater.from(this));
        ColorPickerView view = binding.pickerView;
        view.setSelectedColor(mColor);
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        view.setListener(code -> {
            mColor = code;
            updateBackground();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_note);
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            deleteNote();
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteNote() {
        DataProvider.deleteNote(this, mItem);
        new DeleteNoteFilesAsync(this).execute(mItem.getKey());
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_note, menu);
        if (mItem != null) {
            menu.add(Menu.NONE, MENU_ITEM_DELETE, 100, getString(R.string.delete));
        }
        return true;
    }

    private void getImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.image);
        builder.setItems(new CharSequence[]{getString(R.string.from_gallery),
                        getString(R.string.take_a_shot)},
                (dialog, which) -> {
                    switch (which) {
                        case 0: {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.image)), Constants.ACTION_REQUEST_GALLERY);
                        }
                        break;
                        case 1: {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                            mImageUri = getContentResolver().insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                            startActivityForResult(intent, Constants.ACTION_REQUEST_CAMERA);
                        }
                        break;
                        default:
                            break;
                    }
                });
        builder.show();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.ACTION_REQUEST_GALLERY:
                    getImageFromGallery(data);
                    break;
                case Constants.ACTION_REQUEST_CAMERA:
                    getImageFromCamera();
                    break;
            }
        }
    }

    private void getImageFromGallery(Intent data) {
        if (data.getData() != null) {
            addImageFromUri(data.getData());
        }
    }

    private void addImageFromUri(Uri uri) {
        if (uri == null) return;
        mImageUri = uri;
        Bitmap bitmapImage = null;
        try {
            bitmapImage = BitmapUtil.decodeUriToBitmap(this, uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmapImage != null) {
            binding.noteImage.setImageBitmap(bitmapImage);
        }
    }

    private void getImageFromCamera() {
        addImageFromUri(mImageUri);
        String pathFromURI = getRealPathFromURI(mImageUri);
        File file = new File(pathFromURI);
        if (file.exists()) {
            file.delete();
        }
    }

    private void updateBackground() {
        layoutContainer.setBackgroundColor(cs.getColor(mColor));
        toolbar.setBackgroundColor(cs.getColor(cs.colorPrimary(mColor)));
        if (Module.isLollipop()) {
            getWindow().setStatusBarColor(cs.getColor(cs.colorPrimaryDark(mColor)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(taskField.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SD_CARD:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage();
                }
                break;
        }
    }
}