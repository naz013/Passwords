package com.cray.software.passwords.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;

/**
 * Copyright 2016 Nazar Suhovich
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

public final class Prefs extends SharedPrefs {

    private static final String TAG = "Prefs";
    public static final String DRIVE_USER_NONE = "none";

    private static Prefs instance = null;

    private Prefs(Context context) {
        super(context);
    }

    public static Prefs getInstance() {
        if (instance != null) {
            return instance;
        }
        throw new IllegalArgumentException("Use Prefs(Context context) constructor!");
    }

    public static Prefs getInstance(Context context) {
        if (instance == null) {
            synchronized (Prefs.class) {
                if (instance == null) {
                    instance = new Prefs(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public boolean hasSecureKey(String checkString) {
        return prefsPassword.contains(checkString);
    }

    public void setRestoreWord(String value){
        prefsPassword.edit().putString(KEYWORD, value).apply();
    }

    public String getRestoreWord(){
        return prefsPassword.getString(KEYWORD, null);
    }

    public void savePassPrefs(String value){
        prefsPassword.edit().putString(LOGIN_PASSCODE, value).apply();
    }

    @NonNull
    public String loadPassPrefs(){
        return prefsPassword.getString(LOGIN_PASSCODE, "1111");
    }

    public boolean isPassString(){
        return prefsPassword.contains(LOGIN_PASSCODE);
    }

    public void saveSystemPrefs(String key, String value){
        prefsPassword.edit().putString(key, value).apply();
    }

    public String loadSystemPrefs(String key){
        return prefsPassword.getString(key, Constants.DRIVE_USER_NONE);
    }

    public boolean isSystemKey(String key){
        return prefsPassword.contains(key);
    }

    public String getDriveUser() {
        return SuperUtil.decrypt(getString(DRIVE_USER));
    }

    public void setDriveUser(String value) {
        putString(DRIVE_USER, SuperUtil.encrypt(value));
    }

    public boolean isRateShowed() {
        return getBoolean(RATE_SHOW);
    }

    public void setRateShowed(boolean value) {
        putBoolean(RATE_SHOW, value);
    }

    public boolean isDeleteBackFileEnabled() {
        return getBoolean(DELETE_BACKUP);
    }

    public void setDeleteBackFileEnabled(boolean value) {
        putBoolean(DELETE_BACKUP, value);
    }

    public boolean isAutoBackupEnabled() {
        return getBoolean(AUTO_BACKUP);
    }

    public void setAutoBackupEnabled(boolean value) {
        putBoolean(AUTO_BACKUP, value);
    }

    public boolean isAutoSyncEnabled() {
        return getBoolean(AUTO_SYNC);
    }

    public void setAutoSyncEnabled(boolean value) {
        putBoolean(AUTO_SYNC, value);
    }

    public int getAppThemeColor() {
        return getInt(APP_THEME_COLOR);
    }

    public void setAppThemeColor(int value) {
        putInt(APP_THEME_COLOR, value);
    }

    public int getAppTheme() {
        return getInt(APP_THEME);
    }

    public void setAppTheme(int value) {
        putInt(APP_THEME, value);
    }

    public int getPasswordLength() {
        return getInt(PASSWORD_LENGTH);
    }

    public void setPasswordLength(int value) {
        putInt(PASSWORD_LENGTH, value);
    }

    public int getOldPasswordLength() {
        return getInt(PASSWORD_OLD_LENGTH);
    }

    public void setOldPasswordLength(int value) {
        putInt(PASSWORD_OLD_LENGTH, value);
    }

    public String getOrderBy() {
        return getString(ORDER_BY, Constants.ORDER_DATE_A_Z);
    }

    public void setOrderBy(String value) {
        putString(ORDER_BY, value);
    }

    public void setRunsCount(int value) {
        putInt(APP_RUNS_COUNT, value);
    }

    public int getRunsCount() {
        return getInt(APP_RUNS_COUNT);
    }

    public void checkPrefs() {
        if (!hasKey(APP_THEME_COLOR)) {
            putInt(APP_THEME_COLOR, 3);
        }
        if (!hasKey(APP_THEME)) {
            putInt(APP_THEME, ThemeUtil.THEME_AUTO);
        }
        if (!hasKey(PASSWORD_LENGTH)) {
            putInt(PASSWORD_LENGTH, 4);
        }
        if (!hasKey(DELETE_BACKUP)) {
            putBoolean(DELETE_BACKUP, false);
        }
        if (Module.isPro()) {
            if (!hasKey(AUTO_SYNC)) {
                putBoolean(AUTO_SYNC, false);
            }
            if (!hasKey(AUTO_BACKUP)) {
                putBoolean(AUTO_BACKUP, false);
            }
        }
    }

    public String getDropboxUid() {
        return getString(DROPBOX_UID);
    }

    public void setDropboxUid(String uid) {
        putString(DROPBOX_UID, uid);
    }

    public String getDropboxToken() {
        return getString(DROPBOX_TOKEN);
    }

    public void setDropboxToken(String token) {
        putString(DROPBOX_TOKEN, token);
    }
}
