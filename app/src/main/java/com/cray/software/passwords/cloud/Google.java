package com.cray.software.passwords.cloud;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.utils.LogUtil;
import com.cray.software.passwords.utils.Prefs;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

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

public class Google {

    private static final String TAG = "Google";
    private static final String APPLICATION_NAME = "Passwords/2.0";
    private static final String FOLDER_NAME = "Passwords";

    private Drive driveService;
    private Drives mDrives;

    private static Google instance = null;

    private Google(Context context) throws IllegalStateException {
        String user = Prefs.getInstance(context).getDriveUser();
        if (user.matches(".*@.*")) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Collections.singletonList(DriveScopes.DRIVE));
            credential.setSelectedAccountName(user);
            JsonFactory mJsonFactory = GsonFactory.getDefaultInstance();
            HttpTransport mTransport = AndroidHttp.newCompatibleTransport();
            driveService = new Drive.Builder(mTransport, mJsonFactory, credential).setApplicationName(APPLICATION_NAME).build();
            mDrives = new Drives();
        } else {
            logOut();
            throw new IllegalArgumentException("Not logged to Google");
        }
    }

    void logOut() {
        Prefs.getInstance().setDriveUser(Prefs.DRIVE_USER_NONE);
        instance = null;
    }

    @Nullable
    public static Google getInstance(Context context) {
        try {
            instance = new Google(context.getApplicationContext());
        } catch (IllegalArgumentException | NullPointerException e) {
            LogUtil.d(TAG, "getInstance: " + e.getLocalizedMessage());
        }
        return instance;
    }

    @Nullable
    public Drives getDrive() {
        return mDrives;
    }

    public class Drives {

        /**
         * Count all backup files stored on Google Drive.
         *
         * @return number of files in local folder.
         */
        public int countFiles() throws IOException {
            int count = 0;
            Drive.Files.List request = driveService.files().list().setQ("mimeType = 'text/plain'").setFields("nextPageToken, files");
            do {
                FileList files = request.execute();
                ArrayList<File> fileList = (ArrayList<File>) files.getFiles();
                for (File f : fileList) {
                    String title = f.getName();
                    if (title.contains(Constants.FILE_EXTENSION)) {
                        count++;
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
            return count;
        }

        private void removeAllCopies(String fileName) throws IOException {
            Drive.Files.List request = driveService.files().list()
                    .setQ("mimeType = 'text/plain' and name contains '" + fileName + "'")
                    .setFields("nextPageToken, files");
            do {
                FileList files = request.execute();
                ArrayList<File> fileList = (ArrayList<File>) files.getFiles();
                for (File f : fileList) {
                    driveService.files().delete(f.getId()).execute();
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }

        /**
         * Upload file from folder to Google Drive.
         *
         * @param metadata metadata.
         * @throws IOException
         */
        private void saveFileToDrive(@NonNull String pathToFile, Metadata metadata) throws IOException {
            if (metadata.getFolder() == null) return;
            java.io.File[] files = metadata.getFolder().listFiles();
            if (files == null) return;
            String folderId = null;
            try {
                folderId = getFolderId();
            } catch (IllegalArgumentException ignored) {
            }
            if (folderId == null) {
                return;
            }
            java.io.File f = new java.io.File(pathToFile);
            if (!f.exists()) {
                return;
            }
            if (!f.getName().endsWith(metadata.getFileExt())) return;
            removeAllCopies(f.getName());
            File fileMetadata = new File();
            fileMetadata.setName(f.getName());
            fileMetadata.setDescription(metadata.getMeta());
            fileMetadata.setParents(Collections.singletonList(folderId));
            FileContent mediaContent = new FileContent("text/plain", f);
            driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        }

        public void download(boolean deleteBackup, Metadata metadata) throws IOException {
            java.io.File folder = metadata.getFolder();
            if (!folder.exists() && !folder.mkdirs()) {
                return;
            }
            Drive.Files.List request = driveService.files().list()
                    .setQ("mimeType = 'text/plain' and name contains '" + metadata.getFileExt() + "'")
                    .setFields("nextPageToken, files");
            do {
                FileList files = request.execute();
                ArrayList<File> fileList = (ArrayList<File>) files.getFiles();
                for (File f : fileList) {
                    String title = f.getName();
                    if (title.endsWith(metadata.getFileExt())) {
                        java.io.File file = new java.io.File(folder, title);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        OutputStream out = new FileOutputStream(file);
                        driveService.files().get(f.getId()).executeMediaAndDownloadTo(out);
                        if (metadata.action != null) {
                            metadata.action.onSave(file);
                        }
                        if (deleteBackup) {
                            if (file.exists()) {
                                file.delete();
                            }
                            driveService.files().delete(f.getId()).execute();
                        }
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }

        public void saveFileToDrive() throws IOException {
            String folderId = getFolderId();
            if (folderId == null){
                com.google.api.services.drive.model.File destFolder = createFolder();
                folderId = destFolder.getId();
            }

            java.io.File sdPath = Environment.getExternalStorageDirectory();
            java.io.File sdPathDr = new java.io.File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            java.io.File[] files = sdPathDr.listFiles();
            for (java.io.File file : files) {
                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setName(file.getName());
                fileMetadata.setDescription("Passwords Backup");
                fileMetadata.setParents(Collections.singletonList(folderId));
                FileContent mediaContent = new FileContent("text/plain", file);

                deleteFile(file.getName());

                driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
            }
        }

        public void loadFileFromDrive() throws IOException {
            java.io.File sdPath = Environment.getExternalStorageDirectory();
            java.io.File sdPathDr = new java.io.File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP);
            //deleteFolders();
            Drive.Files.List request;
            try {
                request = driveService.files().list().setQ("mimeType = 'text/plain'"); // .setQ("mimeType=\"text/plain\"");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            do {
                FileList files;
                try {
                    files = request.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ArrayList<com.google.api.services.drive.model.File> fileList =
                        (ArrayList<com.google.api.services.drive.model.File>) files.getFiles();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String title = f.getName();
                    if (!sdPathDr.exists() && !sdPathDr.mkdirs()) {
                        throw new IOException("Unable to create parent directory");
                    }

                    if (title.endsWith(Constants.FILE_EXTENSION)) {
                        java.io.File file = new java.io.File(sdPathDr, title);
                        if (!file.exists()) {
                            try {
                                file.createNewFile(); //otherwise dropbox client will fail silently
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        OutputStream out = new FileOutputStream(file);
                        driveService.files().get(f.getId()).executeMediaAndDownloadTo(out);
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }

        public void deleteFile (String title){
            Drive.Files.List request = null;
            try {
                request = driveService.files().list().setQ("mimeType = 'text/plain'");
            } catch (IOException e) {
                e.printStackTrace();
            }
            do {
                FileList files;
                try {
                    files = request.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                ArrayList<com.google.api.services.drive.model.File> fileList =
                        (ArrayList<com.google.api.services.drive.model.File>) files.getFiles();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String fileTitle = f.getName();

                    if (fileTitle.endsWith(Constants.FILE_EXTENSION) && fileTitle.contains(title)) {
                        try {
                            Log.d(Constants.LOG_TAG, "file deleted " + fileTitle);
                            driveService.files().delete(f.getId()).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }

        /**
         * Delete application folder from Google Drive.
         */
        public void clean() throws IOException {
            Drive.Files.List request = driveService.files().list()
                    .setQ("mimeType = 'application/vnd.google-apps.folder' and name contains '" + FOLDER_NAME + "'");
            if (request == null) return;
            do {
                FileList files = request.execute();
                ArrayList<File> fileList = (ArrayList<File>) files.getFiles();
                for (File f : fileList) {
                    String fileMIME = f.getMimeType();
                    if (fileMIME.contains("application/vnd.google-apps.folder") && f.getName().contains(FOLDER_NAME)) {
                        driveService.files().delete(f.getId()).execute();
                        break;
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }

        /**
         * Remove all backup files from app folder.
         *
         * @throws IOException
         */
        public void cleanFolder() throws IOException {
            Drive.Files.List request = driveService.files().list()
                    .setQ("mimeType = 'text/plain' and (name contains '" + Constants.FILE_EXTENSION + "')");
            if (request == null) return;
            do {
                FileList files = request.execute();
                ArrayList<File> fileList = (ArrayList<File>) files.getFiles();
                for (File f : fileList) {
                    driveService.files().delete(f.getId()).execute();
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }

        /**
         * Holder application folder identifier on Google Drive.
         *
         * @return Drive folder identifier.
         */
        private String getFolderId() throws IOException, IllegalArgumentException {
            Drive.Files.List request = driveService.files().list()
                    .setQ("mimeType = 'application/vnd.google-apps.folder' and name contains '" + FOLDER_NAME + "'");
            if (request == null) return null;
            do {
                FileList files = request.execute();
                if (files == null) return null;
                ArrayList<File> fileList = (ArrayList<File>) files.getFiles();
                for (File f : fileList) {
                    String fileMIME = f.getMimeType();
                    if (fileMIME.trim().contains("application/vnd.google-apps.folder") &&
                            f.getName().contains(FOLDER_NAME)) {
                        LogUtil.d(TAG, "getFolderId: " + f.getName() + ", " + f.getMimeType());
                        return f.getId();
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
            File file = createFolder();
            return file != null ? file.getId() : null;
        }

        /**
         * Create application folder on Google Drive.
         *
         * @return Drive folder
         * @throws IOException
         */
        private File createFolder() throws IOException {
            File folder = new File();
            folder.setName(FOLDER_NAME);
            folder.setMimeType("application/vnd.google-apps.folder");
            Drive.Files.Create folderInsert = driveService.files().create(folder);
            return folderInsert != null ? folderInsert.execute() : null;
        }

        private class Metadata {

            private String fileExt;
            private java.io.File folder;
            private String meta;
            private Action action;

            Metadata(String fileExt, java.io.File folder, String meta, Action action) {
                this.fileExt = fileExt;
                this.folder = folder;
                this.meta = meta;
                this.action = action;
            }

            public Action getAction() {
                return action;
            }

            String getFileExt() {
                return fileExt;
            }

            java.io.File getFolder() {
                return folder;
            }

            String getMeta() {
                return meta;
            }
        }

    }

    private interface Action {
        void onSave(java.io.File file);
    }
}
