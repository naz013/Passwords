package com.cray.software.passwords.cloud;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GDriveHelper {

    Context ctx;
    SharedPrefs prefs;

    private final HttpTransport m_transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory m_jsonFactory = GsonFactory.getDefaultInstance();
    private com.google.api.services.drive.Drive m_client;
    private static final String APPLICATION_NAME = "Passwords/1.1.1";

    public GDriveHelper(Context context){
        this.ctx = context;
    }

    public void authorize(String accountName){
        GoogleAccountCredential m_credential = GoogleAccountCredential.usingOAuth2(ctx, Collections.singleton(DriveScopes.DRIVE));
        m_credential.setSelectedAccountName(accountName);
        m_client = new com.google.api.services.drive.Drive.Builder(
                m_transport, m_jsonFactory, m_credential).setApplicationName(APPLICATION_NAME)
                .build();
    }

    public boolean isLinked(){
        prefs = new SharedPrefs(ctx);
        return prefs.isSystemKey(Constants.NEW_PREFERENCES_DRIVE_USER) && !prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER).matches(Constants.DRIVE_USER_NONE);
    }

    public void unlink(){
        prefs.saveSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER, Constants.DRIVE_USER_NONE);
    }

    public int countFiles(){
        authorize(prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER));
        int i = 0;
        Drive.Files.List request = null;
        try {
            request = m_client.files().list().setQ("mimeType = 'text/plain'");
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
            ArrayList<com.google.api.services.drive.model.File> fileList = (ArrayList<com.google.api.services.drive.model.File>) files.getItems();
            for (com.google.api.services.drive.model.File f : fileList) {
                String fileTitle = f.getTitle();

                if (fileTitle.trim().endsWith(Constants.FILE_EXTENSION)) {
                    i += 1;
                }
            }
            request.setPageToken(files.getNextPageToken());
        } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        return i;
    }

    public void saveFileToDrive() throws IOException {
        if (isLinked()) {
            prefs = new SharedPrefs(ctx);
            authorize(prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER));
            String folderId = getFolderId();
            if (folderId == null){
                com.google.api.services.drive.model.File destFolder = createFolder();
                folderId = destFolder.getId();
            }

            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            File[] files = sdPathDr.listFiles();
            for (File file : files) {
                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setTitle(file.getName());
                fileMetadata.setDescription("Passwords Backup");
                fileMetadata.setParents(Arrays.asList(new ParentReference().setId(folderId)));

                FileContent mediaContent = new FileContent("text/plain", file);

                deleteFile(file.getName());

                com.google.api.services.drive.Drive.Files.Insert insert = m_client.files().insert(fileMetadata, mediaContent);
                MediaHttpUploader uploader = insert.getMediaHttpUploader();
                uploader.setDirectUploadEnabled(true);
                insert.execute();
            }
        }
    }

    public void loadFileFromDrive() throws IOException {
        if (isLinked()) {
            prefs = new SharedPrefs(ctx);
            authorize(prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER));
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP);
            //deleteFolders();
            Drive.Files.List request;
            try {
                request = m_client.files().list().setQ("mimeType = 'text/plain'"); // .setQ("mimeType=\"text/plain\"");
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
                ArrayList<com.google.api.services.drive.model.File> fileList = (ArrayList<com.google.api.services.drive.model.File>) files.getItems();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String title = f.getTitle();
                    if (!sdPathDr.exists() && !sdPathDr.mkdirs()) {
                        throw new IOException("Unable to create parent directory");
                    }

                    if (title.endsWith(Constants.FILE_EXTENSION)) {
                        File file = new File(sdPathDr, title);
                        if (!file.exists()) {
                            try {
                                file.createNewFile(); //otherwise dropbox client will fail silently
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        OutputStream out = new FileOutputStream(file);

                        MediaHttpDownloader downloader =
                                new MediaHttpDownloader(m_transport, m_client.getRequestFactory().getInitializer());
                        downloader.setDirectDownloadEnabled(true);
                        downloader.download(new GenericUrl(f.getDownloadUrl()), out);
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }
    }

    public void deleteFile (String title){
        if (isLinked()) {
            prefs = new SharedPrefs(ctx);
            authorize(prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER));
            Drive.Files.List request = null;
            try {
                request = m_client.files().list().setQ("mimeType = 'text/plain'");
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
                ArrayList<com.google.api.services.drive.model.File> fileList = (ArrayList<com.google.api.services.drive.model.File>) files.getItems();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String fileTitle = f.getTitle();

                    if (fileTitle.endsWith(Constants.FILE_EXTENSION) && fileTitle.contains(title)) {
                        try {
                            Log.d(Constants.LOG_TAG, "file deleted " + fileTitle);
                            m_client.files().delete(f.getId()).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }
    }

    public void clean(){
        if (isLinked()) {
            prefs = new SharedPrefs(ctx);
            authorize(prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER));
            Drive.Files.List request = null;
            try {
                request = m_client.files().list().setQ("mimeType = 'text/plain'");
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
                ArrayList<com.google.api.services.drive.model.File> fileList = (ArrayList<com.google.api.services.drive.model.File>) files.getItems();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String fileTitle = f.getTitle();

                    if (fileTitle.trim().endsWith(Constants.FILE_EXTENSION)) {
                        try {
                            m_client.files().delete(f.getId()).execute();
                            Log.d(Constants.LOG_TAG, "file deleted " + f.getTitle());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);

            Drive.Files.List requestF = null;
            try {
                requestF = m_client.files().list().setQ("mimeType = 'application/vnd.google-apps.folder'");
            } catch (IOException e) {
                e.printStackTrace();
            }
            do {
                FileList files;
                try {
                    files = requestF.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                ArrayList<com.google.api.services.drive.model.File> fileList = (ArrayList<com.google.api.services.drive.model.File>) files.getItems();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String fileMIME = f.getMimeType();

                    if (fileMIME.matches("application/vnd.google-apps.folder") && f.getTitle().matches("Passwords")) {
                        try {
                            m_client.files().delete(f.getId()).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(Constants.LOG_TAG, "deleted folder - " + f.getTitle());
                        break;
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }
    }

    private String getFolderId(){
        String id = null;
        Drive.Files.List request = null;
        try {
            request = m_client.files().list().setQ("mimeType = 'application/vnd.google-apps.folder'");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (request != null) {
            do {
                FileList files = null;
                try {
                    files = request.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    id = null;
                }
                if (files != null) {
                    ArrayList<com.google.api.services.drive.model.File> fileList = (ArrayList<com.google.api.services.drive.model.File>) files.getItems();
                    for (com.google.api.services.drive.model.File f : fileList) {
                        String fileMIME = f.getMimeType();

                        if (fileMIME.trim().matches("application/vnd.google-apps.folder") && f.getTitle().matches("Passwords")) {
                            id = f.getId();
                        }
                    }
                    request.setPageToken(files.getNextPageToken());
                } else id = null;
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        } else id = null;
        return id;
    }

    private com.google.api.services.drive.model.File createFolder() throws IOException {
        com.google.api.services.drive.model.File folder = new com.google.api.services.drive.model.File();
        folder.setTitle("Passwords");
        folder.setMimeType("application/vnd.google-apps.folder");
        com.google.api.services.drive.Drive.Files.Insert folderInsert = null;
        try {
            folderInsert = m_client.files().insert(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderInsert.execute();
    }
}
