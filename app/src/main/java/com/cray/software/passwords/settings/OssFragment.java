package com.cray.software.passwords.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.FragmentOssBinding;
import com.cray.software.passwords.fragments.NestedFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class OssFragment extends NestedFragment {

    public static final String TAG = "OssFragment";

    private FragmentOssBinding binding;

    public static OssFragment newInstance() {
        return new OssFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOssBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(() -> {
            String text = readFile();
            binding.textView.post(() -> binding.textView.setText(text));
        }).start();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (anInterface != null) {
            anInterface.setTitle(getString(R.string.open_source_licenses));
        }
    }

    private String readFile(){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader input;
            input = new InputStreamReader(getActivity().getAssets().open("files/LICENSE.txt"));
            reader = new BufferedReader(input);

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }
}
