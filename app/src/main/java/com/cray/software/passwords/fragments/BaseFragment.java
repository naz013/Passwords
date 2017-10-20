package com.cray.software.passwords.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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

public class BaseFragment extends Fragment {

    @Nullable
    protected FragmentInterface anInterface;

    public void onFragmentResume() {
        if (anInterface != null) {
            anInterface.setClick(null);
            anInterface.setCurrent(this);
        }
    }

    public void onFragmentPause() {

    }

    public boolean canGoBack() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (anInterface == null) {
            anInterface = (FragmentInterface) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (anInterface == null) {
            anInterface = (FragmentInterface) activity;
        }
    }
}
