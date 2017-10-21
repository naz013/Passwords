package com.cray.software.passwords.passwords;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.FragmentPasswordsBinding;
import com.cray.software.passwords.fragments.BaseFragment;
import com.cray.software.passwords.helpers.DataProvider;
import com.cray.software.passwords.helpers.ListInterface;
import com.cray.software.passwords.helpers.Utils;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.SimpleListener;
import com.cray.software.passwords.tasks.DeleteTask;
import com.cray.software.passwords.utils.Dialogues;
import com.cray.software.passwords.utils.Prefs;

/**
 * Copyright 2017 Nazar Suhovich
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

public class PasswordsFragment extends BaseFragment implements SimpleListener {

    public static final String TAG = "PasswordsFragment";

    private FragmentPasswordsBinding binding;
    private PasswordsRecyclerAdapter adapter;

    public PasswordsFragment() {
    }

    public static PasswordsFragment newInstance() {
        return new PasswordsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPasswordsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.currentList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        loaderAdapter();
        if (anInterface != null) {
            anInterface.setTitle(getString(R.string.passwords));
            anInterface.setClick(view -> startActivity(new Intent(getContext(), ManagePassword.class)));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_passwords, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order:
                showOrders();
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
        AlertDialog.Builder builder = Dialogues.getDialog(getContext());
        builder.setTitle(getString(R.string.menu_sort_title));
        builder.setItems(items, (dialog, item) -> {
            Prefs prefs = Prefs.getInstance(getContext());
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

    public void loaderAdapter() {
        adapter = new PasswordsRecyclerAdapter(DataProvider.getData(getContext(), true, false));
        adapter.setEventListener(this);
        binding.currentList.setAdapter(adapter);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (adapter.getItemCount() > 0) {
            binding.emptyItem.setVisibility(View.GONE);
        } else {
            binding.emptyItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int position, View view) {
        switch (view.getId()) {
            case R.id.more_button:
                showPopup(position, view);
                break;
            default:
                edit(position);
                break;
        }
    }

    private void showPopup(int position, View view) {
        ListInterface el = adapter.getItem(position);
        if (el instanceof PasswordListInterface) {
            final String[] items = {
                    getString(R.string.copy_login),
                    getString(R.string.copy_password),
                    getString(R.string.edit),
                    getString(R.string.delete),
                    getString(R.string.delete_with_backup)
            };
            Utils.showLCAM(getContext(), view, item -> {
                switch (item) {
                    case 0:
                        copyText(getString(R.string.item_login), ((PasswordListInterface) el).getLogin());
                        break;
                    case 1:
                        copyText(getString(R.string.item_password), ((PasswordListInterface) el).getPassword());
                        break;
                    case 2:
                        edit(position);
                        break;
                    case 3:
                        delete(position, false);
                        break;
                    case 4:
                        delete(position, true);
                        break;
                }
            }, items);
        }
    }

    private void copyText(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), label + " " + getString(R.string.copied), Toast.LENGTH_SHORT).show();
        }
    }

    private void edit(int position) {
        ListInterface item = adapter.getItem(position);
        long id = item.getId();
        if (item instanceof PasswordListInterface) {
            Intent intentId = new Intent(getContext(), ManagePassword.class);
            intentId.putExtra(Constants.INTENT_ID, id);
            startActivity(intentId);
        }
    }

    private void delete(int position, boolean deleteFile) {
        ListInterface item = adapter.getItem(position);
        long del = item.getId();
        if (item instanceof PasswordListInterface) {
            new DeleteTask(getContext(), null).execute(del, deleteFile ? 1L : 0L);
        }
        adapter.remove(position);
        updateEmptyView();
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }
}
