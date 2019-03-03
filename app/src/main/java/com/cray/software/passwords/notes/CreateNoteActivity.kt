package com.cray.software.passwords.notes

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.appbar.AppBarLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import com.bumptech.glide.Glide
import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.ActivityCreateNoteBinding
import com.cray.software.passwords.databinding.DialogColorPickerLayoutBinding
import com.cray.software.passwords.helpers.DataProvider
import com.cray.software.passwords.helpers.Permissions
import com.cray.software.passwords.helpers.TImeUtils
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.utils.BitmapUtil
import com.cray.software.passwords.utils.Dialogues
import com.cray.software.passwords.utils.SuperUtil
import com.cray.software.passwords.utils.ThemeUtil
import com.cray.software.passwords.views.ColorPickerView

import java.io.File
import java.io.FileNotFoundException
import java.util.Random

/**
 * Copyright 2016 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class CreateNoteActivity : AppCompatActivity() {

    private var mColor = 0
    private var mImageUri: Uri? = null

    private var binding: ActivityCreateNoteBinding? = null

    private var mItem: NoteItem? = null
    private var toolbar: AppBarLayout? = null
    private var taskField: EditText? = null

    private var themeUtil: ThemeUtil? = null

    private fun setText(text: String) {
        binding!!.taskMessage.setText(text)
        binding!!.taskMessage.setSelection(binding!!.taskMessage.text.length)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil = ThemeUtil.getInstance(this)
        if (themeUtil != null) setTheme(themeUtil!!.style)
        binding = DataBindingUtil.setContentView<T>(this, R.layout.activity_create_note)
        initActionBar()
        initMenu()
        loadNote()
        binding!!.imageContainer.visibility = View.GONE
        if (mItem != null) {
            mColor = mItem!!.color
            setText(SuperUtil.decrypt(mItem!!.summary))
            if (mItem!!.image != null) {
                binding!!.imageContainer.visibility = View.VISIBLE
                Glide.with(this).asBitmap().load(mItem!!.image).into(binding!!.noteImage)
            }
        } else {
            mColor = Random().nextInt(16)
        }
        updateBackground()

        binding!!.bottomBarView.setBackgroundColor(themeUtil!!.backgroundStyle)
        if (themeUtil!!.isDark) {
            binding!!.colorButton.setImageResource(R.drawable.ic_palette_white_24dp)
            binding!!.imageButton.setImageResource(R.drawable.ic_image_white_24dp)
            binding!!.deleteImage.setImageResource(R.drawable.ic_clear_white_24dp)
        } else {
            binding!!.colorButton.setImageResource(R.drawable.ic_palette_black_24dp)
            binding!!.imageButton.setImageResource(R.drawable.ic_image_black_24dp)
            binding!!.deleteImage.setImageResource(R.drawable.ic_clear_black_24dp)
        }

        binding!!.deleteImage.setOnClickListener { v -> clearImage() }
    }

    private fun clearImage() {
        if (mItem != null) {
            mItem!!.image = null
        }
        mImageUri = null
        binding!!.imageContainer.visibility = View.GONE
    }

    private fun initMenu() {
        binding!!.colorButton.setOnClickListener { view -> showColorDialog() }
        binding!!.imageButton.setOnClickListener { view -> selectImages() }
    }

    private fun selectImages() {
        if (Permissions.checkPermission(this, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL)) {
            getImage()
        } else {
            Permissions.requestPermission(this, REQUEST_SD_CARD, Permissions.READ_EXTERNAL, Permissions.WRITE_EXTERNAL)
        }
    }

    private fun loadNote() {
        val id = intent.getLongExtra(Constants.INTENT_ID, 0)
        if (id != 0L) {
            mItem = DataProvider.getNote(this, id)
        }
    }

    private fun initActionBar() {
        toolbar = binding!!.appBar
        setSupportActionBar(binding!!.toolbar)
        taskField = binding!!.taskMessage
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.elevation = 0f
        }
        toolbar!!.visibility = View.VISIBLE
    }

    private fun createObject(): Boolean {
        val note = taskField!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(note)) {
            taskField!!.error = getString(R.string.must_be_not_empty)
            return false
        }
        if (mItem == null) {
            mItem = NoteItem()
        }
        mItem!!.summary = SuperUtil.encrypt(note)
        mItem!!.date = SuperUtil.encrypt(TImeUtils.gmtStamp)
        if (mImageUri != null) {
            try {
                mItem!!.image = BitmapUtil.getCompressed(BitmapUtil.decodeUriToBitmap(this, mImageUri!!))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
        mItem!!.color = mColor
        return true
    }

    private fun saveNote() {
        if (!createObject()) {
            return
        }
        if (mItem != null) DataProvider.saveNote(this, mItem!!)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            MENU_ITEM_DELETE -> {
                deleteDialog()
                return true
            }
            R.id.action_add -> {
                saveNote()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showColorDialog() {
        val builder = Dialogues.getDialog(this)
        builder.setTitle(getString(R.string.choose_color))
        val binding = DialogColorPickerLayoutBinding.inflate(LayoutInflater.from(this))
        val view = binding.pickerView
        view.setSelectedColor(mColor)
        builder.setView(binding.getRoot())
        val dialog = builder.create()
        view.setListener { code ->
            mColor = code
            updateBackground()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteDialog() {
        val builder = Dialogues.getDialog(this)
        builder.setMessage(R.string.delete_this_note)
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            dialog.dismiss()
            deleteNote()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteNote() {
        if (mItem != null) {
            DataProvider.deleteNote(this, mItem!!)
            DeleteNoteFilesAsync(this).execute(mItem!!.key)
        }
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_create_note, menu)
        if (mItem != null) {
            menu.add(Menu.NONE, MENU_ITEM_DELETE, 100, getString(R.string.delete))
        }
        return true
    }

    private fun getImage() {
        val builder = Dialogues.getDialog(this)
        builder.setTitle(R.string.image)
        builder.setItems(arrayOf<CharSequence>(getString(R.string.from_gallery), getString(R.string.take_a_shot))
        ) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.image)), Constants.ACTION_REQUEST_GALLERY)
                }
                1 -> {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, "Picture")
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                    mImageUri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                    startActivityForResult(intent, Constants.ACTION_REQUEST_CAMERA)
                }
                else -> {
                }
            }
        }
        builder.show()
    }

    fun getRealPathFromURI(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.ACTION_REQUEST_GALLERY -> getImageFromGallery(data!!)
                Constants.ACTION_REQUEST_CAMERA -> getImageFromCamera()
            }
        }
    }

    private fun getImageFromGallery(data: Intent) {
        if (data.data != null) {
            addImageFromUri(data.data)
        }
    }

    private fun addImageFromUri(uri: Uri?) {
        if (uri == null) return
        mImageUri = uri
        var bitmapImage: Bitmap? = null
        try {
            bitmapImage = BitmapUtil.decodeUriToBitmap(this, uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        if (bitmapImage != null) {
            binding!!.imageContainer.visibility = View.VISIBLE
            binding!!.noteImage.setImageBitmap(bitmapImage)
        }
    }

    private fun getImageFromCamera() {
        addImageFromUri(mImageUri)
        val pathFromURI = getRealPathFromURI(mImageUri)
        val file = File(pathFromURI)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun updateBackground() {
        if (themeUtil != null) {
            binding!!.layoutContainer.setBackgroundColor(themeUtil!!.getColor(themeUtil!!.colorPrimary(mColor)))
            toolbar!!.setBackgroundColor(themeUtil!!.getColor(themeUtil!!.colorPrimary(mColor)))
            if (Module.isLollipop) {
                window.statusBarColor = themeUtil!!.getColor(themeUtil!!.colorPrimaryDark(mColor))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(taskField!!.windowToken, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_SD_CARD -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImage()
            }
        }
    }

    companion object {

        val MENU_ITEM_DELETE = 12
        private val REQUEST_SD_CARD = 1112
    }
}