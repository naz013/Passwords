package com.cray.software.passwords.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.FragmentGeneralSettingsBinding
import com.cray.software.passwords.dialogs.ThemeActivity
import com.cray.software.passwords.modern_ui.NestedFragment
import com.cray.software.passwords.utils.Dialogues
import com.cray.software.passwords.utils.Prefs
import com.cray.software.passwords.utils.ThemeUtil

class GeneralSettingsFragment : NestedFragment() {

    private var binding: FragmentGeneralSettingsBinding? = null
    private var mItemSelect: Int = 0

    private val currentTheme: String
        get() {
            val theme = Prefs.getInstance(context).appTheme
            return if (theme == ThemeUtil.THEME_AUTO)
                getString(R.string.auto)
            else if (theme == ThemeUtil.THEME_WHITE)
                getString(R.string.light)
            else if (theme == ThemeUtil.THEME_AMOLED)
                getString(R.string.amoled)
            else
                getString(R.string.dark)
        }

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGeneralSettingsBinding.inflate(inflater, container, false)

        binding!!.colorPrefs.setOnClickListener { view -> startActivityForResult(Intent(activity, ThemeActivity::class.java), REQ_THEME) }
        initAppTheme()

        return binding!!.getRoot()
    }

    private fun initAppTheme() {
        binding!!.themePrefs.setDetailText(currentTheme)
        binding!!.themePrefs.setOnClickListener { view -> showThemeDialog() }
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        initThemeColor()
        if (anInterface != null) {
            anInterface!!.setClick(null)
            anInterface!!.setTitle(getString(R.string.interface_block))
        }
    }

    private fun showThemeDialog() {
        if (context == null) return
        val builder = Dialogues.getDialog(context!!)
        builder.setCancelable(true)
        builder.setTitle(getString(R.string.theme))
        val colors = arrayOf(getString(R.string.auto), getString(R.string.light), getString(R.string.dark), getString(R.string.amoled))
        val adapter = ArrayAdapter(context!!,
                android.R.layout.simple_list_item_single_choice, colors)
        val initTheme = Prefs.getInstance(context).appTheme
        mItemSelect = initTheme
        builder.setSingleChoiceItems(adapter, mItemSelect) { dialog, which -> mItemSelect = which }
        builder.setPositiveButton(getString(R.string.button_ok)) { dialog, which ->
            Prefs.getInstance(context).appTheme = mItemSelect
            dialog.dismiss()
            if (initTheme != mItemSelect) restartApp()
        }
        val dialog = builder.create()
        dialog.setOnCancelListener { dialogInterface -> mItemSelect = 0 }
        dialog.setOnDismissListener { dialogInterface -> mItemSelect = 0 }
        dialog.show()
    }

    private fun restartApp() {
        if (activity != null) activity!!.recreate()
    }

    private fun initThemeColor() {
        if (themeUtil != null) {
            binding!!.colorPrefs.setViewResource(themeUtil!!.getIndicator(Prefs.getInstance(context).appThemeColor))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            restartApp()
        }
    }

    companion object {

        val TAG = "GeneralSettingsFragment"

        private val REQ_THEME = 1243

        fun newInstance(): GeneralSettingsFragment {
            return GeneralSettingsFragment()
        }
    }
}
