package com.cray.software.passwords.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cray.software.passwords.R;
import com.cray.software.passwords.cloud.DropboxLogin;
import com.cray.software.passwords.cloud.GoogleLogin;
import com.cray.software.passwords.databinding.FragmentCloudBinding;
import com.cray.software.passwords.fragments.NestedFragment;

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

public class CloudFragment extends NestedFragment implements GoogleLogin.LoginCallback, DropboxLogin.LoginCallback {

    public static final String TAG = "CloudFragment";

    private FragmentCloudBinding binding;
    private GoogleLogin mGoogleLogin;
    private DropboxLogin mDropboxLogin;

    public static CloudFragment newInstance() {
        return new CloudFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCloudBinding.inflate(inflater, container, false);

        if (getActivity() != null) mDropboxLogin = new DropboxLogin(getActivity(), this);
        mGoogleLogin = new GoogleLogin(getActivity(), this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.linkGDrive.setOnClickListener(view1 -> {
            if (mGoogleLogin.isLogged()) mGoogleLogin.logOut();
            else mGoogleLogin.login();
        });
        binding.linkDropbox.setOnClickListener(view12 -> mDropboxLogin.login());
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (anInterface != null) {
            anInterface.setClick(null);
            anInterface.setTitle(getString(R.string.cloud_drives_settings_title));
        }
        mDropboxLogin.checkDropboxStatus();
    }

    @Nullable
    @Override
    protected View getBgView() {
        return binding.bgView;
    }

    @Override
    public void onSuccess() {
        binding.linkGDrive.setText(getString(R.string.logout_button));
    }

    @Override
    public void onFail() {
        binding.linkGDrive.setText(getString(R.string.login_button));
    }

    @Override
    public void onSuccess(boolean logged) {
        if (logged) {
            binding.linkDropbox.setText(getString(R.string.logout_button));
        } else {
            binding.linkDropbox.setText(getString(R.string.login_button));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleLogin.onActivityResult(requestCode, resultCode, data);
    }
}
