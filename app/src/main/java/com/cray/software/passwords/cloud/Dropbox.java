package com.cray.software.passwords.cloud;

import android.content.Context;
import android.os.Environment;

import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.utils.LogUtil;
import com.cray.software.passwords.utils.Prefs;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.SpaceUsage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;

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

public class Dropbox {

    private static final String TAG = "Dropbox";
    private static final String APP_KEY = "4zi1d414h0v8sxe";

    private Context mContext;

    private DbxClientV2 mDBApi;

    public Dropbox(Context context) {
        this.mContext = context;
    }

    /**
     * Start connection to Dropbox.
     */
    public void startSession() {
        String token = Prefs.getInstance(mContext).getDropboxToken();
        if (token == null) {
            token = Auth.getOAuth2Token();
            Prefs.getInstance(mContext).setDropboxToken(token);
        }
        LogUtil.d(TAG, "startSession: " + token);
        if (token == null) {
            Prefs.getInstance(mContext).setDropboxToken(null);
            return;
        }
        DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("Just Reminder")
                .withHttpRequestor(new OkHttp3Requestor(new OkHttpClient()))
                .build();

        mDBApi = new DbxClientV2(requestConfig, token);
    }

    /**
     * Check if user has already connected to Dropbox from this application.
     *
     * @return Boolean
     */
    public boolean isLinked() {
        return mDBApi != null && Prefs.getInstance(mContext).getDropboxToken() != null;
    }

    /**
     * Holder Dropbox user name.
     *
     * @return String user name
     */
    public String userName() {
        FullAccount account = null;
        try {
            account = mDBApi.users().getCurrentAccount();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return account != null ? account.getName().getDisplayName() : null;
    }

    /**
     * Holder user all apace on Dropbox.
     *
     * @return Long - user quota
     */
    public long userQuota() {
        SpaceUsage account = null;
        try {
            account = mDBApi.users().getSpaceUsage();
        } catch (DbxException e) {
            LogUtil.e(TAG, "userQuota: ", e);
        }
        return account != null ? account.getAllocation().getIndividualValue().getAllocated() : 0;
    }

    public long userQuotaNormal() {
        SpaceUsage account = null;
        try {
            account = mDBApi.users().getSpaceUsage();
        } catch (DbxException e) {
            LogUtil.e(TAG, "userQuotaNormal: ", e);
        }
        return account != null ? account.getUsed() : 0;
    }

    public void startLink() {
        Auth.startOAuth2Authentication(mContext, APP_KEY);
    }

    public boolean unlink() {
        boolean is = false;
        if (logOut()) {
            is = true;
        }
        return is;
    }

    private boolean logOut() {
        clearKeys();
        return true;
    }

    private void clearKeys() {
        Prefs.getInstance(mContext).setDropboxToken(null);
        Prefs.getInstance(mContext).setDropboxUid(null);
    }

    /**
     * Upload to Dropbox folder backup files from selected folder on SD Card.
     */
    public void uploadToCloud(String fileName) {
        startSession();
        if (!isLinked()) {
            return;
        }
        if (fileName != null) {
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            if (sdPathDr.exists()) {
                String fileLoc = sdPathDr.toString();
                File tmpFile = new File(fileLoc, fileName);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(tmpFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    String filePath = "/" + Constants.DIR_DBX + tmpFile.getName();
                    mDBApi.files().uploadBuilder(filePath)
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(fis);
                } catch (DbxException | IOException e) {
                    LogUtil.e(TAG, "Something went wrong while uploading.", e);
                }
            }
        } else {
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            File[] files = sdPathDr.listFiles();
            String fileLoc = sdPathDr.toString();
            if (files == null) {
                return;
            }
            for (File file : files) {
                String fileLoopName = file.getName();
                File tmpFile = new File(fileLoc, fileLoopName);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(tmpFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    String filePath = "/" + Constants.DIR_DBX + fileLoopName;
                    mDBApi.files().uploadBuilder(filePath)
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(fis);
                } catch (DbxException | IOException e) {
                    LogUtil.e(TAG, "Something went wrong while uploading.", e);
                }
            }
        }
    }

    /**
     * Delete reminder backup file from Dropbox folder.
     *
     * @param name file name.
     */
    public void deleteFile(String name) {
        LogUtil.d(TAG, "deleteFile: " + name);
        startSession();
        if (!isLinked()) {
            return;
        }
        try {
            mDBApi.files().delete("/" + Constants.DIR_DBX + name);
        } catch (DbxException e) {
            LogUtil.e(TAG, "deleteFile: ", e);
        }
    }

    /**
     * Delete all folders inside application folder on Dropbox.
     */
    public void cleanFolder() {
        startSession();
        if (!isLinked()) {
            return;
        }
        deleteFolder("/" + Constants.DIR_DBX);
    }

    private void deleteFolder(String folder) {
        try {
            mDBApi.files().delete(folder);
        } catch (DbxException e) {
            LogUtil.e(TAG, "deleteFolder: ", e);
        }
    }

    /**
     * Download on SD Card all template backup files found on Dropbox.
     */
    public void downloadFiles() {
        File sdPath = Environment.getExternalStorageDirectory();
        File dir = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP);
        startSession();
        if (!isLinked()) {
            return;
        }
        try {
            ListFolderResult result = mDBApi.files().listFolder("/" + Constants.DIR_DBX);
            if (result == null) {
                return;
            }
            for (Metadata e : result.getEntries()) {
                String fileName = e.getName();
                File localFile = new File(dir + "/" + fileName);
                String cloudFile = "/" + Constants.DIR_DBX + fileName;
                downloadFile(localFile, cloudFile);
            }
        } catch (DbxException | IllegalStateException e) {
            LogUtil.e(TAG, "downloadFiles: ", e);
        }
    }

    private void downloadFile(File localFile, String cloudFile) {
        try {
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(localFile);
            mDBApi.files().download(cloudFile).download(outputStream);
        } catch (DbxException | IOException e1) {
            LogUtil.e(TAG, "downloadFile: ", e1);
        }
    }

    /**
     * Count all reminder backup files in Dropbox folder.
     *
     * @return number of found backup files.
     */
    public int countFiles() {
        int count = 0;
        startSession();
        if (!isLinked()) {
            return 0;
        }
        try {
            ListFolderResult result = mDBApi.files().listFolder("/");
            if (result == null) {
                return 0;
            }
        } catch (DbxException e) {
            LogUtil.e(TAG, "countFiles: ", e);
        }
        return count;
    }
}
