package com.cray.software.passwords.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.FragmentGeneralSettingsBinding;
import com.cray.software.passwords.dialogs.ThemerDialog;
import com.cray.software.passwords.fragments.NestedFragment;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.ThemeUtil;

public class GeneralSettingsFragment extends NestedFragment {

    public static final String TAG = "GeneralSettingsFragment";

    private static final int REQ_THEME = 1243;

    private FragmentGeneralSettingsBinding binding;
    private int mItemSelect;

    public static GeneralSettingsFragment newInstance() {
        return new GeneralSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGeneralSettingsBinding.inflate(inflater, container, false);

        binding.colorPrefs.setOnClickListener(view -> startActivityForResult(new Intent(getActivity(), ThemerDialog.class), REQ_THEME));
        initAppTheme();

        return binding.getRoot();
    }

    private void initAppTheme() {
        binding.themePrefs.setDetailText(getCurrentTheme());
        binding.themePrefs.setOnClickListener(view -> showThemeDialog());
    }

    private String getCurrentTheme() {
        int theme = Prefs.getInstance(getContext()).getAppTheme();
        if (theme == ThemeUtil.THEME_AUTO) return getString(R.string.auto);
        else if (theme == ThemeUtil.THEME_WHITE) return getString(R.string.light);
        else if (theme == ThemeUtil.THEME_AMOLED) return getString(R.string.amoled);
        else return getString(R.string.dark);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        initThemeColor();
        if (anInterface != null) {
            anInterface.setTitle(getString(R.string.interface_block));
        }
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }

    private void showThemeDialog() {
        AlertDialog.Builder builder = Dialogues.getDialog(getContext());
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.theme));
        String[] colors = new String[]{getString(R.string.auto), getString(R.string.light), getString(R.string.dark), getString(R.string.amoled)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_single_choice, colors);
        int initTheme = Prefs.getInstance(getContext()).getAppTheme();
        mItemSelect = initTheme;
        builder.setSingleChoiceItems(adapter, mItemSelect, (dialog, which) -> mItemSelect = which);
        builder.setPositiveButton(getString(R.string.button_ok), (dialog, which) -> {
            Prefs.getInstance(getContext()).setAppTheme(mItemSelect);
            dialog.dismiss();
            if (initTheme != mItemSelect) restartApp();
        });
        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(dialogInterface -> mItemSelect = 0);
        dialog.setOnDismissListener(dialogInterface -> mItemSelect = 0);
        dialog.show();
    }

    private void restartApp() {
        getActivity().recreate();
    }

    private void initThemeColor() {
        binding.colorPrefs.setViewResource(ThemeUtil.getInstance(getContext()).getIndicator(Prefs.getInstance(getContext()).getAppThemeColor()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            restartApp();
        }
    }
}
