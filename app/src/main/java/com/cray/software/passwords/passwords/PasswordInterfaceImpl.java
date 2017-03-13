package com.cray.software.passwords.passwords;

import com.cray.software.passwords.helpers.Crypter;
import com.cray.software.passwords.helpers.TImeUtils;

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

public class PasswordInterfaceImpl implements PasswordListInterface {

    private Password mPassword;

    public PasswordInterfaceImpl(Password password) {
        this.mPassword = password;
    }

    @Override
    public int getColor() {
        return mPassword.getColor();
    }

    @Override
    public long getId() {
        return mPassword.getId();
    }

    @Override
    public int getViewType() {
        return PasswordsRecyclerAdapter.PASSWORD;
    }

    @Override
    public String getTitle() {
        return Crypter.decrypt(mPassword.getTitle());
    }

    @Override
    public String getLogin() {
        return DataProvider.getStarred(Crypter.decrypt(mPassword.getLogin()));
    }

    @Override
    public String getDate() {
        return TImeUtils.getDateFromGmt(Crypter.decrypt(mPassword.getDate()));
    }
}
