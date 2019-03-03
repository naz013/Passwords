package com.cray.software.passwords.dialogs

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.DialogGeneratePasswordBinding
import com.cray.software.passwords.utils.ThemeUtil

import java.util.HashMap
import java.util.Random

class GeneratePassword : AppCompatActivity(), View.OnClickListener {

    private var binding: DialogGeneratePasswordBinding? = null
    private val rnd = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeUtil = ThemeUtil.getInstance(this)
        if (themeUtil != null) setTheme(themeUtil.dialogStyle)
        binding = DataBindingUtil.setContentView<T>(this, R.layout.dialog_generate_password)
        if (themeUtil != null) binding!!.bgView.setBackgroundColor(themeUtil.backgroundStyle)

        binding!!.passwordLength.max = 32
        binding!!.passwordLength.progress = 16
        binding!!.passLength.text = binding!!.passwordLength.progress.toString()
        binding!!.passwordLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding!!.passLength.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        val genButton = findViewById<Button>(R.id.genButton)
        genButton.setOnClickListener(this)
        val genCancel = findViewById<Button>(R.id.genCancel)
        genCancel.setOnClickListener(this)
        val genInsert = findViewById<Button>(R.id.genInsert)
        genInsert.setOnClickListener(this)
    }

    private fun randomString(len: Int, upper: Int, lower: Int, num: Int, sym: Int): String {
        var up: String? = null
        if (upper != 0) {
            up = AZ
        }
        var low: String? = null
        if (lower != 0) {
            low = az
        }
        var numerical: String? = null
        if (num != 0) {
            numerical = numeric
        }
        var symbolical: String? = null
        if (sym != 0) {
            symbolical = symbols
        }
        val mapLength = upper + lower + num + sym
        val map = HashMap<Int, String>()

        for (i in 0 until mapLength) {
            if (up != null && !map.containsValue(up)) {
                map[i] = up
            }
            if (low != null && !map.containsValue(low)) {
                map[i] = low
            }
            if (numerical != null && !map.containsValue(numerical)) {
                map[i] = numerical
            }
            if (symbolical != null && !map.containsValue(symbolical)) {
                map[i] = symbolical
            }
        }
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            val type = Random()
            val next = type.nextInt(mapLength)
            val rand = map[next]
            sb.append(rand!![rnd.nextInt(rand.length)])
        }
        return sb.toString()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.genCancel -> finish()
            R.id.genButton -> if (preGenerateCheck()) {
                startGenerating()
            }
            R.id.genInsert -> {
                val intent = Intent()
                val genPass = binding!!.passwordShow.text!!.toString().trim { it <= ' ' }
                intent.putExtra("GENERATED_PASSWORD", genPass)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun preGenerateCheck(): Boolean {
        val status: Boolean
        if (binding!!.aZCheck.isChecked || binding!!.azCheck.isChecked || binding!!.numericCheck.isChecked || binding!!.symbolCheck.isChecked) {
            if (binding!!.passwordLength.progress > 0) {
                status = true
            } else {
                status = false
                Toast.makeText(this@GeneratePassword, getString(R.string.pass_length), Toast.LENGTH_SHORT).show()
            }
        } else {
            status = false
            binding!!.aZCheck.isChecked = true
            Toast.makeText(this@GeneratePassword, getString(R.string.pass_checks), Toast.LENGTH_SHORT).show()
        }
        return status
    }

    private fun startGenerating() {
        var aZCh = 0
        if (binding!!.aZCheck.isChecked) {
            aZCh = 1
        }
        var azCh = 0
        if (binding!!.azCheck.isChecked) {
            azCh = 1
        }
        var numCh = 0
        if (binding!!.numericCheck.isChecked) {
            numCh = 1
        }
        var symCh = 0
        if (binding!!.symbolCheck.isChecked) {
            symCh = 1
        }
        val password = randomString(binding!!.passwordLength.progress, aZCh, azCh, numCh, symCh)
        binding!!.passwordShow.setText(password)
    }

    companion object {

        private val AZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val az = "abcdefghijklmnopqrstuvwxyz"
        private val numeric = "0123456789"
        private val symbols = "!@#$%()?^*"
    }
}
