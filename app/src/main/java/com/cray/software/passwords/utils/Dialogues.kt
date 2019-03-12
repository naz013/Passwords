package com.cray.software.passwords.utils

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

/**
 * Copyright 2017 Nazar Suhovich
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

object Dialogues {

    fun getDialog(context: Context): AlertDialog.Builder {
//        if (Prefs.getInstance(context).appTheme == THEME_AMOLED) {
//            val themeUtil = ThemeUtil.getInstance(context)
//            return if (themeUtil != null) {
//                AlertDialog.Builder(context, themeUtil.dialogStyle)
//            } else {
//                AlertDialog.Builder(context)
//            }
//        } else {
            return AlertDialog.Builder(context)
//        }
    }

    fun showTextDialog(activity: AppCompatActivity, click: DialogOkClick, title: String, value: String) {
//        val builder = getDialog(activity)
//        val binding = DialogTextViewBinding.inflate(LayoutInflater.from(activity))
//        builder.setView(binding.getRoot())
//        val dialog = builder.create()
//        binding.dialogTitle.text = title
//        binding.valueView.text = value
//        binding.buttonOk.setOnClickListener { view -> click.onClick(dialog) }
//        dialog.show()
    }

    fun showSeekDialog(activity: AppCompatActivity, click: DialogSeekOkClick, title: String, current: Int) {
        val builder = getDialog(activity)
//        val binding = DialogSeekBarBinding.inflate(LayoutInflater.from(activity))
//        builder.setView(binding.getRoot())
//        val dialog = builder.create()
//        binding.dialogTitle.text = title
//        binding.seekView.progress = current - 1
//        binding.valueView.text = current.toString()
//        binding.seekView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
//                binding.valueView.text = (i + 1).toString()
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) {
//
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//
//            }
//        })
//        binding.buttonOk.setOnClickListener { view -> click.onClick(dialog, binding.seekView.progress + 1) }
//        dialog.show()
    }

    fun showSimpleDialog(activity: AppCompatActivity, click: DialogOneOkClick, title: String, hint: String) {
//        val builder = getDialog(activity)
//        val binding = DialogSingleFieldBinding.inflate(LayoutInflater.from(activity))
//        builder.setView(binding.getRoot())
//        val dialog = builder.create()
//        binding.dialogTitle.text = title
//        binding.inputField.hint = hint
//        binding.inputField.addTextChangedListener(object : TextWatcher {
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                binding.inputLayout.setError("")
//                binding.inputLayout.setErrorEnabled(false)
//            }
//
//            override fun afterTextChanged(editable: Editable) {
//
//            }
//
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//
//            }
//        })
//        binding.buttonOk.setOnClickListener { view -> click.onClick(dialog, binding.inputField, binding.inputLayout) }
//        dialog.show()
    }

    fun showPasswordChangeDialog(activity: AppCompatActivity, click: DialogTwoOkClick, title: String, hint1: String, hint2: String) {
//        val builder = getDialog(activity)
//        val binding = DialogTwoFieldsBinding.inflate(LayoutInflater.from(activity))
//        builder.setView(binding.getRoot())
//        val dialog = builder.create()
//        binding.dialogTitle.text = title
//
//        binding.inputField.hint = hint1
//        binding.input2Field.hint = hint2
//
//        binding.inputField.addTextChangedListener(object : TextWatcher {
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                binding.inputLayout.setError("")
//                binding.inputLayout.setErrorEnabled(false)
//            }
//
//            override fun afterTextChanged(editable: Editable) {
//
//            }
//
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//
//            }
//        })
//        binding.input2Field.addTextChangedListener(object : TextWatcher {
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                binding.input2Layout.setError("")
//                binding.input2Layout.setErrorEnabled(false)
//            }
//
//            override fun afterTextChanged(editable: Editable) {
//
//            }
//
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//
//            }
//        })
//
//        val passLengthInt = Prefs.getInstance(activity).passwordLength
//        val passOldLengthInt = Prefs.getInstance(activity).oldPasswordLength
//
//        val passDecrypted = WeakReference(SuperUtil.decrypt(Prefs.getInstance(activity).loadPassPrefs()))
//        val loadPassLength = passDecrypted.get().length
//
//        if (passOldLengthInt == loadPassLength) {
//            val FilterOldArray = arrayOfNulls<InputFilter>(1)
//            FilterOldArray[0] = InputFilter.LengthFilter(passOldLengthInt)
//            binding.inputField.filters = FilterOldArray
//        } else {
//            val FilterOldArray = arrayOfNulls<InputFilter>(1)
//            FilterOldArray[0] = InputFilter.LengthFilter(loadPassLength)
//            binding.inputField.filters = FilterOldArray
//        }
//
//        val FilterArray = arrayOfNulls<InputFilter>(1)
//        FilterArray[0] = InputFilter.LengthFilter(passLengthInt)
//        binding.input2Field.filters = FilterArray
//
//        binding.buttonOk.setOnClickListener { view ->
//            click.onClick(dialog, binding.inputField,
//                    binding.inputLayout, binding.input2Field, binding.input2Layout)
//        }
//        dialog.show()
    }

    fun showRateDialog(activity: AppCompatActivity) {
//        val builder = getDialog(activity)
//        builder.setMessage(R.string.rate_question)
//        builder.setPositiveButton(R.string.button_rate) { dialogInterface, i ->
//            dialogInterface.dismiss()
//            Prefs.getInstance(activity).isRateShowed = true
//            SuperUtil.launchMarket(activity)
//        }
//        builder.setNegativeButton(R.string.button_never) { dialogInterface, i ->
//            dialogInterface.dismiss()
//            Prefs.getInstance(activity).isRateShowed = true
//        }
//        builder.setNeutralButton(R.string.button_later) { dialogInterface, i ->
//            dialogInterface.dismiss()
//            Prefs.getInstance(activity).isRateShowed = false
//            Prefs.getInstance(activity).runsCount = 0
//        }
//        builder.setCancelable(false)
//        builder.create().show()
    }

    interface DialogOneOkClick {
        fun onClick(dialog: AlertDialog, field: EditText, layout: TextInputLayout)
    }

    interface DialogTwoOkClick {
        fun onClick(dialog: AlertDialog, field: EditText, layout: TextInputLayout, field2: EditText, layout2: TextInputLayout)
    }

    interface DialogSeekOkClick {
        fun onClick(dialog: AlertDialog, value: Int)
    }

    interface DialogOkClick {
        fun onClick(dialog: AlertDialog)
    }
}
